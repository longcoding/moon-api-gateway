package com.longcoding.undefined.exceptions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.longcoding.undefined.helpers.Const;
import com.longcoding.undefined.helpers.MessageManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by longcoding on 16. 4. 9..
 */
@ControllerAdvice
public class ExceptionAdviser {

    private final Logger logger = LogManager.getLogger(ExceptionAdviser.class);

    @Autowired
    MessageManager messageManager;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<JsonNode> exception(Exception e) {
        logger.error(getStackTrace(e));
        ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
        objectNode.put(Const.ERROR_MEESAGE, messageManager.getProperty("500"));
        return new ResponseEntity<>(objectNode, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RatelimitFailException.class)
    public ResponseEntity<JsonNode> ratelimtFailException(RatelimitFailException e) {
        ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
        objectNode.put(Const.ERROR_MEESAGE, messageManager.getProperty("502"));
        objectNode.put(Const.DETAIL_ERROR_MEESAGE, e.getExceptionMessage().getErrorMessage());
        return new ResponseEntity<>(objectNode, HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(ValidationFailException.class)
    public ResponseEntity<JsonNode> validationFailException(ValidationFailException e) {
        ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
        objectNode.put(Const.ERROR_MEESAGE, e.getExceptionMessage().getErrorMessage());
        return new ResponseEntity<>(objectNode, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ProxyServiceFailException.class)
    public ResponseEntity<JsonNode> proxyServiceFailException(ProxyServiceFailException e) {
        logger.error(getStackTrace(e));
        ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
        objectNode.put(Const.ERROR_MEESAGE, messageManager.getProperty("504"));
        objectNode.put(Const.DETAIL_ERROR_MEESAGE, e.getMessage());
        return new ResponseEntity<>(objectNode, HttpStatus.GATEWAY_TIMEOUT);
    }

    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<JsonNode> generalException(GeneralException e) {
        String errorCode = String.valueOf(e.getExceptionMessage().getErrorCode());

        ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
        objectNode.put(Const.ERROR_MEESAGE, messageManager.getProperty(errorCode));
        objectNode.put(Const.DETAIL_ERROR_MEESAGE, e.getExceptionMessage().getErrorMessage());
        return new ResponseEntity<>(objectNode, HttpStatus.valueOf(e.getExceptionMessage().getErrorCode()));
    }

    private static StringWriter getStackTrace(Exception e) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        return stringWriter;
    }

}
