package com.longcoding.undefined.configs;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.longcoding.undefined.interceptors.impl.test1;
import com.longcoding.undefined.interceptors.impl.test2;
import com.longcoding.undefined.interceptors.impl.test3;
import com.longcoding.undefined.interceptors.impl.test4;
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
    public test1 test1() {
        return new test1();
    }
    @Bean
    public test2 test2() {
        return new test2();
    }
    @Bean
    public test3 test3() {
        return new test3();
    }
    @Bean
    public test4 test4() {
        return new test4();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(test1());
        registry.addInterceptor(test2());
        registry.addInterceptor(test3());
        registry.addInterceptor(test4());
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