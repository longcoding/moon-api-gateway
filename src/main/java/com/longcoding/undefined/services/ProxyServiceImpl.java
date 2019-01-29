package com.longcoding.undefined.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.longcoding.undefined.exceptions.ProxyServiceFailException;
import com.longcoding.undefined.helpers.Constant;
import com.longcoding.undefined.helpers.JettyClientFactory;
import com.longcoding.undefined.helpers.JsonUtil;
import com.longcoding.undefined.models.ResponseInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.api.Result;
import org.eclipse.jetty.client.util.BufferingResponseListener;
import org.eclipse.jetty.client.util.BytesContentProvider;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Objects;

/**
 * Created by longcoding on 16. 4. 6..
 * Updated by longcoding on 18. 12. 26..
 */
@Slf4j
@Service
public class ProxyServiceImpl implements ProxyService {

    @Autowired
    JettyClientFactory jettyClientFactory;

    private ResponseInfo responseInfo;
    private static final String ERROR_MESSAGE_WRONG_CONTENT_TYPE = "Content-Type is not matched";

    public void requestProxyService(HttpServletRequest request, DeferredResult<ResponseEntity> deferredResult) {

        this.responseInfo = (ResponseInfo) request.getAttribute(Constant.RESPONSE_INFO_DATA);
        Request proxyRequest = jettyClientFactory.getJettyClient().newRequest(responseInfo.getRequestURI());

        long proxyStartTime = System.currentTimeMillis();

        setHeaderAndQueryInfo(proxyRequest, responseInfo).send(new BufferingResponseListener() {
            @Override
            public void onComplete(Result result) {
                if (result.isSucceeded()) {
                    ResponseEntity<JsonNode> responseEntity;

                    responseInfo.setProxyElapsedTime(System.currentTimeMillis() - proxyStartTime);

                    if (log.isDebugEnabled()){
                        log.debug("Http Proxy ElapsedTime " + responseInfo.getProxyElapsedTime());
                    }

                    HttpFields responseHeaders = result.getResponse().getHeaders();
                    if (responseHeaders.contains(HttpHeader.CONTENT_TYPE)) {
                        String contentTypeValue = responseHeaders.get(HttpHeader.CONTENT_TYPE);
                        if ( contentTypeValue.split(Constant.CONTENT_TYPE_EXTRACT_DELIMITER)[0]
                                .equals(responseInfo.getRequestAccept().split(Constant.CONTENT_TYPE_EXTRACT_DELIMITER)[0])){

                            JsonNode responseInJsonNode = InputStreamToJsonObj(getContentAsInputStream());
                            responseEntity =
                                    new ResponseEntity<>(responseInJsonNode, HttpStatus.valueOf(result.getResponse().getStatus()));

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
            if (responseInfo.getRequestContentType().contains(MimeTypeUtils.APPLICATION_JSON_VALUE) || responseInfo.getRequestContentType().contains(MimeTypeUtils.TEXT_PLAIN_VALUE)) {
                if (Objects.nonNull(responseInfo.getRequestBody())) request.content(new BytesContentProvider(responseInfo.getRequestBody()), responseInfo.getRequestContentType());
            }
        }

        Map<String, String> requestQueryParams = responseInfo.getQueryStringMap();
        requestQueryParams.forEach(request::param);

        return request;
    }

    private static JsonNode InputStreamToJsonObj(InputStream responseInput) {
        try {
            InputStreamReader responseInputStreamReader = new InputStreamReader(responseInput, Charset.forName(Constant.SERVER_DEFAULT_ENCODING_TYPE));
            return JsonUtil.getObjectMapper().readTree(responseInputStreamReader);
        } catch (IOException ex) {
            throw new ProxyServiceFailException(ERROR_MESSAGE_WRONG_CONTENT_TYPE);
        }
    }

}
