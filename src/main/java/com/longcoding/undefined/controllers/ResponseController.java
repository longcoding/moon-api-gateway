package com.longcoding.undefined.controllers;

import com.longcoding.undefined.configs.APISpecConfig;
import com.longcoding.undefined.helpers.MessageManager;
import com.longcoding.undefined.services.ProxyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by longcoding on 16. 4. 5..
 * Updated by longcoding on 18. 12. 26..
 */
@Slf4j
@RestController
@RequestMapping(value = "/*")
public class ResponseController {

    @Autowired
    MessageManager messageManager;

    @Autowired
    ProxyService proxyService;

    @Value("${undefined.service.proxy-timeout}")
    private long proxyServiceTimeout;

    @RequestMapping(value = "/**")
    public DeferredResult<ResponseEntity> responseHttpResult(HttpServletRequest request) {
        
        DeferredResult<ResponseEntity> deferredResult = new DeferredResult<>(proxyServiceTimeout);
        proxyService.requestProxyService(request, deferredResult);

        return deferredResult;
    }
}
