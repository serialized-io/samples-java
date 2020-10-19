package io.serialized.samples.orderservice.api;

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
    if (ex.statusCode() == 404) {
      LOGGER.info(ex.getMessage());
      return Response.status(NOT_FOUND).type(APPLICATION_JSON_TYPE).build();
    } else if (ex.statusCode() == 409) {
      LOGGER.info(ex.getMessage());
      return Response.status(CONFLICT).type(APPLICATION_JSON_TYPE).build();
    } else {
      LOGGER.warn(ex.getMessage(), ex);
      return Response.serverError().type(APPLICATION_JSON_TYPE).build();
    }
  }

}
