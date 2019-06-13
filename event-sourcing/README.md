# Event sourcing sample

Sample code demonstrating the [Event Sourcing API](https://docs.serialized.io/api-reference/apis/event-sourcing)
using [Jersey HTTP Client](https://jersey.github.io/documentation/latest/client.html).

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
mvn -pl event-sourcing exec:java -Dexec.mainClass="io.serialized.samples.aggregate.order.OrderTest"
```

## Expected output

The OrderTest program will post a number of **order** events to http://api.serialized.io/aggregates/order and 
print out the details on the console.

```
Placing order: OrderId[id=f8fa9a84-d55c-4b2d-a121-3a149a933b28]
Event(s) successfully saved
Loading aggregate with ID: OrderId[id=f8fa9a84-d55c-4b2d-a121-3a149a933b28]
Cancelling order: OrderId[id=f8fa9a84-d55c-4b2d-a121-3a149a933b28]
Event(s) successfully saved
Placing order: OrderId[id=8220a09a-cdf5-4e93-a93d-9f6cbff065d9]
Event(s) successfully saved
Loading aggregate with ID: OrderId[id=8220a09a-cdf5-4e93-a93d-9f6cbff065d9]
Paying order: OrderId[id=8220a09a-cdf5-4e93-a93d-9f6cbff065d9]
Event(s) successfully saved
Loading aggregate with ID: OrderId[id=8220a09a-cdf5-4e93-a93d-9f6cbff065d9]
Shipping order: OrderId[id=8220a09a-cdf5-4e93-a93d-9f6cbff065d9]
Event(s) successfully saved
```
