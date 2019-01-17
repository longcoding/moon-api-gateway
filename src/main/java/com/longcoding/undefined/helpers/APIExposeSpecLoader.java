package com.longcoding.undefined.helpers;

import com.google.common.collect.Lists;
import com.longcoding.undefined.configs.APIExposeSpecConfig;
import com.longcoding.undefined.models.apis.TransformData;
import com.longcoding.undefined.models.cluster.ApiSync;
import com.longcoding.undefined.models.ehcache.ApiInfo;
import com.longcoding.undefined.models.ehcache.ServiceInfo;
import com.longcoding.undefined.models.enumeration.SyncType;
import com.longcoding.undefined.models.enumeration.TransformType;
import com.longcoding.undefined.services.sync.SyncService;
import lombok.extern.slf4j.Slf4j;
import org.ehcache.impl.internal.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import javax.annotation.PostConstruct;
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
public class APIExposeSpecLoader {

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

    @PostConstruct
    void loadAPIExposeSpecifications() {

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
            }
        }

        if ( !apiExposeSpecConfig.isInitEnable() ) return ;

        //Enroll Service expose
        apiExposeSpecConfig.getServices().forEach(service -> {
            ServiceInfo serviceInfo = ServiceInfo.builder()
                    .serviceId(service.getServiceId())
                    .serviceName(service.getServiceName())
                    .minutelyCapacity(String.valueOf(service.getServiceMinutelyCapacity()))
                    .dailyCapacity(String.valueOf(service.getServiceDailyCapacity()))
                    .servicePath(service.getServicePath())
                    .build();
            apiExposeSpecification.getServiceInfoCache().put(String.valueOf(service.getServiceId()), serviceInfo);
            jedisFactory.getInstance().hsetnx(Const.REDIS_KEY_INTERNAL_SERVICE_INFO, service.getServiceId(), JsonUtil.fromJson(serviceInfo));
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

                    List<TransformData> transformRequests = Lists.newArrayList();
                    Map<String, String[]> transformRequest = apiSpec.getTransform();
                    if (Objects.nonNull(transformRequest)) {
                        transformRequest.forEach((targetValue, transformPoint) -> {
                            TransformData transformData = TransformData.builder()
                                    .targetValue(targetValue)
                                    .currentPoint(TransformType.of(transformPoint[0]))
                                    .targetPoint(TransformType.of(transformPoint[1]))
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
                                .inboundURL(apiSpec.getInboundUrl())
                                .outboundURL(apiSpec.getOutboundUrl())
                                .inboundMethod(apiSpec.getMethod())
                                .outboundMethod(apiSpec.getMethod())
                                .protocol(protocol)
                                .isOpenApi(true)
                                .transformData(transformRequests)
                                .build();

                        jedisFactory.getInstance().hsetnx(Const.REDIS_KEY_INTERNAL_API_INFO, String.join("-", String.valueOf(apiInfo.getApiId()), protocol), JsonUtil.fromJson(apiInfo));
                        apiExposeSpecification.getApiInfoCache().put(apiSpec.getApiId(), apiInfo);
                    });
        }));

        //Enroll API Routing URL
        apiExposeSpecConfig.getServices().forEach(service -> service.getApis().forEach(apiSpec -> {

            String routingUrl = apiSpec.getInboundUrl();
            String routingPathInRegex = HttpHelper.getRoutingRegex(routingUrl);

            String servicePath = (service.getServicePath().startsWith("/"))? service.getServicePath() : "/" + service.getServicePath();
            Pattern routingUrlInRegex = Pattern.compile(servicePath + routingPathInRegex);
            apiSpec.getProtocol().forEach(protocol ->
                    apiExposeSpecification.getApiIdCache(protocol + apiSpec.getMethod()).put(apiSpec.getApiId(), routingUrlInRegex));

        }));

    }

}
