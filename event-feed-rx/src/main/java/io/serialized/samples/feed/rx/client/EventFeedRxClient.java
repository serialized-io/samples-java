package io.serialized.samples.feed.rx.client;

import io.reactivex.Observable;
import io.reactivex.internal.schedulers.RxThreadFactory;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.concurrent.TimeUnit.SECONDS;

public class EventFeedRxClient implements Runnable {

  private static final String SERIALIZED_ACCESS_KEY = "Serialized-Access-Key";
  private static final String SERIALIZED_SECRET_ACCESS_KEY = "Serialized-Secret-Access-Key";

  private final AtomicLong lastConsumedSequenceNumber = new AtomicLong();

  private final URI feedUri;
  private final String aggregateType;
  private final FeedEntryHandler feedEntryHandler;
  private EventFeedService eventFeedService;

  public EventFeedRxClient(URI feedUri, String aggregateType, FeedEntryHandler feedEntryHandler, String accessKey, String secretAccessKey) {
    this.feedUri = feedUri;
    this.aggregateType = aggregateType;
    this.feedEntryHandler = feedEntryHandler;
    this.eventFeedService = newRetrofitClient(new Headers.Builder()
        .add(SERIALIZED_ACCESS_KEY, accessKey)
        .add(SERIALIZED_SECRET_ACCESS_KEY, secretAccessKey)
        .build(), EventFeedService.class);
  }

  public void run() {
    Observable.interval(2, SECONDS)
        .observeOn(Schedulers.from(Executors.newSingleThreadExecutor(new RxThreadFactory("feed-poller"))))
        .flatMap(tick -> eventFeedService.getEvents(aggregateType, lastConsumedSequenceNumber.get()))
        .doOnError(err -> System.err.println("Error retrieving messages: " + err))
        .retry()
        .filter(feed -> !feed.entries.isEmpty())
        .subscribe(feed -> {
          for (Feed.FeedEntry feedEntry : feed.entries) {
            feedEntryHandler.handle(feedEntry);
            lastConsumedSequenceNumber.set(feedEntry.sequenceNumber);
          }
        });
  }

  private <T> T newRetrofitClient(Headers headers, Class<T> clazz) {
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(feedUri.toString())
        .addConverterFactory(JacksonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .client(new OkHttpClient.Builder()
            .addInterceptor(chain -> chain.proceed(chain.request().newBuilder().headers(headers).build()))
            .build())
        .build();
    return retrofit.create(clazz);
  }

  public interface EventFeedService {
    @GET("{aggregateType}")
    Observable<Feed> getEvents(@Path("aggregateType") String aggregateType, @Query("since") Long since);
  }

}
