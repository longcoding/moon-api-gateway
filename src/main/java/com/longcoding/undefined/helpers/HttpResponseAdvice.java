package com.longcoding.undefined.helpers;

import com.longcoding.undefined.models.CommonResponseEntity;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * A class that replaces all responses with the CommonResponseEntity class and returns.
 * Catch all responses using Spring's ResponseBodyAdvice. It then changes to the commonResponseEntity object and returns.
 * If you need to preprocess before returning, you can change this class.
 *
 * @author longcoding
 */

@RestControllerAdvice(annotations = RestController.class)
public class HttpResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType ) {
        return true;
    }

    @Override public Object beforeBodyWrite(Object body, MethodParameter returnType
            , MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType
            , ServerHttpRequest request, ServerHttpResponse response ) {

        return CommonResponseEntity.generate(body);
    }
}
