package com.longcoding.undefined.services.impl;

import com.longcoding.undefined.helpers.netty.NettyClientFactory;
import com.longcoding.undefined.services.ProxyService;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.api.Result;
import org.eclipse.jetty.client.util.BufferingResponseListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;
import play.libs.Json;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

/**
 * Created by longcoding on 16. 4. 6..
 */
@Service
public class ProxyServiceImpl implements ProxyService {

    @Autowired
    NettyClientFactory nettyClientFactory;

    private DeferredResult<HttpEntity> deferredResult;
    private Request request;

    public void requestProxyService(DeferredResult<HttpEntity> deferredResult) {

        this.deferredResult = deferredResult;
        Request request = nettyClientFactory.getNettyClient().newRequest(setURI());

        setHeaderParam(request).send(bufferingResponseListener());
        
    }

    private BufferingResponseListener bufferingResponseListener() {
        return new BufferingResponseListener() {
            @Override
            public void onComplete(Result result) {
                if (!result.isFailed()) {
                    HttpEntity httpEntity = new HttpEntity(Json.parse(getContentAsString(Charset.forName("UTF-8"))));
                    deferredResult.setResult(httpEntity);
                }
            }
        }
    }

    private Request setHeaderParam(Request request) {
        return request;
    }

    private URI setURI() {
        try {
            return new URI("develop");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }
}
