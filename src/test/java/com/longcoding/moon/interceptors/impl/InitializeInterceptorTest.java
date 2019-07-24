package com.longcoding.moon.interceptors.impl;

import com.longcoding.moon.helpers.Constant;
import com.longcoding.moon.models.RequestInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.util.MimeTypeUtils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class InitializeInterceptorTest {

    private final Logger log = LogManager.getLogger(getClass());

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
        assertThat(requestInfo.getHeaders().get("apikey"), equalTo("5000-5000-5000-5000"));
    }

    @Test
    public void initializeRequestWithHeaders() {
        log.info("Initializes the request. And Check headers");

        RequestInfo requestInfo = (RequestInfo) request.getAttribute(Constant.REQUEST_INFO_DATA);
        assertThat(requestInfo.getHeaders().size(), equalTo(2));
    }

    @Test
    public void initializeRequestWithHttpMethod() {
        log.info("Initializes the request. And Check http Method");

        RequestInfo requestInfo = (RequestInfo) request.getAttribute(Constant.REQUEST_INFO_DATA);
        assertThat(requestInfo.getRequestMethod(), equalTo("POST"));
    }

    @Test
    public void initializeRequestWithQueryParam() {
        log.info("Initializes the request. And Check query parameters");

        RequestInfo requestInfo = (RequestInfo) request.getAttribute(Constant.REQUEST_INFO_DATA);
        assertThat(requestInfo.getQueryStringMap().containsKey("version"), is(true));
    }

}
