package io.serialized.samples.feed;

import io.serialized.samples.feed.client.EventFeedClient;
import io.serialized.samples.feed.order.OrderFeedEntryHandler;

import java.net.URI;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.apache.commons.lang.StringUtils.defaultString;

public class FeedTest {

  private static final URI ORDER_FEED_API_URI = URI.create("https://api.serialized.io/feeds/order");

  public static void main(String[] args) {
    String accessKey = getConfig("SERIALIZED_ACCESS_KEY");
    String secretAccessKey = getConfig("SERIALIZED_SECRET_ACCESS_KEY");

    System.out.format("Connecting to [%s] using [%s]\n", ORDER_FEED_API_URI, accessKey);

    OrderFeedEntryHandler entryHandler = new OrderFeedEntryHandler();
    EventFeedClient feedClient = new EventFeedClient(ORDER_FEED_API_URI, entryHandler, accessKey, secretAccessKey);
    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    System.out.println("Waiting for events...");
    executor.scheduleWithFixedDelay(feedClient, 2, 2, TimeUnit.SECONDS);
  }

  private static String getConfig(String key) {
    return Optional.ofNullable(defaultString(System.getenv(key), System.getProperty(key)))
        .orElseThrow(() -> new IllegalStateException("Missing environment property: " + key));
  }

}
