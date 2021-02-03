package io.serialized.samples.guessinggame.api;

import io.dropwizard.jersey.errors.ErrorMessage;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

public class IllegalStateExceptionMapper implements ExceptionMapper<IllegalStateException> {

  @Override
  public Response toResponse(IllegalStateException ex) {

    return Response.status(BAD_REQUEST)
        .type(APPLICATION_JSON_TYPE)
        .entity(new ErrorMessage(BAD_REQUEST.getStatusCode(), ex.getMessage()))
        .build();
  }

}
