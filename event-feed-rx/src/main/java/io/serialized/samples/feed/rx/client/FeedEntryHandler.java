package io.serialized.samples.feed.rx.client;

public interface FeedEntryHandler {

  void handle(Feed.FeedEntry feedEntry);

}
