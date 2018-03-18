package io.serialized.samples.feed.client;

import java.util.LinkedHashMap;
import java.util.List;

public class Feed {

  public List<FeedEntry> entries;
  public boolean hasMore;
  public long currentSequenceNumber;

  public static class FeedEntry {
    public long sequenceNumber;
    public String aggregateId;
    public long timestamp;
    public String type;
    public List<FeedEntry.Event> events;

    public static class Event {
      public String eventId;
      public String eventType;
      public LinkedHashMap data;
    }
  }
}
