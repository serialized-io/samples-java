# Event encryption sample

Sample code demonstrating how to store and consume events containing encrypted data using
the official [Serialized Java Client](https://github.com/serialized-io/client-java).

## Get your free API-keys

[Sign up](https://serialized.io/) and login to get your free API-keys to [Serialized](https://serialized.io).

## Clone and build using Maven

```
git clone git@github.com:serialized-io/samples-java.git
mvn clean install
```
  
## Store event with encrypted data

Open a terminal window and copy/paste the commands below.

```
export SERIALIZED_ACCESS_KEY=<your-access-key>
export SERIALIZED_SECRET_ACCESS_KEY=<your-secret_access-key>
mvn -pl event-encryption exec:java -Dexec.mainClass="io.serialized.samples.encryption.EncryptionTest"
```

Output should look like this:

```
Storing aggregate: d5b19c8d-e6ff-4e80-b558-20add939279c
	secretMessage: This is a secret message created at 2019-06-13T12:42:51.340Z
	encryptedData: 779648929639866b82559889175bf2070e663d43b00101df0c940cb8acbf35c0c2878ab1a46ae831cdbfbedab9c1ca4
Done!
```

## Consume and decrypt event data

Open a seconds terminal window and copy/paste the commands below.

```
export SERIALIZED_ACCESS_KEY=<your-access-key>
export SERIALIZED_SECRET_ACCESS_KEY=<your-secret_access-key>
mvn -pl event-encryption exec:java -Dexec.mainClass="io.serialized.samples.encryption.DecryptionTest"
```

Output should look like this:

```
Processing entry with sequence number [1] - DecryptedSecret = [This is a secret message created at 2019-06-13T12:42:51.340Z]
Done!
```

