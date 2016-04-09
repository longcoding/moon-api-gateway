package com.longcoding.undefined.configs;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.longcoding.undefined.interceptors.impl.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by longcoding on 16. 4. 5..
 */
@Configuration
public class UndefinedServletConfig extends WebMvcConfigurerAdapter {

    @Bean
    public InitializeInterceptor initializeInterceptor() {
        return new InitializeInterceptor();
    }
    @Bean
    public PathAndPrepareRedisInterceptor pathAndPrepareRedisInterceptor() {
        return new PathAndPrepareRedisInterceptor();
    }
    @Bean
    public RatelimitInterceptor ratelimitInterceptor() {
        return new RatelimitInterceptor();
    }
    @Bean
    public ExecuteRedisValidationInterceptor executeRedisValidationInterceptor() {
        return new ExecuteRedisValidationInterceptor();
    }
    @Bean
    public ExtractRequestInterceptor extractRequestInterceptor() {
        return new ExtractRequestInterceptor();
    }
    @Bean
    public HeaderAndQueryValidationInterceptor headerAndQueryValidationInterceptor() { return new HeaderAndQueryValidationInterceptor(); }
    @Bean
    public PrepareProxyInterceptor prepareProxyInterceptor() { return new PrepareProxyInterceptor(); }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(initializeInterceptor());
        registry.addInterceptor(pathAndPrepareRedisInterceptor());
        registry.addInterceptor(ratelimitInterceptor());
        registry.addInterceptor(executeRedisValidationInterceptor());
        registry.addInterceptor(extractRequestInterceptor());
        registry.addInterceptor(headerAndQueryValidationInterceptor());
        registry.addInterceptor(prepareProxyInterceptor());
    }

    @Bean
    MappingJackson2HttpMessageConverter converter()
    {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES );
        mapper.disable( SerializationFeature.FAIL_ON_EMPTY_BEANS );
        mapper.disable( MapperFeature.DEFAULT_VIEW_INCLUSION );
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        converter.setObjectMapper( mapper );
        return converter;
    }

    @Override
    public void configureMessageConverters( List<HttpMessageConverter<?>> converters ) {
        converters.add(converter());
    }

}