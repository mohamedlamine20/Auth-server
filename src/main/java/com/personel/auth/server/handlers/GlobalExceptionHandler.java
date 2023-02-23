package com.personel.auth.server.handlers;

import com.personel.auth.server.exceptions.InvalidCredentialsException;
import com.personel.auth.server.exceptions.ObjectNotValidException;
import com.personel.auth.server.exceptions.ResourceAlreadyExistsException;
import com.personel.auth.server.exceptions.ResourceNotFoundException;
import com.personel.auth.server.modeles.ErrorModel;
import com.personel.auth.server.payload.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.*;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<?> handleException(InvalidCredentialsException e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(e.getMessage());
    }

    @ExceptionHandler(ObjectNotValidException.class)
    public ResponseEntity<?> handleException(ObjectNotValidException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleException(ResourceNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<?> handleException(ResourceAlreadyExistsException e) {

        ErrorModel errorModel = new ErrorModel(e.getField(), "", new ArrayList<>(Collections.singletonList(e.getMessage())));
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(errorModel);
    }

//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public Map<String, String> handleValidationExceptions(
//            MethodArgumentNotValidException ex) {
//        Map<String, String> errors = new HashMap<>();
//        ex.getBindingResult().getAllErrors().forEach((error) -> {
//            String fieldName = ((FieldError) error).getField();
//            String errorMessage = error.getDefaultMessage();
//            errors.put(fieldName, errorMessage);
//        });
//        return errors;
//    }

    /**
     * Method that check against {@code @Valid} Objects passed to controller endpoints
     *
     * @param exception
     * @return a {@code ErrorResponse}
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleException(MethodArgumentNotValidException exception) {
        List<ErrorModel> errorMessages =
                exception.getBindingResult().getFieldErrors().stream()
                        .map(err -> new ErrorModel(err.getField(), err.getRejectedValue(), Arrays.stream(Objects.requireNonNull(err.getDefaultMessage()).split(",")).toList())
                        ).distinct().collect(Collectors.toList());
        return ErrorResponse.builder().errors(errorMessages)
                .status(HttpStatus.BAD_REQUEST.value() + "")
                .error_type(HttpStatus.BAD_REQUEST.toString())
                .message("")
                .build();
    }
}
