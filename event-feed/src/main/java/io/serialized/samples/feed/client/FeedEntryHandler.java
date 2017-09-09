package io.serialized.samples.feed.client;

public interface FeedEntryHandler {

  void handle(Feed.FeedEntry feedEntry);

}
