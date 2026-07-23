package com.prova.quipux.exception;

import com.prova.quipux.dto.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PersonNotFoundException.class)
    public ResponseEntity<ApiError> handlePersonNotFound(

            PersonNotFoundException exception,

            HttpServletRequest request
    ) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                request,
                Map.of()
        );
    }
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiError> handleDuplicate(
            DuplicateResourceException exception,

            HttpServletRequest request
    ) {
        return buildResponse(
                HttpStatus.CONFLICT,
                exception.getMessage(),
                request,
                Map.of()
        );
    }
    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ApiError> handleExternalService(
            ExternalServiceException exception,

            HttpServletRequest request
    ) {
        return buildResponse(
                HttpStatus.BAD_GATEWAY,
                exception.getMessage(),
                request,
                Map.of()
        );
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleBodyValidation(

            MethodArgumentNotValidException exception,

            HttpServletRequest request

    ) {
        Map<String, String> errors =
                new LinkedHashMap<>();
        exception
                .getBindingResult()
                .getFieldErrors()
                .forEach(fieldError ->

                        errors.putIfAbsent(
                                fieldError.getField(),
                                fieldError.getDefaultMessage()
                        )
                );
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Existem campos inválidos",
                request,
                errors
        );
    }
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleParameterValidation(
            ConstraintViolationException exception,

            HttpServletRequest request
    ) {
        Map<String, String> errors =
                new LinkedHashMap<>();
        exception
                .getConstraintViolations()
                .forEach(violation ->
                        errors.put(
                                violation
                                        .getPropertyPath()
                                        .toString(),

                                violation.getMessage()
                        )
                );
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Existem parâmetros inválidos",
                request,
                errors
        );
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleIntegrityViolation(
            DataIntegrityViolationException exception,

            HttpServletRequest request
    ) {
        return buildResponse(
                HttpStatus.CONFLICT,
                "Documento ou e-mail já cadastrado",
                request,
                Map.of()
        );
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpectedError(

            Exception exception,

            HttpServletRequest request

    ) {
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocorreu um erro interno",
                request,
                Map.of()
        );
    }
    private ResponseEntity<ApiError> buildResponse(
            HttpStatus status,

            String message,

            HttpServletRequest request,

            Map<String, String> validationErrors
    ) {
        ApiError error = new ApiError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI(),
                validationErrors
        );
        return ResponseEntity
                .status(status)
                .body(error);
    }
}