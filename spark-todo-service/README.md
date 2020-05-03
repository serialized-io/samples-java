# Todo Service - Minimal CQRS/EventSourcing sample application

Minimal sample application demonstrating the Serialized APIs using [Spark](http://sparkjava.com/) and
the official [Serialized Java Client](https://github.com/serialized-io/client-java).

## Get your free API-keys

[Sign up](https://serialized.io/) and login to get your free API-keys to [Serialized](https://serialized.io).

## Clone and build using Maven

```
git clone git@github.com:serialized-io/samples-java.git
mvn clean install
```
  
## Run

Open a terminal window and start the service, on port 8080, using the following command

```
export SERIALIZED_ACCESS_KEY=<your-access-key>
export SERIALIZED_SECRET_ACCESS_KEY=<your-secret_access-key>
mvn -pl spark-todo-service exec:java -Dexec.mainClass="io.serialized.samples.TodoService"
```

#### Create a TODO list

```
curl -i http://localhost:8080/commands/create-list \
  --header "Content-Type: application/json" \
  --data '
  {  
     "listId": "1cf63e2d-bdd6-4ec4-85ec-2a9c0d34c749",
     "name": "Important things to do"
  }
  '
```

#### Inspect the projected result

Get the projected list with a simple GET request

```
curl http://localhost:8080/queries/lists/1cf63e2d-bdd6-4ec4-85ec-2a9c0d34c749
```

The result should be:

```
{                                                          
  "projectionId" : "1cf63e2d-bdd6-4ec4-85ec-2a9c0d34c749", 
  "updatedAt" : 1560419582186,                             
  "data" : {                                               
    "name" : "Important things to do",                     
    "status" : "EMPTY"                                     
  }
}
```

#### Create a TODO and add it to the list

```
curl -i http://localhost:8080/commands/create-todo \
  --header "Content-Type: application/json" \
  --data '
  {  
     "listId": "1cf63e2d-bdd6-4ec4-85ec-2a9c0d34c749",
     "todoId": "9fc82130-6ce2-45bb-af25-c27eb97b38f0",
     "todoText": "Buy milk"
  }
  '
```

#### Inspect the projected result again

```
curl http://localhost:8080/queries/lists/1cf63e2d-bdd6-4ec4-85ec-2a9c0d34c749
```

The result should be:

```
{                                                          
  "projectionId" : "1cf63e2d-bdd6-4ec4-85ec-2a9c0d34c749", 
  "updatedAt" : 1560419864096,                             
  "data" : {                                               
    "name" : "Important things to do",                     
    "status" : "IN_PROGRESS",                              
    "todos" : [ {                                          
      "todoId" : "9fc82130-6ce2-45bb-af25-c27eb97b38f0",   
      "text" : "Buy milk",                                 
      "status" : "IN_PROGRESS"                             
    } ]                                                    
  }                                                        
}
```

#### Complete the TODO

```
curl -i http://localhost:8080/commands/complete-todo \
  --header "Content-Type: application/json" \
  --data '
  {  
     "listId": "1cf63e2d-bdd6-4ec4-85ec-2a9c0d34c749",
     "todoId": "9fc82130-6ce2-45bb-af25-c27eb97b38f0"
  }
  '
```

#### Filter all lists by status

Available list statuses are *EMPTY*, *IN_PROGRESS* and *COMPLETED*.

```
curl http://localhost:8080/queries/lists?status=COMPLETED
```

