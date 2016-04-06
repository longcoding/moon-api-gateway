package com.longcoding.undefined.services;

import org.springframework.http.HttpEntity;
import org.springframework.web.context.request.async.DeferredResult;

/**
 * Created by longcoding on 16. 4. 6..
 */
public interface ProxyService {

    void requestProxyService(DeferredResult<HttpEntity> deferredResult);

}
