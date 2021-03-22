package io.serialized.samples.encryption;

import io.serialized.client.SerializedClientConfig;
import io.serialized.client.feed.Event;
import io.serialized.client.feed.FeedClient;
import io.serialized.client.feed.FeedResponse;
import io.serialized.samples.encryption.crypto.CryptoKeyRepository;
import io.serialized.samples.encryption.crypto.EncryptionService;
import io.serialized.samples.encryption.crypto.impl.AesEncryptionService;
import io.serialized.samples.encryption.crypto.impl.DummyCryptoKeyRepository;

import java.util.Optional;
import java.util.UUID;

import static io.serialized.client.feed.FeedRequests.getFromFeed;
import static org.apache.commons.lang3.StringUtils.defaultString;

public class DecryptionTest {

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

    FeedClient feedClient = FeedClient.feedClient(config).build();

    FeedResponse response = feedClient.execute(getFromFeed("customer").build(), 0);

    System.out.printf("Processing entry with sequence number [%s] - ", response.currentSequenceNumber());

    for (Event event : response.events()) {
      UUID customerId = UUID.fromString(event.dataValueAs("customerId", String.class));
      byte[] secretKey = cryptoKeyRepository.getSecretKey(customerId);
      String decryptedSecret = (String) encryptionService.decrypt(secretKey, event.encryptedData());
      System.out.printf("DecryptedSecret = [%s]\n", decryptedSecret);
    }

    System.out.println("Done!");
  }

  private static String getConfig(String key) {
    return Optional.ofNullable(defaultString(System.getenv(key), System.getProperty(key)))
        .orElseThrow(() -> new IllegalStateException("Missing environment property: " + key));
  }

}
