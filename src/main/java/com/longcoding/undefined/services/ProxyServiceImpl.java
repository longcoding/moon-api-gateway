package com.longcoding.undefined.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.longcoding.undefined.exceptions.ProxyServiceFailException;
import com.longcoding.undefined.helpers.Const;
import com.longcoding.undefined.helpers.JettyClientFactory;
import com.longcoding.undefined.helpers.JsonUtil;
import com.longcoding.undefined.models.ResponseInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.api.Result;
import org.eclipse.jetty.client.util.BufferingResponseListener;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Created by longcoding on 16. 4. 6..
 * Updated by longcoding on 18. 12. 26..
 */
@Slf4j
@Service
public class ProxyServiceImpl implements ProxyService {

    private static final String CONST_CONTENT_TYPE_EXTRACT_DELIMITER = ";";

    @Autowired
    JettyClientFactory jettyClientFactory;

    private ResponseInfo responseInfo;
    private static final String ERROR_MESSAGE_WRONG_CONTENT_TYPE = "Content-Type is not matched";

    public void requestProxyService(HttpServletRequest request, DeferredResult<ResponseEntity> deferredResult) {

        long start = System.currentTimeMillis();

        this.responseInfo = (ResponseInfo) request.getAttribute(Const.RESPONSE_INFO_DATA);
        Request proxyRequest = jettyClientFactory.getJettyClient().newRequest(responseInfo.getRequestURI());

        setHeaderAndQueryInfo(proxyRequest, responseInfo).send(new BufferingResponseListener() {
            @Override
            public void onComplete(Result result) {
                if (result.isSucceeded()) {
                    ResponseEntity<JsonNode> responseEntity;

                    if (log.isDebugEnabled()){
                        log.debug("Http Time " + (System.currentTimeMillis() - start));
                    }

                    HttpFields responseHeaders = result.getResponse().getHeaders();
                    if (responseHeaders.contains(HttpHeader.CONTENT_TYPE)) {
                        String contentTypeValue = responseHeaders.get(HttpHeader.CONTENT_TYPE);
                        if ( contentTypeValue.split(CONST_CONTENT_TYPE_EXTRACT_DELIMITER)[0]
                                .equals(responseInfo.getRequestAccept().split(CONST_CONTENT_TYPE_EXTRACT_DELIMITER)[0])){
                            responseEntity =
                                    new ResponseEntity<>(JsonUtil.toJsonNode(getContentAsString(Charset.forName(Const.SERVER_DEFAULT_ENCODING_TYPE))), HttpStatus.valueOf(result.getResponse().getStatus()));

                            deferredResult.setResult(responseEntity);
                        }
                    }

                    deferredResult.setErrorResult(new ProxyServiceFailException(ERROR_MESSAGE_WRONG_CONTENT_TYPE));
                }
            }

            @Override
            public void onFailure(Response response, Throwable failure) {
                deferredResult.setErrorResult(new ProxyServiceFailException(failure));
            }
        });
    }

    private static Request setHeaderAndQueryInfo(Request request, ResponseInfo responseInfo) {
        Map<String, String> requestHeaders = responseInfo.getHeaders();

        requestHeaders.forEach(request::header);

        request.method(responseInfo.getRequestMethod());
        request.accept(responseInfo.getRequestAccept());

        if (Strings.isNotEmpty(responseInfo.getRequestContentType())) {
            if (responseInfo.getRequestContentType().contains(MimeTypeUtils.APPLICATION_JSON_VALUE)) {
                String body = JsonUtil.fromJson(responseInfo.getRequestBody());
                request.content(new StringContentProvider(body), responseInfo.getRequestContentType());
            } else if (responseInfo.getRequestContentType().contains(MimeTypeUtils.TEXT_PLAIN_VALUE)) {
                String body = String.valueOf(responseInfo.getRequestBody());
                request.content(new StringContentProvider(body), responseInfo.getRequestContentType());
            }

            Map<String, String> requestQueryParams = responseInfo.getQueryStringMap();
        requestQueryParams.forEach(request::param);

        return request;
    }

}
