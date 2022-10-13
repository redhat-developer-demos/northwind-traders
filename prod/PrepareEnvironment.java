///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 17+
//DEPS io.fabric8:openshift-client:6.1.1
//DEPS org.apache.commons:commons-compress:1.21

import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.kubernetes.client.dsl.ExecListener;
import io.fabric8.openshift.api.model.Route;
import io.fabric8.openshift.api.model.RouteBuilder;
import io.fabric8.openshift.client.OpenShiftClient;

import java.io.ByteArrayOutputStream;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class PrepareEnvironment {

  private static final String APP = "app";
  private static final String GROUP = "group";
  private static final String RABBIT_MQ = "rabbit-mq";
  private static final String RABBIT_MQ_MANAGEMENT = "rabbit-mq-management";
  private static final String POSTGRESQL = "postgresql";
  private static final String USER = "jkube";
  private static final String PASSWORD = "pa33word";
  private static final String ECLIPSECON_2022 = "eclipsecon-2022";

  public static void main(String... args) {
    try (var kc = new KubernetesClientBuilder().build()) {
      deployRabbitMq(kc);
      deployPostgreSql(kc);
    }
  }

  private static void deployPostgreSql(KubernetesClient kc) {
    kc.apps().deployments().withName(POSTGRESQL).delete();
    kc.apps().deployments().withName(POSTGRESQL).waitUntilCondition(Objects::isNull, 10, TimeUnit.SECONDS);
    kc.pods().withLabel(APP, POSTGRESQL).withLabel(GROUP, ECLIPSECON_2022).withGracePeriod(0L).delete();
    kc.pods().withLabel(APP, POSTGRESQL).withLabel(GROUP, ECLIPSECON_2022)
      .waitUntilCondition(Objects::isNull, 10, TimeUnit.SECONDS);
    final var postgresDeployment = new DeploymentBuilder()
      .withNewMetadata()
      .withName(POSTGRESQL)
      .addToLabels(APP, POSTGRESQL)
      .addToLabels(GROUP, ECLIPSECON_2022)
      .endMetadata()
      .withNewSpec()
      .withReplicas(1)
      .withNewSelector()
      .addToMatchLabels(APP, POSTGRESQL)
      .addToMatchLabels(GROUP, ECLIPSECON_2022)
      .endSelector()
      .withNewTemplate()
      .withNewMetadata()
      .addToLabels(APP, POSTGRESQL)
      .addToLabels(GROUP, ECLIPSECON_2022)
      .endMetadata()
      .withNewSpec()
      .addNewContainer()
      .withName(POSTGRESQL)
      .withImage("bitnami/postgresql:14.5.0")
      .addToEnv(new EnvVar("POSTGRESQL_USERNAME", USER, null))
      .addToEnv(new EnvVar("POSTGRESQL_PASSWORD", PASSWORD, null))
      .addNewPort().withContainerPort(5432).endPort()
      .withNewReadinessProbe()
      .withNewExec()
      .withCommand("/bin/sh", "-c", "pg_isready -U ${POSTGRESQL_USERNAME}")
      .endExec()
      .endReadinessProbe()
      .endContainer()
      .endSpec()
      .endTemplate()
      .endSpec()
      .build();
    final var postgresService = service(POSTGRESQL, POSTGRESQL, 5432);
    Stream.of(postgresDeployment, postgresService).forEach(s -> kc.resource(s).createOrReplace());
    var pod = kc.pods().withLabel(APP, POSTGRESQL).withLabel(GROUP, ECLIPSECON_2022)
      .waitUntilReady(1, TimeUnit.MINUTES);
    kc.pods().resource(pod).file("/tmp/northwind.sql").upload(Path.of("northwind.sql"));
    try {
      final var baos = new ByteArrayOutputStream();
      kc.pods().resource(pod)
        .writingOutput(baos) // Seems there's an error in the client, it requires this variable
        .writingError(System.err)
        .withTTY()
        .exec(
          "sh", "-c",
          "export PGPASSWORD=${POSTGRESQL_PASSWORD} && " +
            "psql -U jkube -d postgres -w -c \"CREATE DATABASE northwind\" && " +
            "psql -U jkube -d northwind -w -f /tmp/northwind.sql"
        ).exitCode().get(1, TimeUnit.MINUTES);
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  private static void deployRabbitMq(KubernetesClient kc) {
    kc.apps().deployments().withName(RABBIT_MQ).delete();
    kc.apps().deployments().withName(RABBIT_MQ).waitUntilCondition(Objects::isNull, 10, TimeUnit.SECONDS);
    final var rabbitDeployment = new DeploymentBuilder()
      .withNewMetadata()
      .withName(RABBIT_MQ)
      .addToLabels(APP, RABBIT_MQ)
      .addToLabels(GROUP, ECLIPSECON_2022)
      .endMetadata()
      .withNewSpec()
      .withReplicas(1)
      .withNewSelector()
      .addToMatchLabels(APP, RABBIT_MQ)
      .addToMatchLabels(GROUP, ECLIPSECON_2022)
      .endSelector()
      .withNewTemplate()
      .withNewMetadata()
      .addToLabels(APP, RABBIT_MQ)
      .addToLabels(GROUP, ECLIPSECON_2022)
      .endMetadata()
      .withNewSpec()
      .addNewContainer()
      .withName(RABBIT_MQ)
      .withImage("rabbitmq:3.11-management")
      .addToEnv(new EnvVar("RABBITMQ_DEFAULT_USER", USER, null))
      .addToEnv(new EnvVar("RABBITMQ_DEFAULT_PASS", PASSWORD, null))
      .addNewPort().withContainerPort(5672).endPort()
      .addNewPort().withContainerPort(15672).endPort()
      .endContainer()
      .endSpec()
      .endTemplate()
      .endSpec()
      .build();
    final var rabbitService = service(RABBIT_MQ, RABBIT_MQ, 5672);
    final var rabbitManagementService = service(RABBIT_MQ_MANAGEMENT, RABBIT_MQ, 15672);
    Stream.of(rabbitDeployment, rabbitService, rabbitManagementService).forEach(s -> kc.resource(s).createOrReplace());
    if (kc.adapt(OpenShiftClient.class).isSupported()) {
      kc.resource(route(RABBIT_MQ_MANAGEMENT, RABBIT_MQ, 15672)).createOrReplace();
    }
  }

  private static Route route(String name, String app, int port) {
    return new RouteBuilder()
      .withNewMetadata()
      .withName(name)
      .addToLabels(APP, app)
      .addToLabels(GROUP, ECLIPSECON_2022)
      .endMetadata()
      .withNewSpec()
      .withNewTo()
      .withKind("Service")
      .withName(name)
      .endTo()
      .withNewPort()
      .withNewTargetPort(port)
      .endPort()
      .endSpec()
      .build();
  }

  private static Service service(String name, String app, int port) {
    return new ServiceBuilder()
      .withNewMetadata()
      .withName(name)
      .addToLabels(APP, app)
      .addToLabels(GROUP, ECLIPSECON_2022)
      .endMetadata()
      .withNewSpec()
      .addToSelector(APP, app)
      .addToSelector(GROUP, ECLIPSECON_2022)
      .addNewPort().withPort(port).endPort()
      .endSpec()
      .build();
  }

}
