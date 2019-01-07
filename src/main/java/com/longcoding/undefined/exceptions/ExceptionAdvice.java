package com.longcoding.undefined.exceptions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.longcoding.undefined.helpers.Const;
import com.longcoding.undefined.helpers.MessageManager;
import com.longcoding.undefined.models.CommonResponseEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by longcoding on 16. 4. 9..
 * Updated by longcoding 0n 19. 1. 7..
 */
@Slf4j
@ControllerAdvice
public class ExceptionAdvice {
    @Autowired
    MessageManager messageManager;

    @ExceptionHandler(Exception.class)
    public ResponseEntity exception(Exception e) {
        log.error("{}", getStackTrace(e));
        ExceptionType exceptionType = ExceptionType.E_9999_INTERNAL_SERVER_ERROR;
        CommonResponseEntity response = CommonResponseEntity.generateException(exceptionType.getCode(), messageManager.getProperty(exceptionType.getCode()));
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RatelimitFailException.class)
    public ResponseEntity ratelimtFailException(RatelimitFailException e) {
        ExceptionType exceptionType = ExceptionType.E_1009_SERVICE_RATELIMIT_OVER;
        CommonResponseEntity response = CommonResponseEntity.generateException(exceptionType.getCode(), messageManager.getProperty(exceptionType.getCode()));
        return new ResponseEntity<>(response, exceptionType.getHttpStatus());
    }

    @ExceptionHandler(ValidationFailException.class)
    public ResponseEntity validationFailException(ValidationFailException e) {
        log.error("{}", getStackTrace(e));
        ExceptionType exceptionType = ExceptionType.E_1007_INVALID_OR_MISSING_ARGUMENT;
        CommonResponseEntity response = CommonResponseEntity.generateException(exceptionType.getCode(), messageManager.getProperty(exceptionType.getCode()));
        return new ResponseEntity<>(response, exceptionType.getHttpStatus());
    }

    @ExceptionHandler(ProxyServiceFailException.class)
    public ResponseEntity proxyServiceFailException(ProxyServiceFailException e) {
        log.error("{}", getStackTrace(e));
        ExceptionType exceptionType = ExceptionType.E_1102_OUTBOUND_SERVICE_IS_NOT_UNSTABLE;
        CommonResponseEntity response = CommonResponseEntity.generateException(exceptionType.getCode(), messageManager.getProperty(exceptionType.getCode()));
        return new ResponseEntity<>(response, exceptionType.getHttpStatus());
    }

    @ExceptionHandler(GeneralException.class)
    public ResponseEntity generalException(GeneralException e) {
        log.error("{}", getStackTrace(e));
        ExceptionType exceptionType = e.getExceptionType();
        String message = messageManager.getProperty(exceptionType.getCode());
        if (Strings.isNotEmpty(e.getMessage())) message += e.getMessage();
        CommonResponseEntity response = CommonResponseEntity.generateException(e.getExceptionType().getCode(), message);
        return new ResponseEntity<>(response, e.getExceptionType().getHttpStatus());
    }

    private static StringWriter getStackTrace(Exception e) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        return stringWriter;
    }

}
