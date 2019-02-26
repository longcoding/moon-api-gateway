package com.longcoding.moon.configs;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.Lists;
import com.longcoding.moon.interceptors.impl.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

/**
 * Contains settings for the application context.
 * The api-gateway operates based on the interceptor class.
 * The sequence is very important and you need to be more careful when you change it.
 *
 * @author longcoding
 */

@Configuration
public class MoonServletConfig implements WebMvcConfigurer {

    /**
     * The internal API and swagger require settings to avoid interceptors for the rest api.
     * The internal api and swagger settings are likely to change.
     */

    private static final List<String> EXCLUDE_PATH_INTERNAL_API = Arrays.asList("/internal/**", "/");
    private static final List<String> EXCLUDE_PATH_SWAGGER_UI = Arrays.asList("/swagger-ui.html", "/swagger-resources/**", "/v2/api-docs", "/webjars/**", "/error", "/csrf", "/");
    private static List<String> EXCLUDE_TOTAL_PATH = Lists.newArrayList();

    static {
        EXCLUDE_TOTAL_PATH.addAll(EXCLUDE_PATH_INTERNAL_API);
        EXCLUDE_TOTAL_PATH.addAll(EXCLUDE_PATH_SWAGGER_UI);
    }

    /**
     * It is the first interceptor to meet when a request comes in.
     * It is responsible for copying header, body, ip and various params of request.
     */
    @Bean
    public InitializeInterceptor initializeInterceptor() { return new InitializeInterceptor(); }

    /**
     * Is an interceptor responsible for authentication
     * Basically, check whether apikey exists in header or query param and check whether it is valid key.
     *
     * If Ip-ACL is enabled(optional), also check if it is a valid client ip.
     *
     * If you want to get other authentication such as oauth other than basic authentication, you can touch here.
     */
    @Bean
    public AuthenticationInterceptor authenticationInterceptor() { return new AuthenticationInterceptor(); }

    /**
     * Check that the request path is valid.
     * The project has paths to the various APIs and checks to see if they match.
     * It checks to see which API is matched and gets information about the api specification.
     *
     * If it passes through the path match, it judges that the request is valid
     * and starts to prepare the rate limiting service capacity calculation using redis.
     */
    @Bean
    public PathAndAppAndPrepareRedisInterceptor pathAndPrepareRedisInterceptor() { return new PathAndAppAndPrepareRedisInterceptor(); }

    /**
     * calculate the capacity of an outbound service.
     * It is aimed to prevent load of outbound service.
     */
    @Bean
    public ServiceCapacityInterceptor serviceCapacityInterceptor() { return new ServiceCapacityInterceptor(); }

    /**
     * ServiceCapacity interceptor and ApplicationRatelimitInterceptor are preliminary tasks to execute executeRedisValidation.
     * Execute the stored job at once and determine whether the request can be passed to the outbound service.
     * */
    @Bean
    public ExecuteRedisValidationInterceptor executeRedisValidationInterceptor() { return new ExecuteRedisValidationInterceptor(); }

    /**
     * Split the request path by '/' and save it.
     * This is used when calling the API of the outbound service.
     */
    @Bean
    public ExtractRequestPathInterceptor extractRequestInterceptor() { return new ExtractRequestPathInterceptor(); }

    /**
     * Checks and stores the required header and query params defined in the api specification.
     * If the required value is not entered, it is regarded as invalid request.
     * It also stores the values specified as options in the api specification.
     */
    @Bean
    public HeaderAndQueryValidationInterceptor headerAndQueryValidationInterceptor() { return new HeaderAndQueryValidationInterceptor(); }

    /**
     * Create a new request to make api call with outbound service.
     * It reassembles the header, query, and body of the user request.
     */
    @Bean
    public PrepareProxyInterceptor prepareProxyInterceptor() { return new PrepareProxyInterceptor(); }

    /**
     * Calculate the application's ratelimiting. This is to prevent one api key from making too many requests.
     * Later, it may lead to a billing model.
     */
    @Bean
    public ApplicationRatelimitInterceptor applicationRatelimitInterceptor() { return new ApplicationRatelimitInterceptor(); }

    /**
     * Also check the service contract.
     * Check that the api key can call the api (belonging to a specific service).
     * The api key establishes a relationship in service units. service has various api.
     */
    @Bean
    public ServiceContractValidationInterceptor serviceContractValidationInterceptor() { return new ServiceContractValidationInterceptor(); }

    /**
     * Changes the location of variables obtained through user request.
     * Change the location of input (header, body, query param) to proxy input for outbound service (header, body, query param).
     * To access the body, the content type of the user request should be 'application/json' unconditionally.
     * Access to the body is not recommended for performance.
     */
    @Bean
    public TransformRequestInterceptor transformRequestInterceptor() { return new TransformRequestInterceptor(); }

//    @Bean
//    public EhcacheConfigureFactory ehcacheConfigureFactory() { return new EhcacheConfigureFactory(); }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(initializeInterceptor()).excludePathPatterns(EXCLUDE_TOTAL_PATH);
        registry.addInterceptor(authenticationInterceptor()).excludePathPatterns(EXCLUDE_TOTAL_PATH);
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

    /**
     * Force the format of the response to json via messagecoverter.
    */

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

    /**
     * Defines a resourceHandler for the swagger.
     */

    @Override
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //for swagger
        registry.addResourceHandler("/swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    /**
     * Defines api for health check.
     */

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addStatusController("/valid", HttpStatus.OK);
    }

    @Override
    public void configureMessageConverters( List<HttpMessageConverter<?>> converters ) {
        converters.add(converter());
    }

}
