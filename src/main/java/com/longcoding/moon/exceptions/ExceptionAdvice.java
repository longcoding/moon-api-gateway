package com.longcoding.moon.exceptions;

import com.longcoding.moon.helpers.Constant;
import com.longcoding.moon.helpers.HttpHelper;
import com.longcoding.moon.helpers.MessageManager;
import com.longcoding.moon.models.CommonResponseEntity;
import com.longcoding.moon.models.RequestInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;

/**
 * If a logic exception occurs, then the declared exception will be caught by this class.
 * By default, we are sending a generic exception with a common exception.
 * The code table for the exception condition can be referenced to the exceptionType.
 *
 * You can refer to this uuid when tracking error situations by including uuid in every request.
 *
 * @see GeneralException
 * @see ExceptionType
 *
 * @author longcoding
 */
@Slf4j
@ControllerAdvice
public class ExceptionAdvice {
    @Autowired
    MessageManager messageManager;

    @ExceptionHandler(Exception.class)
    public ResponseEntity exception(Exception e, HttpServletRequest request) {
        log.error("{}", getStackTrace(e));
        ExceptionType exceptionType = ExceptionType.E_9999_INTERNAL_SERVER_ERROR;
        setHttpResponseErrorCode(request, ExceptionType.E_9999_INTERNAL_SERVER_ERROR.getCode());
        CommonResponseEntity response = CommonResponseEntity.generateException(exceptionType.getCode(), messageManager.getProperty(exceptionType.getCode()));
        return HttpHelper.newResponseEntityWithId(HttpStatus.INTERNAL_SERVER_ERROR, response);
    }

    @ExceptionHandler(AsyncRequestTimeoutException.class)
    public ResponseEntity asyncRequestTimeoutException(Exception e, HttpServletRequest request) {
        log.error("{}", getStackTrace(e));
        ExceptionType exceptionType = ExceptionType.E_1104_OUTBOUND_SERVICE_REQUEST_TIME_OUT;
        setHttpResponseErrorCode(request, ExceptionType.E_1104_OUTBOUND_SERVICE_REQUEST_TIME_OUT.getCode());
        CommonResponseEntity response = CommonResponseEntity.generateException(exceptionType.getCode(), messageManager.getProperty(exceptionType.getCode()));
        return HttpHelper.newResponseEntityWithId(HttpStatus.REQUEST_TIMEOUT, response);
    }

    @ExceptionHandler(RatelimitFailException.class)
    public ResponseEntity ratelimtFailException(RatelimitFailException e, HttpServletRequest request) {
        log.error("{}", getStackTrace(e));
        ExceptionType exceptionType = ExceptionType.E_1009_SERVICE_RATELIMIT_OVER;
        setHttpResponseErrorCode(request, ExceptionType.E_1009_SERVICE_RATELIMIT_OVER.getCode());
        CommonResponseEntity response = CommonResponseEntity.generateException(exceptionType.getCode(), messageManager.getProperty(exceptionType.getCode()));
        return HttpHelper.newResponseEntityWithId(exceptionType.getHttpStatus(), response);
    }

    @ExceptionHandler(ValidationFailException.class)
    public ResponseEntity validationFailException(ValidationFailException e, HttpServletRequest request) {
        log.error("{}", getStackTrace(e));
        ExceptionType exceptionType = ExceptionType.E_1007_INVALID_OR_MISSING_ARGUMENT;
        setHttpResponseErrorCode(request, ExceptionType.E_1007_INVALID_OR_MISSING_ARGUMENT.getCode());
        CommonResponseEntity response = CommonResponseEntity.generateException(exceptionType.getCode(), messageManager.getProperty(exceptionType.getCode()));
        return HttpHelper.newResponseEntityWithId(exceptionType.getHttpStatus(), response);
    }

    @ExceptionHandler(ProxyServiceFailException.class)
    public ResponseEntity proxyServiceFailException(ProxyServiceFailException e, HttpServletRequest request) {
        log.error("{}", getStackTrace(e));
        ExceptionType exceptionType = ExceptionType.E_1102_OUTBOUND_SERVICE_IS_NOT_UNSTABLE;
        setHttpResponseErrorCode(request, ExceptionType.E_1102_OUTBOUND_SERVICE_IS_NOT_UNSTABLE.getCode());
        String message = messageManager.getProperty(exceptionType.getCode());
        CommonResponseEntity response = CommonResponseEntity.generateException(exceptionType.getCode(), message);
        return HttpHelper.newResponseEntityWithId(exceptionType.getHttpStatus(), response);
    }

    @ExceptionHandler(GeneralException.class)
    public ResponseEntity generalException(GeneralException e, HttpServletRequest request) {
        log.error("{}", getStackTrace(e));
        ExceptionType exceptionType = e.getExceptionType();
        setHttpResponseErrorCode(request, exceptionType.getCode());
        String message = messageManager.getProperty(exceptionType.getCode());
        if (Strings.isNotEmpty(e.getMessage())) message += e.getMessage();
        CommonResponseEntity response = CommonResponseEntity.generateException(e.getExceptionType().getCode(), message);
        return HttpHelper.newResponseEntityWithId(e.getExceptionType().getHttpStatus(), response);
    }

    private static StringWriter getStackTrace(Exception e) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        return stringWriter;
    }

    private void setHttpResponseErrorCode(HttpServletRequest request, String code) {
        RequestInfo requestInfo = (RequestInfo) request.getAttribute(Constant.REQUEST_INFO_DATA);
        if (Objects.nonNull(requestInfo)) requestInfo.setErrorCode(code);
    }

}
