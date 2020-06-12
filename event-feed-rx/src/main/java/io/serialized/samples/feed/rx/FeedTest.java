package io.serialized.samples.feed.rx;

import io.serialized.samples.feed.rx.client.EventFeedRxClient;
import io.serialized.samples.feed.rx.order.OrderFeedEntryHandler;

import java.net.URI;
import java.util.Optional;
import java.util.concurrent.Executors;

import static org.apache.commons.lang3.StringUtils.defaultString;

public class FeedTest {

  private static final URI FEED_API_URI = URI.create("https://api.serialized.io/feeds/");

  public static void main(String[] args) {
    String accessKey = getConfig("SERIALIZED_ACCESS_KEY");
    String secretAccessKey = getConfig("SERIALIZED_SECRET_ACCESS_KEY");

    System.out.format("Connecting to [%s] using [%s]\n", FEED_API_URI, accessKey);

    OrderFeedEntryHandler entryHandler = new OrderFeedEntryHandler();
    EventFeedRxClient feedClient = new EventFeedRxClient(FEED_API_URI, "order", entryHandler, accessKey, secretAccessKey);
    System.out.println("Waiting for 'order' events...");
    Executors.newSingleThreadExecutor().submit(feedClient);
  }

  private static String getConfig(String key) {
    return Optional.ofNullable(defaultString(System.getenv(key), System.getProperty(key)))
        .orElseThrow(() -> new IllegalStateException("Missing environment property: " + key));
  }

}
