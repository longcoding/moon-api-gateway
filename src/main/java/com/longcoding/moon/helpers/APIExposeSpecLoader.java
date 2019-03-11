package com.longcoding.moon.helpers;

import com.google.common.collect.Lists;
import com.longcoding.moon.configs.APIExposeSpecConfig;
import com.longcoding.moon.exceptions.ExceptionType;
import com.longcoding.moon.exceptions.GeneralException;
import com.longcoding.moon.helpers.cluster.IClusterRepository;
import com.longcoding.moon.models.apis.TransformData;
import com.longcoding.moon.models.cluster.ApiSync;
import com.longcoding.moon.models.ehcache.ApiInfo;
import com.longcoding.moon.models.ehcache.ServiceInfo;
import com.longcoding.moon.models.enumeration.*;
import com.longcoding.moon.services.sync.SyncService;
import com.longcoding.moon.models.ehcache.ServiceRoutingInfo;
import lombok.extern.slf4j.Slf4j;
import org.ehcache.impl.internal.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * A class for loading all API specification information into the cache along with application loading.
 * Regardless of cluster mode, it is loaded based on application-apis.yml by default.
 * If an exception occurs, make sure that the configuration file is not corrupted.
 * If you use the cluster mode, you do not need to load the configuration file every time.
 *
 * @author longcoding
 */

@Slf4j
@Component
@EnableConfigurationProperties(APIExposeSpecConfig.class)
public class APIExposeSpecLoader implements InitializingBean {

    @Autowired
    APIExposeSpecConfig apiExposeSpecConfig;

    @Autowired
    APIExposeSpecification apiExposeSpecification;

    @Autowired
    SyncService syncService;

    @Value("${moon.service.ip-acl-enable}")
    Boolean enableIpAcl;

    @Value("${moon.service.cluster.enable}")
    Boolean enableCluster;

    @Autowired
    IClusterRepository clusterRepository;

