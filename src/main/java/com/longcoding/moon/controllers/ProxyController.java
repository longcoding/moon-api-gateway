package com.longcoding.moon.controllers;

import com.longcoding.moon.helpers.MessageManager;
import com.longcoding.moon.services.ProxyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletRequest;

/**
 * The Controller class is responsible for delivering the newly created request from the PrepareProxyInterceptor
 * to the outbound service. In addition, the outbound service sends the response to the user.
 * The api-gateway is developed based on the interceptor.
 * If all interceptors have been passed, the ProxyController will be reached.
 *
 * @author longcoding
 */

@Slf4j
@RestController
public class ProxyController {

    @Autowired
    MessageManager messageManager;

    @Autowired
    ProxyService proxyService;

    @Value("${moon.service.proxy-timeout}")
    private long proxyServiceTimeout;

    /**
     * All api requests will reach that method.
     * It forwards the user's request to the outbound service and forwards the response back to the user.
     *
     * swagger and internal api are not accepted.
     *
     * @param request Request received from user.
     * @return The response data from the outbound service.
     */
    @RequestMapping(value = "/{serviceName:^(?!swagger-ui.html|webjars|internal).*}/**")
    public DeferredResult<ResponseEntity> responseHttpResult(HttpServletRequest request) {

        DeferredResult<ResponseEntity> deferredResult = new DeferredResult<>(proxyServiceTimeout);
        proxyService.requestProxyService(request, deferredResult);

        return deferredResult;
    }
}
