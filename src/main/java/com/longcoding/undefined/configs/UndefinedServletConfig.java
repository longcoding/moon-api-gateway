package com.longcoding.undefined.configs;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.Lists;
import com.longcoding.undefined.interceptors.impl.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

/**
 * Created by longcoding on 16. 4. 5..
 * Updated by longcoding on 18. 12. 26..
 */
@Configuration
public class UndefinedServletConfig implements WebMvcConfigurer {

    private static final List<String> EXCLUDE_PATH_INTERNAL_API = Arrays.asList("/internal/**", "/");
    private static final List<String> EXCLUDE_PATH_SWAGGER_UI = Arrays.asList("/swagger-ui.html", "/swagger-resources/**", "/v2/api-docs", "/webjars/**", "/error", "/csrf", "/");
    private static List<String> EXCLUDE_TOTAL_PATH = Lists.newArrayList();

    static {
        EXCLUDE_TOTAL_PATH.addAll(EXCLUDE_PATH_INTERNAL_API);
        EXCLUDE_TOTAL_PATH.addAll(EXCLUDE_PATH_SWAGGER_UI);
    }

    @Bean
    public InitializeInterceptor initializeInterceptor() { return new InitializeInterceptor(); }
    @Bean
    public AuthenticationInterceptor authenticationInterceptor() { return new AuthenticationInterceptor(); }
    @Bean
    public PathAndAppAndPrepareRedisInterceptor pathAndPrepareRedisInterceptor() { return new PathAndAppAndPrepareRedisInterceptor(); }
    @Bean
    public ServiceCapacityInterceptor serviceCapacityInterceptor() { return new ServiceCapacityInterceptor(); }
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
        registry.addInterceptor(initializeInterceptor()).excludePathPatterns(EXCLUDE_PATH_SWAGGER_UI);
        registry.addInterceptor(authenticationInterceptor()).excludePathPatterns(EXCLUDE_PATH_SWAGGER_UI);
        registry.addInterceptor(pathAndPrepareRedisInterceptor()).excludePathPatterns(EXCLUDE_TOTAL_PATH);
        registry.addInterceptor(serviceContractValidationInterceptor()).excludePathPatterns(EXCLUDE_TOTAL_PATH);
        registry.addInterceptor(serviceCapacityInterceptor()).excludePathPatterns(EXCLUDE_TOTAL_PATH);
        registry.addInterceptor(applicationRatelimitInterceptor()).excludePathPatterns(EXCLUDE_TOTAL_PATH);
        registry.addInterceptor(executeRedisValidationInterceptor()).excludePathPatterns(EXCLUDE_TOTAL_PATH);
        registry.addInterceptor(extractRequestInterceptor()).excludePathPatterns(EXCLUDE_TOTAL_PATH);
        registry.addInterceptor(headerAndQueryValidationInterceptor()).excludePathPatterns(EXCLUDE_TOTAL_PATH);
        registry.addInterceptor(transformRequestInterceptor()).excludePathPatterns(EXCLUDE_TOTAL_PATH);
        registry.addInterceptor(prepareProxyInterceptor()).excludePathPatterns(EXCLUDE_TOTAL_PATH);
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
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //for swagger
        registry.addResourceHandler("/swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addStatusController("/status", HttpStatus.OK);
    }

    @Override
    public void configureMessageConverters( List<HttpMessageConverter<?>> converters ) {
        converters.add(converter());
    }

}
