package com.longcoding.undefined.services.impl;

import com.longcoding.undefined.helpers.Const;
import com.longcoding.undefined.helpers.netty.NettyClientFactory;
import com.longcoding.undefined.models.RequestInfo;
import com.longcoding.undefined.models.ResponseInfo;
import com.longcoding.undefined.services.ProxyService;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.api.Result;
import org.eclipse.jetty.client.util.BufferingResponseListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;
import play.libs.Json;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Created by longcoding on 16. 4. 6..
 */
@Service
public class ProxyServiceImpl implements ProxyService {

    @Autowired
    NettyClientFactory nettyClientFactory;

    private DeferredResult<ResponseEntity> deferredResult;

    public void requestProxyService(HttpServletRequest request, DeferredResult<ResponseEntity> deferredResult) {

        this.deferredResult = deferredResult;

        ResponseInfo responseInfo = (ResponseInfo) request.getAttribute(Const.RESPONSE_INFO_DATA);
        Request proxyRequest = nettyClientFactory.getNettyClient().newRequest(responseInfo.getRequestURI());

        setHeaderInfo(proxyRequest, responseInfo).send(bufferingResponseListener());
        
    }

    private Request setHeaderInfo(Request request, ResponseInfo responseInfo) {
        Map<String, String> requestHeaders = responseInfo.getHeaders();

        for ( String headerKey : requestHeaders.keySet() ) {
            request.header(headerKey, requestHeaders.get(headerKey));
        }

        request.accept(responseInfo.getRequestAccept());
        request.header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);

        return request;
    }

    private Response.Listener bufferingResponseListener() {
        return new BufferingResponseListener() {
            @Override
            public void onComplete(Result result) {
                if (!result.isFailed()) {
                    ResponseEntity responseEntity = new ResponseEntity(Json.parse(getContentAsString(Charset.forName("UTF-8"))), HttpStatus.OK);
                    deferredResult.setResult(responseEntity);
                }
            }
        };
    }

}
