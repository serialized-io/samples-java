package io.serialized.samples.projector;

import com.amazonaws.serverless.proxy.jersey.JerseyLambdaContainerHandler;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import io.serialized.samples.projector.api.EventProjectorResource;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamLambdaHandler implements RequestStreamHandler {

  private static final ResourceConfig JERSEY_APPLICATION = new ResourceConfig()
      .register(EventProjectorResource.class)
      .register(JacksonFeature.class);

  private static final JerseyLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> HANDLER
      = JerseyLambdaContainerHandler.getAwsProxyHandler(JERSEY_APPLICATION);

  @Override
  public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
    HANDLER.proxyStream(inputStream, outputStream, context);
    outputStream.close();
  }

}