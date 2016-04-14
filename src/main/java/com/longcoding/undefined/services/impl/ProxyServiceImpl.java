package com.longcoding.undefined.services.impl;

import com.longcoding.undefined.exceptions.ProxyServiceFailException;
import com.longcoding.undefined.helpers.Const;
import com.longcoding.undefined.helpers.netty.NettyClientFactory;
import com.longcoding.undefined.models.ResponseInfo;
import com.longcoding.undefined.services.ProxyService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.api.Result;
import org.eclipse.jetty.client.util.BufferingResponseListener;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;
import play.libs.Json;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Created by longcoding on 16. 4. 6..
 */
@Service
public class ProxyServiceImpl implements ProxyService {

    protected static final Logger logger = LogManager.getLogger(ProxyServiceImpl.class);

    private static final String CONST_CONTENT_TYPE_EXTRACT_DELIMITER = ";";

    @Autowired
    NettyClientFactory nettyClientFactory;

    private DeferredResult<ResponseEntity> deferredResult;
    private ResponseInfo responseInfo;

    private static final String ERROR_MESSAGE_WRONG_CONTENT_TYPE = "Content-Type is not matched";

    public void requestProxyService(HttpServletRequest request, DeferredResult<ResponseEntity> deferredResult) {

        this.deferredResult = deferredResult;

        long start = System.currentTimeMillis();

        this.responseInfo = (ResponseInfo) request.getAttribute(Const.RESPONSE_INFO_DATA);
        Request proxyRequest = nettyClientFactory.getNettyClient().newRequest(responseInfo.getRequestURI());

        setHeaderAndQueryInfo(proxyRequest, responseInfo).send(new BufferingResponseListener() {
            @Override
            public void onComplete(Result result) {
                if (!result.isFailed()) {
                    ResponseEntity responseEntity = null;

                    logger.debug("Http Time " + (System.currentTimeMillis() - start));

                    HttpFields responseHeaders = result.getResponse().getHeaders();
                    if (responseHeaders.contains(HttpHeader.CONTENT_TYPE)) {
                        String contentTypeValue = responseHeaders.get(HttpHeader.CONTENT_TYPE);
                        if ( contentTypeValue.split(CONST_CONTENT_TYPE_EXTRACT_DELIMITER)[0]
                                .equals(responseInfo.getRequestAccept().split(CONST_CONTENT_TYPE_EXTRACT_DELIMITER)[0])){
                            responseEntity =
                                    new ResponseEntity(Json.parse(getContentAsString(Charset.forName(Const.SERVER_DEFAULT_ENCODING_TYPE))), HttpStatus.OK);
                        } else {
                            deferredResult.setErrorResult(new ProxyServiceFailException(ERROR_MESSAGE_WRONG_CONTENT_TYPE));
                        }
                    } else {
                        deferredResult.setErrorResult(new ProxyServiceFailException(ERROR_MESSAGE_WRONG_CONTENT_TYPE));
                    }
                    deferredResult.setResult(responseEntity);
                }
            }

            @Override
            public void onFailure(Response response, Throwable failure) {
                deferredResult.setErrorResult(new ProxyServiceFailException("", failure));
            }
        });

    }

    private static Request setHeaderAndQueryInfo(Request request, ResponseInfo responseInfo) {
        Map<String, String> requestHeaders = responseInfo.getHeaders();

        for ( String headerKey : requestHeaders.keySet() ) {
            request.header(headerKey, requestHeaders.get(headerKey));
        }

        request.method(request.getMethod());
        request.accept(responseInfo.getRequestAccept());

        Map<String, String> requestQueryParams = responseInfo.getQueryStringMap();
        for ( String queryKey : requestQueryParams.keySet() ) {
            request.param(queryKey, requestQueryParams.get(queryKey));
        }

        return request;
    }

}
