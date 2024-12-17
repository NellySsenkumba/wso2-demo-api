package org.nelly.wso2demoapi.exceptions;


import com.alibaba.fastjson2.JSONException;
import com.alibaba.fastjson2.JSONObject;

import lombok.extern.log4j.Log4j2;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.SQLGrammarException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.format.DateTimeParseException;

@RestControllerAdvice
@Log4j2
public class CustomExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    private ResponseEntity<JSONObject> handleAll(Exception e) {
        log(e);

        return new ResponseEntity<>(
                JSONObject.of("code", HttpStatus.BAD_GATEWAY.value(), "message", e.getMessage()),
                HttpStatus.BAD_GATEWAY);
    }


    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    public final ResponseEntity<JSONObject> handleParsing(MethodArgumentTypeMismatchException e) {
        log(e);
        return new ResponseEntity<>(JSONObject.of(
                "code", HttpStatus.I_AM_A_TEAPOT.value(),
                "message", e.getValue() + " is not of " + e.getRequiredType().getName() + " searchType. Please check your request and try again."
        ), HttpStatus.I_AM_A_TEAPOT);
    }


    @ExceptionHandler(value = {HttpMessageNotReadableException.class, MethodArgumentNotValidException.class})
    public final ResponseEntity<JSONObject> handleRequest(Exception e) {
        log(e);
        return new ResponseEntity<>(JSONObject.of(
                "code", HttpStatus.I_AM_A_TEAPOT.value(),
                "message", "Required request body is missing or is invalid. Please check your request and try again."
        ), HttpStatus.I_AM_A_TEAPOT);
    }

    @ExceptionHandler(value = {ConstraintViolationException.class, DataIntegrityViolationException.class})
    public final ResponseEntity<JSONObject> handleConstraintAndDataIntegrity(Exception e) {
        log(e);
        return new ResponseEntity<>(JSONObject.of(
                "code", HttpStatus.EXPECTATION_FAILED.value(),
                "message", "Constraint and Data Integrity Violation."
        ), HttpStatus.EXPECTATION_FAILED);
    }

    @ExceptionHandler(value = SQLGrammarException.class)
    public final ResponseEntity<JSONObject> handleSQLGrammarException(SQLGrammarException e) {
        log(e);
        return new ResponseEntity<>(JSONObject.of(
                "code", HttpStatus.EXPECTATION_FAILED.value(),
                "message", "Syntax Error in SQL Query."
        ), HttpStatus.EXPECTATION_FAILED);
    }


    @ExceptionHandler(value = JSONException.class)
    private ResponseEntity<JSONObject> handleJSONException(JSONException e) {
        log(e);
        return new ResponseEntity<>(JSONObject.of(
                "code", HttpStatus.BAD_REQUEST.value(),
                "message", "Cannot parse JSON. Please check your request and try again."
        ), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = DateTimeParseException.class)
    private ResponseEntity<JSONObject> handleDateTimeParseException(DateTimeParseException e) {
        log(e);
        return new ResponseEntity<>(JSONObject.of(
                "code", HttpStatus.BAD_REQUEST.value(),
                "message", e.getParsedString() + " is an invalid date format (YYYY-MM-DD). Please check your request and try again."
        ), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = RuntimeException.class)
    private ResponseEntity<JSONObject> handleRuntimeExceptionException(RuntimeException e) {
        log(e);
        return new ResponseEntity<>(JSONObject.of(
                "code", HttpStatus.BAD_REQUEST.value(),
                "message", e.getMessage()
        ), HttpStatus.BAD_REQUEST);
    }


    //todo: can be made better
    private void log(Exception e) {
        if (log.isDebugEnabled()) {
            log.error(e.getMessage(), e);
        }
        log.error(e.getMessage());
        log.error(e.getLocalizedMessage());
        log.error(e.getCause());
        log.error(e.getClass());
    }
}
