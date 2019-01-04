package com.longcoding.undefined.configs;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.longcoding.undefined.interceptors.impl.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by longcoding on 16. 4. 5..
 * Updated by longcoding on 18. 12. 26..
 */
@Configuration
public class UndefinedServletConfig implements WebMvcConfigurer {

    @Bean
    public InitializeInterceptor initializeInterceptor() {
        return new InitializeInterceptor();
    }
    @Bean
    public PathAndAppAndPrepareRedisInterceptor pathAndPrepareRedisInterceptor() { return new PathAndAppAndPrepareRedisInterceptor(); }
    @Bean
    public ServiceCapacityInterceptor serviceCapacityInterceptor() {
        return new ServiceCapacityInterceptor();
    }
    @Bean
    public ExecuteRedisValidationInterceptor executeRedisValidationInterceptor() { return new ExecuteRedisValidationInterceptor(); }
    @Bean
    public ExtractRequestInterceptor extractRequestInterceptor() { return new ExtractRequestInterceptor(); }
    @Bean
    public HeaderAndQueryValidationInterceptor headerAndQueryValidationInterceptor() { return new HeaderAndQueryValidationInterceptor(); }
    @Bean
    public PrepareProxyInterceptor prepareProxyInterceptor() { return new PrepareProxyInterceptor(); }
    @Bean
    public ApplicationRatelimitInterceptor applicationRatelimitInterceptor() { return new ApplicationRatelimitInterceptor(); }
    @Bean
    public ServiceContractValidationInterceptor serviceContractValidationInterceptor() { return new ServiceContractValidationInterceptor(); }
    @Bean
    public TransformRequestInterceptor transformRequestInterceptor() { return new TransformRequestInterceptor(); }

//    @Bean
//    public EhcacheConfigureFactory ehcacheConfigureFactory() { return new EhcacheConfigureFactory(); }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(initializeInterceptor());
        registry.addInterceptor(pathAndPrepareRedisInterceptor());
        registry.addInterceptor(serviceContractValidationInterceptor());
        registry.addInterceptor(serviceCapacityInterceptor());
        registry.addInterceptor(applicationRatelimitInterceptor());
        registry.addInterceptor(executeRedisValidationInterceptor());
        registry.addInterceptor(extractRequestInterceptor());
        registry.addInterceptor(headerAndQueryValidationInterceptor());
        registry.addInterceptor(transformRequestInterceptor());
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
