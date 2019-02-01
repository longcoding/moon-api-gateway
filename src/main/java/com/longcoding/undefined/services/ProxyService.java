package com.longcoding.undefined.services;

import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by longcoding on 16. 4. 6..
 */
public interface ProxyService {

    void requestProxyService(HttpServletRequest request, DeferredResult<ResponseEntity> deferredResult);

}
