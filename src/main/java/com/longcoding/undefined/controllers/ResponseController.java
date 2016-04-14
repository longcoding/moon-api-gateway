package com.longcoding.undefined.controllers;

import com.longcoding.undefined.helpers.MessageManager;
import com.longcoding.undefined.services.ProxyService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by longcoding on 16. 4. 5..
 */
@RestController
@RequestMapping(value = "/*")
public class ResponseController {

    private static final Logger logger = LogManager.getLogger(ResponseController.class);

    @Autowired
    ProxyService proxyService;

    @Autowired
    MessageManager messageManager;

    private static long PROXY_SERVICE_TIMEOUT;

    @PostConstruct
    private void initializeProxyService() {
        PROXY_SERVICE_TIMEOUT = messageManager.getLongProperty("undefined.service.proxy.timeout");
    }

    @RequestMapping(value = "/**")
    public DeferredResult<ResponseEntity> responseHttpResult(HttpServletRequest request) {

        DeferredResult deferredResult = new DeferredResult();
        proxyService.requestProxyService(request, deferredResult);

        return deferredResult;
    }
}