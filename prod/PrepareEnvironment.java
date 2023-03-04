///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 17+
//DEPS io.fabric8:openshift-client:6.4.0
//DEPS org.apache.commons:commons-compress:1.22

import io.fabric8.kubernetes.api.model.ContainerBuilder;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimBuilder;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.ServiceSpecBuilder;
import io.fabric8.kubernetes.api.model.VolumeBuilder;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.api.model.apps.DeploymentSpecBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.openshift.api.model.Route;
import io.fabric8.openshift.api.model.RouteBuilder;
import io.fabric8.openshift.api.model.RouteSpecBuilder;
import io.fabric8.openshift.client.OpenShiftClient;

import java.io.ByteArrayOutputStream;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class PrepareEnvironment {

  private static final String APP = "app";
  private static final String GROUP = "group";
  private static final String PART_OF = "app.kubernetes.io/part-of";
  private static final String RUNTIME = "app.openshift.io/runtime";
  private static final String RABBIT_MQ = "rabbit-mq";
  private static final String RABBIT_MQ_MANAGEMENT = "rabbit-mq-management";
  private static final String NORTHWIND_DB = "northwind-db";
  private static final String USER = "jkube";
  private static final String PASSWORD = "pa33word";
  private static final String NORTHIWND_GROUP = "northwind-industries";

  public static void main(String... args) {
    try (var kc = new KubernetesClientBuilder().build()) {
      deployRabbitMq(kc);
      deployPostgreSql(kc);
    }
  }

  private static void deployPostgreSql(KubernetesClient kc) {
    kc.apps().deployments().withName(NORTHWIND_DB).withGracePeriod(0L).delete();
    kc.apps().deployments().withName(NORTHWIND_DB).waitUntilCondition(Objects::isNull, 10, TimeUnit.SECONDS);
    kc.pods().withLabel(APP, NORTHWIND_DB).withLabel(GROUP, NORTHIWND_GROUP).withGracePeriod(0L).delete();
    kc.pods().withLabel(APP, NORTHWIND_DB).withLabel(GROUP, NORTHIWND_GROUP)
      .waitUntilCondition(Objects::isNull, 10, TimeUnit.SECONDS);
    kc.persistentVolumeClaims().withName(NORTHWIND_DB).withGracePeriod(0L).delete();
    kc.persistentVolumeClaims().withName(NORTHWIND_DB).waitUntilCondition(Objects::isNull, 10, TimeUnit.SECONDS);
    final var persistentVolumeClaim = new PersistentVolumeClaimBuilder()
      .withMetadata(new ObjectMetaBuilder()
        .withName(NORTHWIND_DB)
        .addToLabels(APP, NORTHWIND_DB)
        .addToLabels(GROUP, NORTHIWND_GROUP)
        .addToLabels(PART_OF, NORTHIWND_GROUP)
        .build())
      .withNewSpec()
      .withAccessModes("ReadWriteOnce")
      .withNewResources()
      .addToRequests("storage", Quantity.parse("256Mi"))
      .endResources()
      .endSpec()
      .build();
    kc.resource(persistentVolumeClaim).createOrReplace();
    final var postgresDeployment = new DeploymentBuilder()
      .withMetadata(new ObjectMetaBuilder()
        .withName(NORTHWIND_DB)
        .addToLabels(APP, NORTHWIND_DB)
        .addToLabels(GROUP, NORTHIWND_GROUP)
        .addToLabels(PART_OF, NORTHIWND_GROUP)
        .addToLabels(RUNTIME, "postgresql")
        .build()
      )
      .withSpec(new DeploymentSpecBuilder()
        .withReplicas(1)
        .withNewSelector()
        .addToMatchLabels(APP, NORTHWIND_DB)
        .addToMatchLabels(GROUP, NORTHIWND_GROUP)
        .endSelector()
        .withNewTemplate()
        .withNewMetadata()
        .addToLabels(APP, NORTHWIND_DB)
        .addToLabels(GROUP, NORTHIWND_GROUP)
        .endMetadata()
        .withNewSpec()
        .addToContainers(new ContainerBuilder()
          .withName(NORTHWIND_DB)
          .withImage("bitnami/postgresql:14.5.0")
          .addToEnv(new EnvVar("POSTGRESQL_USERNAME", USER, null))
          .addToEnv(new EnvVar("POSTGRESQL_PASSWORD", PASSWORD, null))
          .addNewPort().withContainerPort(5432).endPort()
          .addNewVolumeMount()
          .withName("data")
          .withMountPath("/bitnami/postgresql")
          .endVolumeMount()
          .withNewReadinessProbe()
          .withNewExec()
          .withCommand("/bin/sh", "-c", "pg_isready -U ${POSTGRESQL_USERNAME}")
          .endExec()
          .endReadinessProbe()
          .build())
        .addToVolumes(new VolumeBuilder()
          .withName("data")
          .withNewPersistentVolumeClaim()
          .withClaimName(NORTHWIND_DB)
          .endPersistentVolumeClaim()
          .build())
        .endSpec()
        .endTemplate()
        .build())
      .build();
    final var postgresService = service(NORTHWIND_DB, NORTHWIND_DB, 5432);
    Stream.of(postgresDeployment, postgresService).forEach(s -> kc.resource(s).createOrReplace());
    var pod = kc.pods().withLabel(APP, NORTHWIND_DB).withLabel(GROUP, NORTHIWND_GROUP)
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
      .withMetadata(new ObjectMetaBuilder()
        .withName(RABBIT_MQ)
        .addToLabels(APP, RABBIT_MQ)
        .addToLabels(GROUP, NORTHIWND_GROUP)
        .addToLabels(PART_OF, NORTHIWND_GROUP)
        .addToLabels(RUNTIME, "rabbitmq")
        .build())
      .withSpec(new DeploymentSpecBuilder()
        .withReplicas(1)
        .withNewSelector()
        .addToMatchLabels(APP, RABBIT_MQ)
        .addToMatchLabels(GROUP, NORTHIWND_GROUP)
        .endSelector()
        .withNewTemplate()
        .withNewMetadata()
        .addToLabels(APP, RABBIT_MQ)
        .addToLabels(GROUP, NORTHIWND_GROUP)
        .endMetadata()
        .withNewSpec()
        .addToContainers(new ContainerBuilder()
          .withName(RABBIT_MQ)
          .withImage("rabbitmq:3.11-management")
          .addToEnv(new EnvVar("RABBITMQ_DEFAULT_USER", USER, null))
          .addToEnv(new EnvVar("RABBITMQ_DEFAULT_PASS", PASSWORD, null))
          .addNewPort().withContainerPort(5672).endPort()
          .addNewPort().withContainerPort(15672).endPort()
          .build())
        .endSpec()
        .endTemplate()
        .build())
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
      .withMetadata(new ObjectMetaBuilder()
        .withName(name)
        .addToLabels(APP, app)
        .addToLabels(GROUP, NORTHIWND_GROUP)
        .addToLabels(PART_OF, NORTHIWND_GROUP)
        .build())
      .withSpec(new RouteSpecBuilder()
        .withNewTo()
        .withKind("Service")
        .withName(name)
        .endTo()
        .withNewPort()
        .withNewTargetPort(port)
        .endPort()
        .build())
      .build();
  }

  private static Service service(String name, String app, int port) {
    return new ServiceBuilder()
      .withMetadata(new ObjectMetaBuilder()
        .withName(name)
        .addToLabels(APP, app)
        .addToLabels(GROUP, NORTHIWND_GROUP)
        .addToLabels(PART_OF, NORTHIWND_GROUP)
        .build())
      .withSpec(new ServiceSpecBuilder()
        .addToSelector(APP, app)
        .addToSelector(GROUP, NORTHIWND_GROUP)
        .addNewPort().withPort(port).endPort()
        .build())
      .build();
  }

}
