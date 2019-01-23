package com.longcoding.undefined.helpers;

import com.google.common.collect.Lists;
import com.longcoding.undefined.configs.APIExposeSpecConfig;
import com.longcoding.undefined.exceptions.ExceptionType;
import com.longcoding.undefined.exceptions.GeneralException;
import com.longcoding.undefined.models.apis.TransformData;
import com.longcoding.undefined.models.cluster.ApiSync;
import com.longcoding.undefined.models.ehcache.ApiInfo;
import com.longcoding.undefined.models.ehcache.ServiceInfo;
import com.longcoding.undefined.models.ehcache.ServiceRoutingInfo;
import com.longcoding.undefined.models.enumeration.RoutingType;
import com.longcoding.undefined.models.enumeration.SyncType;
import com.longcoding.undefined.models.enumeration.TransformType;
import com.longcoding.undefined.services.sync.SyncService;
import lombok.extern.slf4j.Slf4j;
import org.ehcache.impl.internal.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Created by longcoding on 19. 1. 1..
 */

@Slf4j
@Component
@EnableConfigurationProperties(APIExposeSpecConfig.class)
public class APIExposeSpecLoader implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    APIExposeSpecConfig apiExposeSpecConfig;

    @Autowired
    APIExposeSpecification apiExposeSpecification;

    @Autowired
    SyncService syncService;

    @Value("${undefined.service.ip-acl-enable}")
    Boolean enableIpAcl;

    @Value("${undefined.service.cluster.enable}")
    Boolean enableCluster;

    @Autowired
    JedisFactory jedisFactory;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {

        APIExposeSpecification.setIsEnabledIpAcl(enableIpAcl);

        if (enableCluster) {
            try (Jedis jedisClient = jedisFactory.getInstance()) {
                jedisClient.hgetAll(Const.REDIS_KEY_INTERNAL_SERVICE_INFO)
                        .forEach((key, serviceInString) -> {
                            ServiceInfo serviceInfo = JsonUtil.fromJson(serviceInString, ServiceInfo.class);
                            apiExposeSpecification.getServiceInfoCache().put(String.valueOf(serviceInfo.getServiceId()), serviceInfo);
                        });


                jedisClient.hgetAll(Const.REDIS_KEY_INTERNAL_API_INFO)
                        .forEach((key, apiInString) -> {
                            ApiInfo apiInfo = JsonUtil.fromJson(apiInString, ApiInfo.class);
                            syncService.syncApiInfoToCache(new ApiSync(SyncType.CREATE, apiInfo));
                        });
            } catch (Exception ex) {
                throw new GeneralException(ExceptionType.E_1203_FAIL_CLUSTER_SYNC, ex);
            }
        }

        if ( !apiExposeSpecConfig.isInitEnable() ) return ;

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
                jedisFactory.getInstance().hsetnx(Const.REDIS_KEY_INTERNAL_SERVICE_INFO, service.getServiceId(), JsonUtil.fromJson(serviceInfo));
            });
        } catch (Exception ex) {
            throw new GeneralException(ExceptionType.E_1200_FAIL_SERVICE_INFO_CONFIGURATION_INIT, ex);
        }

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

                    apiSpec.getProtocol().forEach(protocol -> {
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
                                .protocol(protocol)
                                .isOpenApi(true)
                                .transformData(transformRequests)
                                .build();

                        jedisFactory.getInstance().hsetnx(Const.REDIS_KEY_INTERNAL_API_INFO, String.join("-", String.valueOf(apiInfo.getApiId()), protocol), JsonUtil.fromJson(apiInfo));
                        apiExposeSpecification.getApiInfoCache().put(apiSpec.getApiId(), apiInfo);
                    });
                });
            });

            //Enroll API Routing URL
            apiExposeSpecConfig.getServices().forEach(service -> {
                if (Objects.isNull(service.getApis())) return ;
                service.getApis().forEach(apiSpec -> {

                    String routingUrl = apiSpec.getInboundUrl();
                    String routingPathInRegex = HttpHelper.getRoutingRegex(routingUrl);

                    String servicePath = (service.getServicePath().startsWith("/"))? service.getServicePath() : "/" + service.getServicePath();
                    Pattern routingUrlInRegex = Pattern.compile(servicePath + routingPathInRegex);

                    apiSpec.getProtocol().forEach(protocol ->
                            apiExposeSpecification.getApiIdCache(protocol + apiSpec.getMethod()).put(apiSpec.getApiId(), routingUrlInRegex));
                });
            });
        } catch (Exception ex) {
            throw new GeneralException(ExceptionType.E_1201_FAIL_API_INFO_CONFIGURATION_INIT, ex);
        }
    }

}
