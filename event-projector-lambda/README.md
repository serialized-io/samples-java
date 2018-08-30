# Serialized Event Projector Lambda

This example describes how to create and deploy an AWS Lambda function acting as a
[Serialized](https://serialized.io) [projector](https://serialized.io/docs/getting-started/projections/).

The function will automatically be called by the Serialized Event Engine whenever you store a new event.
The function call will contain the event data, metadata and the current projection state (if any).
The function will return a result that will be stored by Serialized as a
[projection](https://serialized.io/docs/apis/event-projection/).    

In this example we will create a function that keeps track of how many times each user has logged in. 
We assume we have an aggregate called `user` and that an event called `UserLoggedInEvent` is emitted every time
a user logs in. We will count every `UserLoggedInEvent` and increment a field called `loginCount` in our projection.

The project folder includes a `template.yaml` file. You can use this
[SAM](https://github.com/awslabs/serverless-application-model) file to deploy the project to AWS Lambda and Amazon
API Gateway or test in local with [SAM Local](https://github.com/awslabs/aws-sam-local). 

### Building the project

Using [Maven](https://maven.apache.org/), you can create an AWS Lambda-compatible jar file simply by running the
maven package command from the project folder.

```bash
$ mvn clean package

[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: X.XXX s
[INFO] Finished at: 2018-XX-XXTXX:XX:XX-XX:XX
[INFO] Final Memory: XXM/XXXM
[INFO] ------------------------------------------------------------------------
```

### Testing the function locally
You can use [AWS SAM Local](https://github.com/awslabs/aws-sam-local) to start the project.

First, install SAM local using `npm` or `pip`:

#### npm
```bash
$ npm install -g aws-sam-local
```

#### pip
```bash
$ pip install --user aws-sam-cli
```

Next, from the project root folder - where the `template.yaml` file is located - start the API with the SAM Local CLI.

```bash
$ sam local start-api --template template.yaml

...
Mounting EventProjectorLambdaFunction at http://127.0.0.1:3000/{proxy+} [GET, DELETE, PUT, POST, HEAD, OPTIONS, PATCH]
...
```

Using a new shell, you can send a test payload to the API:

```bash
$ curl -i http://127.0.0.1:3000/project-event \
    --header "Content-Type: application/json" \
    --data '
    {
      "metadata" : {
        "aggregateId": "a341b64c-b01f-43fb-907c-50c0067df672",
        "createdAt": 1535442699551,
        "updatedAt": 1535442699551
      },
      "currentState": {},
      "event": {
        "eventId": "d710b9b1-063a-4b65-98be-0d46de443bdd",
        "eventType": "UserLoggedInEvent",
        "data": {
          "userId": "618f5a47-9d4c-4f42-9380-ca47c12087a1"
        }
      }
    }
    '
``` 

The jar file will now be automatically decompressed and executed by SAM. Note that this might take quite
some time! The response should look like this:

```
{
    "updatedState": {
        "loginCount": 1
    }
}
```

Try to change the `POST` above to include a `currentState` like this:

```
"currentState": {"loginCount": 99}
```

The response should now look like this:

```
{
    "updatedState": {
        "loginCount": 100
    }
}
```

### Deploying to AWS

You can use the [AWS CLI](https://aws.amazon.com/cli/) to quickly deploy the application to AWS Lambda
and Amazon API Gateway with your SAM template.

You will need an S3 bucket to store the artifacts for deployment. 

```
$ aws s3api create-bucket --bucket event-projector-lambda --region eu-west-1 --create-bucket-configuration LocationConstraint=eu-west-1
```

Once you have created the S3 bucket, run the following command from the project's root folder - where the
`template.yaml` file is located:

```
$ sam package --template-file template.yaml --output-template out.yaml --s3-bucket event-projector-lambda
Uploading to fa2xxxxxxxxxxxxxxxxxxxxxxxxa6359  9697552 / 9697552.0  (100.00%)
Successfully packaged artifacts and wrote output template to file out.yaml.
```

As the command output suggests, you can now use the cli to deploy the application.
 
```
$ sam deploy --template-file out.yaml --stack-name EventProjectorStack --capabilities CAPABILITY_IAM

Waiting for changeset to be created..
Waiting for stack create/update to complete
Successfully created/updated stack - EventProjectorStack
```

Once the application is deployed, you can describe the stack to show the API endpoint that was created.
The endpoint should be the `EventProjectorLambdaApi` key of the `Outputs` property:

```
$ aws cloudformation describe-stacks --stack-name EventProjectorStack --output json | grep "OutputValue"

         "OutputValue": "https://xxxxxxxxxx.execute-api.eu-west-1.amazonaws.com/Prod/project-event"
```

Now you can test the `curl` command above on your live lambda!

### Using from Serialized

Create a projection definition referring to your remote function.

Example of a definition listening to events of type `UserLoggedInEvent` posted on an aggregate type called `user`.

```bash
$ curl -i https://api.serialized.io/projections/definitions \
  --header "Content-Type: application/json" \
  --header "Serialized-Access-Key: <YOUR_ACCESS_KEY>" \
  --header "Serialized-Secret-Access-Key: <YOUR_SECRET_ACCESS_KEY>" \
  --data '
  {
    "projectionName": "login-count",
    "feedName": "user",
    "handlers": [
      {
        "eventType": "UserLoggedInEvent",
        "functionUri": "https://xxxxxxxxxx.execute-api.eu-west-1.amazonaws.com/Prod/project-event"
      }
    ]
  }
  '
``` 

The full documentation can be found [here](https://serialized.io/docs/apis/event-projection/).

This example is based on 
[Jersey Quick-start guide](https://github.com/awslabs/aws-serverless-java-container/wiki/Quick-start---Jersey).

