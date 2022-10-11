///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 17+
//DEPS io.fabric8:openshift-client:6.1.1

import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.openshift.api.model.RouteBuilder;
import io.fabric8.openshift.client.OpenShiftClient;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class PrepareEnvironment {

  private static final String APP = "app";
  private static final String GROUP = "group";
  private static final String RABBIT_MQ = "rabbit-mq";
  private static final String RABBIT_MQ_MANAGEMENT = "rabbit-mq-management";
  private static final String ECLIPSECON_2022 = "eclipsecon-2022";

  public static void main(String... args) {
    try (var kc = new KubernetesClientBuilder().build()) {
      deployRabbitMq(kc);
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
      .addToEnv(new EnvVar("RABBITMQ_DEFAULT_USER", "jkube", null))
      .addToEnv(new EnvVar("RABBITMQ_DEFAULT_PASS", "pa33word", null))
      .addNewPort().withContainerPort(5672).endPort()
      .addNewPort().withContainerPort(15672).endPort()
      .endContainer()
      .endSpec()
      .endTemplate()
      .endSpec()
      .build();
    final var rabbitService = new ServiceBuilder()
      .withNewMetadata()
      .withName(RABBIT_MQ)
      .addToLabels(APP, RABBIT_MQ)
      .addToLabels(GROUP, ECLIPSECON_2022)
      .endMetadata()
      .withNewSpec()
      .addToSelector(APP, RABBIT_MQ)
      .addToSelector(GROUP, ECLIPSECON_2022)
      .addNewPort().withPort(5672).endPort()
      .endSpec()
      .build();
    final var rabbitManagementService = new ServiceBuilder()
      .withNewMetadata()
      .withName(RABBIT_MQ_MANAGEMENT)
      .addToLabels(APP, RABBIT_MQ)
      .addToLabels(GROUP, ECLIPSECON_2022)
      .endMetadata()
      .withNewSpec()
      .addToSelector(APP, RABBIT_MQ)
      .addToSelector(GROUP, ECLIPSECON_2022)
      .addNewPort().withPort(15672).endPort()
      .endSpec()
      .build();
    Stream.of(rabbitDeployment, rabbitService, rabbitManagementService).forEach(s -> kc.resource(s).createOrReplace());
    if (kc.adapt(OpenShiftClient.class).isSupported()) {
      final var rabbitRoute = new RouteBuilder()
        .withNewMetadata()
        .withName(RABBIT_MQ_MANAGEMENT)
        .addToLabels(APP, RABBIT_MQ)
        .addToLabels(GROUP, ECLIPSECON_2022)
        .endMetadata()
        .withNewSpec()
        .withNewTo()
        .withKind("Service")
        .withName(RABBIT_MQ_MANAGEMENT)
        .endTo()
        .withNewPort()
        .withNewTargetPort(15672)
        .endPort()
        .endSpec()
        .build();
      kc.resource(rabbitRoute).createOrReplace();
    }
  }
}
