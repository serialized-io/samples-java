package io.serialized.samples.orderservice;

import io.dropwizard.Configuration;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.defaultString;

public class OrderApplicationConfig extends Configuration {

  @NotNull
  public URI serializedApi;

  public String serializedAccessKey = getSystemConfig("SERIALIZED_ACCESS_KEY");

  public String serializedSecretAccessKey = getSystemConfig("SERIALIZED_SECRET_ACCESS_KEY");

  public static String getSystemConfig(String key) {
    return Optional.ofNullable(defaultString(System.getenv(key), System.getProperty(key)))
        .orElseThrow(() -> new IllegalStateException("Missing environment property: " + key));
  }

}
