package com.longcoding.undefined.helpers;

import com.longcoding.undefined.configs.APIExposeSpecConfig;
import com.longcoding.undefined.models.ehcache.ApiInfoCache;
import com.longcoding.undefined.models.ehcache.ServiceInfoCache;
import lombok.extern.slf4j.Slf4j;
import org.ehcache.impl.internal.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.StringTokenizer;

/**
 * Created by longcoding on 19. 1. 1..
 */

@Slf4j
@Component
@EnableConfigurationProperties(APIExposeSpecConfig.class)
public class APIExposeSpecLoader {

    @Autowired
    APIExposeSpecConfig apiExposeSpecConfig;

    @Autowired
    APIExposeSpecification apiExposeSpecification;

    @PostConstruct
    void loadAPIExposeSpecifications() {

        //Enroll Service expose
        apiExposeSpecConfig.getServices().forEach(service -> {
            ServiceInfoCache serviceInfo = ServiceInfoCache.builder()
                    .serviceId(service.getServiceId())
                    .serviceName(service.getServiceName())
                    .minutelyCapacity(String.valueOf(service.getServiceMinutelyCapacity()))
                    .dailyCapacity(String.valueOf(service.getServiceDailyCapacity()))
                    .build();
            apiExposeSpecification.getServiceInfoCache().put(String.valueOf(service.getServiceId()), serviceInfo);
        });

        //Enroll API expose
        apiExposeSpecConfig.getServices().forEach(service ->
                service.getApis().forEach(apiSpec -> {

                    ConcurrentHashMap<String, Boolean> headers = new ConcurrentHashMap<>();
                    apiSpec.getHeader().forEach(header -> headers.put(header, false));
                    apiSpec.getHeaderRequired().forEach(requiredHeader -> headers.replace(requiredHeader, true));

                    ConcurrentHashMap<String, Boolean> queryParams = new ConcurrentHashMap<>();
                    apiSpec.getUrlParam().forEach(param -> queryParams.put(param, false));
                    apiSpec.getUrlParamRequired().forEach(requiredParam -> queryParams.replace(requiredParam, true));

                    ApiInfoCache apiInfoCache = ApiInfoCache.builder()
                            .apiId(apiSpec.getApiId())
                            .apiName(apiSpec.getApiName())
                            .serviceId(service.getServiceId())
                            .headers(headers)
                            .queryParams(queryParams)
                            .inboundURL(apiSpec.getInboundUrl())
                            .outboundURL(apiSpec.getOutboundUrl())
                            .inboundMethod(apiSpec.getMethod())
                            .outboundMethod(apiSpec.getMethod())
                            //currently spec
                            .protocol(apiSpec.getProtocol().get(0))
                            .isOpenApi(true)
                            .build();

                    apiExposeSpecification.getApiInfoCache().put(apiSpec.getApiId(), apiInfoCache);
        }));

        //Enroll API Routing URL
        apiExposeSpecConfig.getServices().forEach(service -> service.getApis().forEach(apiSpec -> {

            StringBuilder routingPathInRegex = new StringBuilder();
            String routingUrl = apiSpec.getInboundUrl();
            StringTokenizer st = new StringTokenizer(routingUrl, "/");
            while (st.hasMoreTokens()) {
                String element = st.nextToken();
                if (element.startsWith(":")) element = "[a-zA-Z0-9]+";

                routingPathInRegex.append("/").append(element);
            }

            String servicePath = (service.getServicePath().startsWith("/"))? service.getServicePath() : "/" + service.getServicePath();
            String routingUrlInRegex = servicePath + routingPathInRegex.toString();
            apiSpec.getProtocol().forEach(protocol ->
                    apiExposeSpecification.getApiIdCache(protocol + apiSpec.getMethod()).put(routingUrlInRegex, apiSpec.getApiId()));

        }));

    }

}
