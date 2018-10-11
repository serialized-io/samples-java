# Java samples for Serialized


## Request API-keys

[Sign up](https://serialized.io/) to get your free API-keys to [Serialized.io](https://serialized.io).


## Clone and build

Clone this git repository

```
git clone https://bitbucket.org/serialized-io/samples-java.git
```

Build using Maven 

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

## CQRS/ES sample application using Dropwizard and rxjava to access Serialized IO

#### Start the service

Open a terminal window and start the service, on port 8080, using the following command

```
export SERIALIZED_ACCESS_KEY=<your-access-key>
export SERIALIZED_SECRET_ACCESS_KEY=<your-secret_access-key>
cd order-service
java -jar target/order-service.jar server config/dev.yml
```
 
#### Place an order

```
curl -i http://localhost:8080/commands/place-order \
  --header "Content-Type: application/json" \
  --data '
  {  
     "orderId": "32c122fe-f4e8-4315-8b8e-0a17ef1e0c60",
     "customerId": "9c690460-85bb-4bce-832f-bb3f6f61cca8",
     "orderAmount": "1234"
  }
  '
```

#### Inspect the projected result

Get the placed order's projection with a simple GET request

```
curl http://localhost:8080/queries/orders/32c122fe-f4e8-4315-8b8e-0a17ef1e0c60
```

The result should be:

```
{
  "orderId" : "32c122fe-f4e8-4315-8b8e-0a17ef1e0c60",
  "customerId" : "9c690460-85bb-4bce-832f-bb3f6f61cca8",
  "orderAmount" : 1234,
  "status" : "PLACED"
}

```

#### List orders by status

```
curl http://localhost:8080/queries/orders?status=PLACED
```

#### Inspect the projected stats

```
curl http://localhost:8080/queries/shipping-stats/
```

#### Inspect the projected total customer debt

See the total order amount for all orders placed but not yet paid.

```
curl http://localhost:8080/queries/total-customer-debt/
```

#### Executing more commands

Try out the other commands: `pay-order`, `ship-order` and `cancel-order`.

See the details [here](https://github.com/serialized-io/samples-java/tree/master/order-service/src/main/java/io/serialized/samples/orderservice/api/command) 
