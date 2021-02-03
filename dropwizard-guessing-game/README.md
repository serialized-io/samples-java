# Guessing Game

**Event Sourcing / CQRS Demo App using Dropwizard and Serialized**

---

**Build**

``` 
mvn clean install
```

**Start server**

``` 
export SERIALIZED_ACCESS_KEY=<your-access-key>
export SERIALIZED_SECRET_ACCESS_KEY=<your-secret-key>

java -jar target/guessing-game.jar server config/dev.yml
``` 

**Start a new guessing game**

```
curl -i http://localhost:8080/commands/start-game \
  --header "Content-Type: application/json" \
  --data '
  {  
     "gameId": "3dbc7063-76d6-4df2-8ab4-72d1f897563b"
  }
  '
```

The expected response should be something like this:

```
HTTP/1.1 201 Created
Location: http://localhost:8080/queries/games/3dbc7063-76d6-4df2-8ab4-72d1f897563b
Content-Length: 0
```

**Get the game state**

Get the projected game state with a simple GET request

```
curl -i http://localhost:8080/queries/games/3dbc7063-76d6-4df2-8ab4-72d1f897563b
```

The expected response should be something like this:

```
HTTP/1.1 200 OK
Content-Type: application/json
Vary: Accept-Encoding
Content-Length: 55

{
  "guessCount" : 0,
  "hint" : "Start guessing!"
}
```

**Take a guess**

```
curl -i http://localhost:8080/commands/guess-number \
  --header "Content-Type: application/json" \
  --data '
  {  
     "gameId": "3dbc7063-76d6-4df2-8ab4-72d1f897563b",
     "number": 50
  }
  '
```

... and keep playing and try to guess the correct number in ten tries or less.

Hopefully, you will see a result like this:

```
HTTP/1.1 200 OK
Content-Type: application/json
Vary: Accept-Encoding
Content-Length: 77

{
  "guessCount" : 5,
  "correctAnswer" : 12,
  "result" : "Player won"
}
```

After you are done playing, make the following request to display the game history:

```
curl -i http://localhost:8080/queries/games/3dbc7063-76d6-4df2-8ab4-72d1f897563b/history
```
