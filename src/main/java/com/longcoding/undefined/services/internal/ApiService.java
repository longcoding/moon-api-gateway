package com.longcoding.undefined.services.internal;

import com.google.common.collect.Lists;
import com.longcoding.undefined.helpers.ClusterSyncUtil;
import com.longcoding.undefined.helpers.Const;
import com.longcoding.undefined.helpers.JedisFactory;
import com.longcoding.undefined.helpers.JsonUtil;
import com.longcoding.undefined.models.cluster.ApiSync;
import com.longcoding.undefined.models.ehcache.ApiInfo;
import com.longcoding.undefined.models.enumeration.ProtocolType;
import com.longcoding.undefined.models.enumeration.SyncType;
import com.longcoding.undefined.models.internal.EnrollApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by longcoding on 19. 1. 17..
 */

@Slf4j
@Service
public class ApiService {

    @Autowired
    JedisFactory jedisFactory;

    @Autowired
    ClusterSyncUtil clusterSyncUtil;

    public ApiInfo createOrModifyApi(SyncType syncType, EnrollApi enrollApi) {
        ApiInfo apiInfo;
        try (Jedis jedis = jedisFactory.getInstance()) {
            //TODO: validation

            apiInfo = convertedEnrollApiToApiInfo(enrollApi);
            ApiSync apiSync = new ApiSync(syncType, apiInfo);

            jedis.hset(Const.REDIS_KEY_INTERNAL_API_INFO, apiInfo.getApiId(), JsonUtil.fromJson(apiInfo));
            clusterSyncUtil.setexInfoToHealthyNode(Const.REDIS_KEY_API_UPDATE, Const.SECOND_OF_HOUR, JsonUtil.fromJson(apiSync));
        }

        return apiInfo;
    }

    public boolean deleteApi(String apiId) {
        try (Jedis jedis = jedisFactory.getInstance()) {

            ApiInfo apiInfo = new ApiInfo();
            apiInfo.setApiId(apiId);

            clusterSyncUtil.setexInfoToHealthyNode(Const.REDIS_KEY_API_UPDATE, Const.SECOND_OF_HOUR, JsonUtil.fromJson(new ApiSync(SyncType.DELETE, apiInfo)));
            return jedis.hdel(Const.REDIS_KEY_INTERNAL_API_INFO, apiId) == 1;
        }
    }

    private ApiInfo convertedEnrollApiToApiInfo(EnrollApi enrollApi) {
        return ApiInfo.builder()
                .apiId(String.valueOf(enrollApi.getApiId()))
                .apiName(enrollApi.getApiName())
                .serviceId(enrollApi.getServiceId())
                .headers(enrollApi.getHeaders())
                .queryParams(enrollApi.getQueryParams())
                .inboundMethod(enrollApi.getInboundMethod())
                .outboundMethod(enrollApi.getOutboundMethod())
                .inboundURL(enrollApi.getInboundURL())
                .outboundURL(enrollApi.getOutboundURL())
                .protocol(enrollApi.getProtocol().stream().map(ProtocolType::of).collect(Collectors.toList()))
                .transformData(enrollApi.getTransformData())
                .isOpenApi(enrollApi.isOpenApi())
                .build();
    }

    public ApiInfo selectApi(String apiId) {
        try (Jedis jedis = jedisFactory.getInstance()) {
            String apiInfoInString = jedis.hget(Const.REDIS_KEY_INTERNAL_API_INFO, apiId);

            return JsonUtil.fromJson(apiInfoInString, ApiInfo.class);
        }
    }

}
