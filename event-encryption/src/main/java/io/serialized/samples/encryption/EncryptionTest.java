package io.serialized.samples.encryption;

import io.serialized.client.SerializedClientConfig;
import io.serialized.client.aggregate.AggregateClient;
import io.serialized.client.aggregate.Event;
import io.serialized.samples.encryption.crypto.CryptoKeyRepository;
import io.serialized.samples.encryption.crypto.EncryptionService;
import io.serialized.samples.encryption.crypto.impl.AesEncryptionService;
import io.serialized.samples.encryption.crypto.impl.DummyCryptoKeyRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static java.util.Collections.singletonList;
import static org.apache.commons.lang.StringUtils.defaultString;

public class EncryptionTest {

  public static void main(String[] args) {
    String accessKey = getConfig("SERIALIZED_ACCESS_KEY");
    String secretAccessKey = getConfig("SERIALIZED_SECRET_ACCESS_KEY");

    SerializedClientConfig config = SerializedClientConfig.serializedConfig()
        .accessKey(accessKey)
        .secretAccessKey(secretAccessKey)
        .build();

    System.out.format("Connecting using [%s]\n", accessKey);

    EncryptionService encryptionService = new AesEncryptionService();
    CryptoKeyRepository cryptoKeyRepository = new DummyCryptoKeyRepository();

    AggregateClient<Object> aggregateClient = AggregateClient.aggregateClient("customer", Object.class, config).build();

    UUID customerId = UUID.randomUUID();

    byte[] secretKey = cryptoKeyRepository.getSecretKey(customerId);
    String secretMessage = "This is a secret message created at " + Instant.now();
    String encryptedData = encryptionService.encrypt(secretKey, secretMessage);

    Event event = Event.newEvent("CustomerRegistered")
        .data("customerId", customerId)
        .encryptedData(encryptedData).build();

    UUID aggregateId = UUID.randomUUID();

    System.out.println("Storing aggregate: " + aggregateId);
    System.out.println("\tsecretMessage: " + secretMessage);
    System.out.println("\tencryptedData: " + event.getEncryptedData());

    aggregateClient.save(aggregateId, singletonList(event));

    System.out.println("Done!");
  }

  private static String getConfig(String key) {
    return Optional.ofNullable(defaultString(System.getenv(key), System.getProperty(key)))
        .orElseThrow(() -> new IllegalStateException("Missing environment property: " + key));
  }

}