# Rock-Paper-Scissors implementation using Event Sourcing in Java


## Request API-keys

[Sign up](https://serialized.io/) to get your free API-keys to [Serialized](https://serialized.io).

## Build using Maven

```
mvn clean package
```

## Run the service

Open a terminal window and start the service, on port 8080, using the following command

```
export SERIALIZED_ACCESS_KEY=<your-access-key>
export SERIALIZED_SECRET_ACCESS_KEY=<your-secret_access-key>

mvn spring-boot:run
```

## Start a new game

```
curl -i http://localhost:8080/start-game \
  --header "Content-Type: application/json" \
  --data '
    {
        "gameId" : "b0ac0259-190b-459e-a179-49696f857484",
        "player1" : "Alice",
        "player2" : "Bob"
    }
  '
```

## Make player 1 show a hand

```
curl -i http://localhost:8080/show-hand \
  --header "Content-Type: application/json" \
  --data '
    {
        "gameId" : "b0ac0259-190b-459e-a179-49696f857484",
        "player" : "Alice",
        "answer" : "PAPER"
    }
  '
```

## Make player 2 show a hand

```
curl -i http://localhost:8080/show-hand \
  --header "Content-Type: application/json" \
  --data '
    {
        "gameId" : "b0ac0259-190b-459e-a179-49696f857484",
        "player" : "Bob",
        "answer" : "SCISSORS"
    }
  '
```

... Keep showing hands until one of the players have won.

## Get high score

```
curl http://localhost:8080/high-score
```

## Get game stats

```
curl http://localhost:8080/stats
```

