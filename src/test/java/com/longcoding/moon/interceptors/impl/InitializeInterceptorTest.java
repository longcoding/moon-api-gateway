package com.longcoding.moon.interceptors.impl;

import com.longcoding.moon.helpers.Constant;
import com.longcoding.moon.models.RequestInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.util.MimeTypeUtils;

@Slf4j
public class InitializeInterceptorTest {

    private MockHttpServletRequest request = new MockHttpServletRequest("POST", "/stackoverflow/2.2/question/coding");
    private MockHttpServletResponse response = new MockHttpServletResponse();
    private InitializeInterceptor interceptor = new InitializeInterceptor();

    @Before
    public void setup() throws Exception {
        request.addHeader("Content-Type", MimeTypeUtils.APPLICATION_JSON_VALUE);
        request.addHeader("apiKey", "5000-5000-5000-5000");
        request.setQueryString("version=2.2");

        interceptor.preHandler(request, response, null);
    }

    @Test
    public void initializeRequestWithApikey() {
        log.info("Initializes the request. And Check apiKey");

        RequestInfo requestInfo = (RequestInfo) request.getAttribute(Constant.REQUEST_INFO_DATA);
        Assert.assertEquals("5000-5000-5000-5000", requestInfo.getHeaders().get("apikey"));
    }

    @Test
    public void initializeRequestWithHeaders() {
        log.info("Initializes the request. And Check headers");

        RequestInfo requestInfo = (RequestInfo) request.getAttribute(Constant.REQUEST_INFO_DATA);
        Assert.assertEquals(2, requestInfo.getHeaders().size());
    }

    @Test
    public void initializeRequestWithHttpMethod() {
        log.info("Initializes the request. And Check headers");

        RequestInfo requestInfo = (RequestInfo) request.getAttribute(Constant.REQUEST_INFO_DATA);
        Assert.assertEquals("POST", requestInfo.getRequestMethod());
    }

    @Test
    public void initializeRequestWithQueryParam() {
        log.info("Initializes the request. And Check query parameters");

        RequestInfo requestInfo = (RequestInfo) request.getAttribute(Constant.REQUEST_INFO_DATA);
        Assert.assertTrue(requestInfo.getQueryStringMap().containsKey("version"));
    }

}
