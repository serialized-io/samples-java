package io.serialized.samples.feed;

import io.serialized.samples.feed.client.EventFeedClient;
import io.serialized.samples.feed.order.OrderFeedEntryHandler;

import java.net.URI;

public class FeedTest {

  private static final URI ORDER_FEED_URI = URI.create("https://api.serialized.io/feeds/order");

  public static void main(String[] args) throws InterruptedException {
    String accessKey = System.getenv("SERIALIZED_ACCESS_KEY");
    String secretAccessKey = System.getenv("SERIALIZED_SECRET_ACCESS_KEY");

    System.out.format("Connecting to [%s] using [%s]\n", ORDER_FEED_URI, accessKey);

    OrderFeedEntryHandler entryHandler = new OrderFeedEntryHandler();
    new EventFeedClient(ORDER_FEED_URI, entryHandler, accessKey, secretAccessKey).start();
  }

}