    @Override
    public void afterPropertiesSet() throws Exception {

        // Whether to use IP-ACLs.
        // Set in application.yml.
        APIExposeSpecification.setIsEnabledIpAcl(enableIpAcl);

        // In cluster mode, the API information stored in the persistence layer is fetched and stored in the cache.
        if (enableCluster) {
            try {
                clusterRepository.getAllServiceInfo()
                        .forEach(serviceInfo -> apiExposeSpecification.getServiceInfoCache().put(String.valueOf(serviceInfo.getServiceId()), serviceInfo));

                clusterRepository.getAllApiInfo()
                        .forEach(apiInfo -> syncService.syncApiInfoToCache(new ApiSync(SyncType.CREATE, apiInfo)));
            } catch (Exception ex) {
                throw new GeneralException(ExceptionType.E_1203_FAIL_CLUSTER_SYNC, ex);
            }
        }

        // Whether to load the api specification information in the configuration file.
        // There is no need to load each time.
        if ( !apiExposeSpecConfig.isInitEnable() ) return ;

        // Loads the service information in the configuration file into the cache.
        try {
            //Enroll Service expose
            apiExposeSpecConfig.getServices().forEach(service -> {

                RoutingType serviceRoutingType = service.isOnlyPassRequestWithoutTransform()? RoutingType.SKIP_API_TRANSFORM : RoutingType.API_TRANSFER;
                String servicePath = service.getServicePath().startsWith("/")? service.getServicePath().substring(1) : service.getServicePath();
                apiExposeSpecification.getServiceTypeCache().put(servicePath, new ServiceRoutingInfo(service.getServiceId(), serviceRoutingType));

                ServiceInfo serviceInfo = ServiceInfo.builder()
                        .serviceId(service.getServiceId())
                        .serviceName(service.getServiceName())
                        .minutelyCapacity(String.valueOf(service.getServiceMinutelyCapacity()))
                        .dailyCapacity(String.valueOf(service.getServiceDailyCapacity()))
                        .servicePath(service.getServicePath())
                        .outboundServiceHost(service.getOutboundServiceHost())
                        .routingType(serviceRoutingType)
                        .build();
                apiExposeSpecification.getServiceInfoCache().put(String.valueOf(service.getServiceId()), serviceInfo);
                clusterRepository.setServiceInfo(serviceInfo);
            });
        } catch (Exception ex) {
            throw new GeneralException(ExceptionType.E_1200_FAIL_SERVICE_INFO_CONFIGURATION_INIT, ex);
        }

         // Loads the api specification information in the configuration file into the cache.
        try {
            //Enroll API expose
            apiExposeSpecConfig.getServices().forEach(service -> {
                if (Objects.isNull(service.getApis())) return ;
                service.getApis().forEach(apiSpec -> {

                    ConcurrentHashMap<String, Boolean> headers = new ConcurrentHashMap<>();
                    apiSpec.getHeader().forEach(header -> headers.put(header.toLowerCase(), false));
                    apiSpec.getHeaderRequired().forEach(requiredHeader -> headers.replace(requiredHeader.toLowerCase(), true));

                    ConcurrentHashMap<String, Boolean> queryParams = new ConcurrentHashMap<>();
                    apiSpec.getQueryParam().forEach(param -> queryParams.put(param.toLowerCase(), false));
                    apiSpec.getQueryParamRequired().forEach(requiredParam -> queryParams.replace(requiredParam.toLowerCase(), true));

                    List<TransformData> transformRequests = Lists.newArrayList();
                    Map<String, String[]> transformRequest = apiSpec.getTransform();
                    if (Objects.nonNull(transformRequest)) {
                        transformRequest.forEach((targetKey, transformPoint) -> {
                            TransformData transformData = TransformData.builder()
                                    .targetKey(targetKey.toLowerCase())
                                    .currentPoint(TransformType.of(transformPoint[0]))
                                    .targetPoint(TransformType.of(transformPoint[1]))
                                    .newKeyName(transformPoint.length > 2? transformPoint[2]:targetKey.toLowerCase())
                                    .build();
                            transformRequests.add(transformData);
                        });
                    }

                    ApiInfo apiInfo = ApiInfo.builder()
                            .apiId(apiSpec.getApiId())
                            .apiName(apiSpec.getApiName())
                            .serviceId(service.getServiceId())
                            .headers(headers)
                            .queryParams(queryParams)
                            .inboundURL(apiSpec.getInboundUrl().toLowerCase())
                            .outboundURL(service.getOutboundServiceHost() + apiSpec.getOutboundUrl().toLowerCase())
                            .inboundMethod(apiSpec.getMethod())
                            .outboundMethod(apiSpec.getMethod())
                            .protocol(apiSpec.getProtocol().stream().map(ProtocolType::of).collect(Collectors.toList()))
                            .isOpenApi(true)
                            .transformData(transformRequests)
                            .build();

                    clusterRepository.setApiInfo(apiInfo);
                    apiExposeSpecification.getApiInfoCache().put(apiSpec.getApiId(), apiInfo);

                });
            });

            /*
             * Enroll API Routing URL
             * Loads the api path information in the configuration file into the cache.
             * path is made to pattern for regex operations.
             */
            apiExposeSpecConfig.getServices().forEach(service -> {
                if (Objects.isNull(service.getApis())) return ;
                service.getApis().forEach(apiSpec -> {

                    String routingUrl = apiSpec.getInboundUrl();
                    String routingPathInRegex = HttpHelper.getRoutingRegex(routingUrl);

                    String servicePath = (service.getServicePath().startsWith("/"))? service.getServicePath() : "/" + service.getServicePath();
                    Pattern routingUrlInRegex = Pattern.compile(servicePath + routingPathInRegex);

                    apiExposeSpecification.getRoutingPathCache(MethodType.of(apiSpec.getMethod())).put(apiSpec.getApiId(), routingUrlInRegex);

                });
            });
        } catch (Exception ex) {
            throw new GeneralException(ExceptionType.E_1201_FAIL_API_INFO_CONFIGURATION_INIT, ex);
        }
    }
}
