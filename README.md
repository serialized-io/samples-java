# Java samples for Serialized.io

Clone this repository and build using Maven 

```
mvn clean install
```

## Event Feed API

Start by opening one terminal window and copy/paste the commands below.

```
export SERIALIZED_ACCESS_KEY=<your-access-key>
export SERIALIZED_SECRET_ACCESS_KEY=<your-secret_access-key>
mvn -pl event-feed exec:java -Dexec.mainClass="io.serialized.samples.feed.FeedTest"
```

You now have a running java process with an active subscription to the feed `order`.
You are now ready to store some events so keep reading!

## Event Sourcing API

Open a second terminal window and copy/paste the commands below.

```
export SERIALIZED_ACCESS_KEY=<your-access-key>
export SERIALIZED_SECRET_ACCESS_KEY=<your-secret_access-key>
mvn -pl event-sourcing exec:java -Dexec.mainClass="io.serialized.samples.aggregate.order.OrderTest"
```

You should se some output indicating the events were successfully stored in your cloud space at Serialized.io.
Go back to the first terminal window and you should notice that the order events were successfully processed!


