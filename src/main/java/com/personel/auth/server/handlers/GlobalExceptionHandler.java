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

import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<?> databaseHandlerException(SQLException e) {
        String sqlState = e.getSQLState();
        String errorMessage = "An error occurred while accessing the database.";
        String columnName = null;
        String rejectedValue = null;
        switch (sqlState) {
            case "23505": {
                // Get the index of the start and end of the column name(s) in the error message
                int startIndex = e.getMessage().indexOf("(") + 1;
                int endIndex = e.getMessage().indexOf(")", startIndex);
                // Extract the column name(s) from the error message
                columnName = e.getMessage().substring(startIndex, endIndex);
                // Customize the error message with the column name(s)
                errorMessage = String.format("A duplicate key value was found for column(s): %s.", columnName);
                // Duplicate key error
                // errorMessage = "A record with the same value already exists.";
                String patternString = "\\(username\\)=\\((.*?)\\)";
                // Create a pattern object and a matcher object
                Pattern pattern = Pattern.compile(patternString);
                Matcher matcher = pattern.matcher(e.getMessage());
                // Extract the username from the matched substring
                if (matcher.find()) {
                    rejectedValue = matcher.group(1);
                }
                break;
            }
            case "08006": {
                // Connection error
                errorMessage = "Unable to connect to the database.";
                break;
            }

        }
        ErrorModel errorModel = new ErrorModel(columnName, rejectedValue, Collections.singletonList(errorMessage));
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(errorModel);
    }

    /**
     * Method that check against {@code @Valid} Objects passed to controller endpoints
     *
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
