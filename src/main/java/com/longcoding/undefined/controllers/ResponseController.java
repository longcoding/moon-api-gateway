package com.longcoding.undefined.controllers;

import com.longcoding.undefined.helpers.MessageManager;
import com.longcoding.undefined.services.ProxyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by longcoding on 16. 4. 5..
 */
@RestController
@RequestMapping(value = "/**")
public class ResponseController {

    @Autowired
    ProxyService proxyService;

    @Autowired
    MessageManager messageManager;

    private static long PROXY_SERVICE_TIMEOUT;

    @PostConstruct
    private void initializeProxyService() {
        PROXY_SERVICE_TIMEOUT = messageManager.getLongProperty("undefined.netty.http.timeout");
    }

    @RequestMapping(method = RequestMethod.GET)
    public DeferredResult<ResponseEntity> responseHttpResult(HttpServletRequest request) {

        DeferredResult deferredResult = new DeferredResult(PROXY_SERVICE_TIMEOUT);
        proxyService.requestProxyService(request, deferredResult);

        return deferredResult;
    }
}