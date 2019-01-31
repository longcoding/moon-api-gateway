package com.longcoding.undefined.configs;

import com.longcoding.undefined.helpers.Constant;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.Filter;

/**
 * Contains the servlet settings for the project.
 *
 * @author longcoding
 */

@EnableConfigurationProperties
public class UndefinedInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() { return new Class[] { UndefinedRootConfig.class }; }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[] { UndefinedServletConfig.class };
    }

    @Override
    protected String[] getServletMappings() {
        return new String[] { "/" };
    }

    /**
     * The project has set the default encodingType to UTF-8 for Web requests and responses via CharacterEncodingFilter.
     */

    @Override
    protected Filter[] getServletFilters() {
        CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter();
        encodingFilter.setEncoding(Constant.SERVER_DEFAULT_ENCODING_TYPE);
        encodingFilter.setForceEncoding(true);
        return new Filter[] { encodingFilter };
    }
}
