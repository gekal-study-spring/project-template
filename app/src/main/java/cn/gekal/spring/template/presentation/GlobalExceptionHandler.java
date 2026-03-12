package cn.gekal.spring.template.presentation;

import cn.gekal.spring.template.domain.model.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(AuthorizationDeniedException.class)
  public ResponseEntity<ErrorResponse> handleAuthorizationDeniedException(
      AuthorizationDeniedException ex, HttpServletRequest request) {
    log.error("Authorization denied: ", ex);
    return ErrorResponse.toEntity(HttpStatus.FORBIDDEN, "Access is denied", request);
  }

  @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
  public ResponseEntity<ErrorResponse> handleNotFoundException(
      Exception ex, HttpServletRequest request) {
    log.error("Resource not found: ", ex);
    return ErrorResponse.toEntity(HttpStatus.NOT_FOUND, "Resource not found", request);
  }

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleUserNotFoundException(
      UserNotFoundException ex, HttpServletRequest request) {
    log.error("User not found: ", ex);
    return ErrorResponse.toEntity(HttpStatus.NOT_FOUND, "User not found", request);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException ex, HttpServletRequest request) {
    List<ErrorResponse.FieldErrorDetail> errors =
        ex.getBindingResult().getFieldErrors().stream()
            .map(
                error ->
                    new ErrorResponse.FieldErrorDetail(error.getField(), error.getDefaultMessage()))
            .toList();

    log.error("Validation error: {}", errors);
    return ErrorResponse.toEntity(HttpStatus.BAD_REQUEST, "Validation failed", request, errors);
  }

  @ExceptionHandler(HandlerMethodValidationException.class)
  public ResponseEntity<ErrorResponse> handleHandlerMethodValidationException(
      HandlerMethodValidationException ex, HttpServletRequest request) {
    List<ErrorResponse.FieldErrorDetail> errors =
        ex.getAllErrors().stream()
            .map(
                error -> {
                  if (error instanceof FieldError fieldError) {
                    return new ErrorResponse.FieldErrorDetail(
                        fieldError.getField(), fieldError.getDefaultMessage());
                  }
                  return new ErrorResponse.FieldErrorDetail(null, error.getDefaultMessage());
                })
            .toList();

    log.error("Validation error: {}", errors);
    return ErrorResponse.toEntity(HttpStatus.BAD_REQUEST, "Validation failed", request, errors);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
    String message =
        String.format(
            "Parameter value is invalid for type '%s'", ex.getRequiredType().getSimpleName());
    log.error("Type mismatch error for parameter '{}': {}", ex.getName(), message);
    return ErrorResponse.toEntity(HttpStatus.BAD_REQUEST, message, request);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(
      Exception ex, HttpServletRequest request) {
    log.error("An unexpected error occurred: ", ex);
    return ErrorResponse.toEntity(
        HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", request);
  }
}
