package be.informatievlaanderen.ldes.server.integration.test.rest.config;

import be.informatievlaanderen.ldes.server.integration.test.domain.exceptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class MemberGeometryExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(MemberGeometryExceptionHandler.class);

    @ExceptionHandler(value = ResourceNotFoundException.class)
    protected ResponseEntity<Object> handleNotFound(RuntimeException e, WebRequest request) {
        log.error(e.getMessage());
        return handleExceptionInternal(e, e.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }
}
