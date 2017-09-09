package io.serialized.samples.feed.client;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.String.format;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

public class EventFeedClient implements Runnable {

  private static final String SERIALIZED_ACCESS_KEY = "Serialized-Access-Key";
  private static final String SERIALIZED_SECRET_ACCESS_KEY = "Serialized-Secret-Access-Key";

  private final Client client = ClientBuilder.newClient().register(JacksonJsonProvider.class);
  private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
  private final AtomicLong lastConsumedSequenceNumber = new AtomicLong();
  private final AtomicBoolean hasMore = new AtomicBoolean(false);

  private final URI feedUri;
  private final FeedEntryHandler feedEntryHandler;
  private final String accessKey;
  private final String secretAccessKey;
  private ScheduledFuture<?> handle;

  public EventFeedClient(URI feedUri, FeedEntryHandler feedEntryHandler, String accessKey, String secretAccessKey) {
    this.feedUri = feedUri;
    this.feedEntryHandler = feedEntryHandler;
    this.accessKey = accessKey;
    this.secretAccessKey = secretAccessKey;
  }

  public void start() {
    System.out.println("Waiting for events...");
    handle = executorService.scheduleWithFixedDelay(this, 2, 2, TimeUnit.SECONDS);
  }

  @Override
  public void run() {
    try {
      do {
        Feed feed = client.target(feedUri)
            .queryParam("since", lastConsumedSequenceNumber.get())
            .request(APPLICATION_JSON_TYPE)
            .header(SERIALIZED_ACCESS_KEY, accessKey)
            .header(SERIALIZED_SECRET_ACCESS_KEY, secretAccessKey)
            .get(Feed.class);

        hasMore.set(feed.hasMore);

        for (Feed.FeedEntry entry : feed.entries) {
          feedEntryHandler.handle(entry);
          lastConsumedSequenceNumber.set(entry.sequenceNumber);
        }

      } while (hasMore.get());
    } catch (Exception e) {
      System.out.println(format("Error polling feed [%s]: %s", feedUri, e.getMessage()));
    }
  }

  public void stop() {
    handle.cancel(true);
    executorService.shutdown();
  }

}
