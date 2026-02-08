package quironconcursos.exceptions;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import quironconcursos.exceptions.app.AuthenticationException;
import quironconcursos.exceptions.app.ForbiddenException;
import quironconcursos.exceptions.app.NotFoundException;
import quironconcursos.exceptions.app.ValidationException;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class AppRestExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<AppMessageError> handlerException(Exception e) {
        System.err.println(e.getMessage());

        AppMessageError error = new AppMessageError(
                "Internal Server Error",
                List.of("Internal server error...")
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<AppMessageError> handlerException(RuntimeException e) {
        System.err.println(e.getMessage());

        AppMessageError error = new AppMessageError(
                "Internal Server Error",
                List.of("Internal server error...")
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<AppMessageError> handlerException(HttpMessageNotReadableException e) {
        AppMessageError error = new AppMessageError("Bad Request", List.of("Invalid JSON"));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<AppMessageError> handlerException(HttpMediaTypeNotAcceptableException e) {
        AppMessageError error = new AppMessageError("Bad Request", List.of("Bad request..."));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<AppMessageError> handlerException(MethodArgumentNotValidException e) {
        List<String> errors = e.getBindingResult().getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());

        AppMessageError error = new AppMessageError("Invalid data", errors);

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
    }

    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<AppMessageError> handlerException(PropertyReferenceException e) {
        AppMessageError error = new AppMessageError(
                "Invalid sorting property",
                List.of("The sorting property '" + e.getPropertyName() + "' is not valid")
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<AppMessageError> handlerException(AuthenticationException e) {
        AppMessageError error = new AppMessageError(
                "Authentication",
                List.of(e.getMessage())
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<AppMessageError> handlerException(ForbiddenException e) {
        AppMessageError error = new AppMessageError(
                "Forbidden",
                List.of(e.getMessage())
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<AppMessageError> handlerException(NotFoundException e) {
        AppMessageError error = new AppMessageError("Not Found", List.of(e.getMessage()));

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<AppMessageError> handlerException(ValidationException e) {
        AppMessageError error = new AppMessageError("Invalid data",
                List.of(e.getMessage())
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

}
