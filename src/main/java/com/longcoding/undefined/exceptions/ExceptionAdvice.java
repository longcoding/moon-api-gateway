package com.longcoding.undefined.exceptions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.longcoding.undefined.helpers.Const;
import com.longcoding.undefined.helpers.MessageManager;
import com.longcoding.undefined.models.CommonResponseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by longcoding on 16. 4. 9..
 * Updated by longcoding 0n 18. 12. 26..
 */
@Slf4j
@ControllerAdvice
public class ExceptionAdvice {
    @Autowired
    MessageManager messageManager;

    @ExceptionHandler(Exception.class)
    public ResponseEntity exception(Exception e) {
        log.error("{}", getStackTrace(e));
        CommonResponseEntity response = CommonResponseEntity.generateException("500", messageManager.getProperty("500"));
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RatelimitFailException.class)
    public ResponseEntity ratelimtFailException(RatelimitFailException e) {
        CommonResponseEntity response = CommonResponseEntity.generateException("502", messageManager.getProperty("502"));
        return new ResponseEntity<>(response, HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(ValidationFailException.class)
    public ResponseEntity validationFailException(ValidationFailException e) {
        log.error("{}", getStackTrace(e));
        CommonResponseEntity response = CommonResponseEntity.generateException("500", e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ProxyServiceFailException.class)
    public ResponseEntity proxyServiceFailException(ProxyServiceFailException e) {
        log.error("{}", getStackTrace(e));
        CommonResponseEntity response = CommonResponseEntity.generateException("504", messageManager.getProperty("504"));
        return new ResponseEntity<>(response, HttpStatus.GATEWAY_TIMEOUT);
    }

    @ExceptionHandler(GeneralException.class)
    public ResponseEntity generalException(GeneralException e) {
        log.error("{}", getStackTrace(e));
        CommonResponseEntity response = CommonResponseEntity.generateException(e.getCode(), e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    private static StringWriter getStackTrace(Exception e) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        return stringWriter;
    }

}
