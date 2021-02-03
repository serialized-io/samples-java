package io.serialized.samples.guessinggame.api;

import io.dropwizard.jersey.errors.ErrorMessage;
import io.serialized.client.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

public class ApiExceptionMapper implements ExceptionMapper<ApiException> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ApiExceptionMapper.class);

  @Override
  public Response toResponse(ApiException ex) {

    final Response.ResponseBuilder responseBuilder;
    if (ex.statusCode() == 404) {
      LOGGER.info(ex.getMessage());
      responseBuilder = Response.status(NOT_FOUND);
    } else if (ex.statusCode() == 409) {
      LOGGER.info(ex.getMessage());
      responseBuilder = Response.status(CONFLICT);
    } else {
      LOGGER.warn(ex.getMessage(), ex);
      responseBuilder = Response.serverError();
    }

    return responseBuilder
        .type(APPLICATION_JSON_TYPE)
        .entity(new ErrorMessage(ex.statusCode(), "Error calling Serialized API"))
        .build();
  }

}
