# Event feed rx sample

Sample code demonstrating how to poll events from the [Feeds API](https://docs.serialized.io/api-reference/apis/feeds)
using [Retrofit 2](https://square.github.io/retrofit/) and [RxJava](https://github.com/ReactiveX/RxJava).

## Get your free API-keys

[Sign up](https://serialized.io/) and login to get your free API-keys to [Serialized](https://serialized.io).

## Clone and build using Maven

```
git clone git@github.com:serialized-io/samples-java.git
mvn clean install
```
  
## Run

Open a terminal window and copy/paste the commands below.

```
export SERIALIZED_ACCESS_KEY=<your-access-key>
export SERIALIZED_SECRET_ACCESS_KEY=<your-secret_access-key>
mvn -pl event-feed-rx exec:java -Dexec.mainClass="io.serialized.samples.feed.rx.FeedTest"
```

## Expected output

The FeedTest program will periodically poll the **order** feed from http://api.serialized.io/feeds/order and print out
the events on the console.

Note that if you haven't sourced any **order** events yet, no output will be visible - run the
[OrderTest](https://github.com/serialized-io/samples-java/tree/master/event-sourcing) sample program to store
a couple of test events to your event store at Serialized.

The program will continue polling until it's closed.