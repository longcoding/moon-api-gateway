package com.longcoding.undefined.exceptions;

import com.longcoding.undefined.helpers.Const;
import com.longcoding.undefined.helpers.MessageManager;
import com.longcoding.undefined.models.CommonResponseEntity;
import com.longcoding.undefined.models.RequestInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;

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
    public ResponseEntity exception(Exception e, HttpServletRequest request) {
        RequestInfo requestInfo = (RequestInfo) request.getAttribute(Const.REQUEST_INFO_DATA);
        log.error("[ Request ID : {} ]\n, {}", Objects.nonNull(requestInfo)? requestInfo.getRequestId():"none", getStackTrace(e));
        ExceptionType exceptionType = ExceptionType.E_9999_INTERNAL_SERVER_ERROR;
        setHttpResponseErrorCode(request, ExceptionType.E_9999_INTERNAL_SERVER_ERROR.getCode());
        CommonResponseEntity response = CommonResponseEntity.generateException(exceptionType.getCode(), messageManager.getProperty(exceptionType.getCode()));
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RatelimitFailException.class)
    public ResponseEntity ratelimtFailException(RatelimitFailException e, HttpServletRequest request) {
        RequestInfo requestInfo = (RequestInfo) request.getAttribute(Const.REQUEST_INFO_DATA);
        log.error("[ Request ID : {} ]\n, {}", Objects.nonNull(requestInfo)? requestInfo.getRequestId():"none", getStackTrace(e));
        ExceptionType exceptionType = ExceptionType.E_1009_SERVICE_RATELIMIT_OVER;
        setHttpResponseErrorCode(request, ExceptionType.E_1009_SERVICE_RATELIMIT_OVER.getCode());
        CommonResponseEntity response = CommonResponseEntity.generateException(exceptionType.getCode(), messageManager.getProperty(exceptionType.getCode()));
        return new ResponseEntity<>(response, exceptionType.getHttpStatus());
    }

    @ExceptionHandler(ValidationFailException.class)
    public ResponseEntity validationFailException(ValidationFailException e, HttpServletRequest request) {
        RequestInfo requestInfo = (RequestInfo) request.getAttribute(Const.REQUEST_INFO_DATA);
        log.error("[ Request ID : {} ]\n, {}", Objects.nonNull(requestInfo)? requestInfo.getRequestId():"none", getStackTrace(e));
        ExceptionType exceptionType = ExceptionType.E_1007_INVALID_OR_MISSING_ARGUMENT;
        setHttpResponseErrorCode(request, ExceptionType.E_1007_INVALID_OR_MISSING_ARGUMENT.getCode());
        CommonResponseEntity response = CommonResponseEntity.generateException(exceptionType.getCode(), messageManager.getProperty(exceptionType.getCode()));
        return new ResponseEntity<>(response, exceptionType.getHttpStatus());
    }

    @ExceptionHandler(ProxyServiceFailException.class)
    public ResponseEntity proxyServiceFailException(ProxyServiceFailException e, HttpServletRequest request) {
        RequestInfo requestInfo = (RequestInfo) request.getAttribute(Const.REQUEST_INFO_DATA);
        log.error("[ Request ID : {} ]\n, {}", Objects.nonNull(requestInfo)? requestInfo.getRequestId():"none", getStackTrace(e));
        ExceptionType exceptionType = ExceptionType.E_1102_OUTBOUND_SERVICE_IS_NOT_UNSTABLE;
        setHttpResponseErrorCode(request, ExceptionType.E_1102_OUTBOUND_SERVICE_IS_NOT_UNSTABLE.getCode());
        String message = messageManager.getProperty(exceptionType.getCode()) + e.getMessage();
        CommonResponseEntity response = CommonResponseEntity.generateException(exceptionType.getCode(), message);
        return new ResponseEntity<>(response, exceptionType.getHttpStatus());
    }

    @ExceptionHandler(GeneralException.class)
    public ResponseEntity generalException(GeneralException e, HttpServletRequest request) {
        RequestInfo requestInfo = (RequestInfo) request.getAttribute(Const.REQUEST_INFO_DATA);
        log.error("[ Request ID : {} ]\n, {}", Objects.nonNull(requestInfo)? requestInfo.getRequestId():"none", getStackTrace(e));
        ExceptionType exceptionType = e.getExceptionType();
        setHttpResponseErrorCode(request, exceptionType.getCode());
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

    private void setHttpResponseErrorCode(HttpServletRequest request, String code) {
        RequestInfo requestInfo = (RequestInfo) request.getAttribute(Const.REQUEST_INFO_DATA);
        if (Objects.nonNull(requestInfo)) requestInfo.setErrorCode(code);
    }

}
