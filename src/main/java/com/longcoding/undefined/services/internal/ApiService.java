package com.longcoding.undefined.services.internal;

import com.google.common.collect.Lists;
import com.longcoding.undefined.helpers.ClusterSyncUtil;
import com.longcoding.undefined.helpers.Const;
import com.longcoding.undefined.helpers.JedisFactory;
import com.longcoding.undefined.helpers.JsonUtil;
import com.longcoding.undefined.models.cluster.ApiSync;
import com.longcoding.undefined.models.ehcache.ApiInfo;
import com.longcoding.undefined.models.enumeration.SyncType;
import com.longcoding.undefined.models.internal.EnrollApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.List;

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

    public List<ApiInfo> createOrModifyApi(SyncType syncType, EnrollApi enrollApi) {
        List<ApiInfo> apiInfos = Lists.newArrayList();
        try (Jedis jedis = jedisFactory.getInstance()) {
            //TODO: validation

            enrollApi.getProtocol().forEach(protocol -> {
                ApiInfo apiInfoWithProtocol = convertedEnrollApiToApiInfo(protocol, enrollApi);
                ApiSync apiSync = new ApiSync(syncType, apiInfoWithProtocol);
                apiInfos.add(apiInfoWithProtocol);

                jedis.hset(Const.REDIS_KEY_INTERNAL_API_INFO, String.join("-", apiInfoWithProtocol.getApiId(), apiInfoWithProtocol.getProtocol()), JsonUtil.fromJson(apiInfoWithProtocol));
                clusterSyncUtil.setexInfoToHealthyNode(Const.REDIS_KEY_API_UPDATE, Const.SECOND_OF_HOUR, JsonUtil.fromJson(apiSync));
            });

        }

        return apiInfos;
    }

    public boolean deleteApi(String apiId) {
        try (Jedis jedis = jedisFactory.getInstance()) {

            ApiInfo apiInfo = new ApiInfo();
            apiInfo.setApiId(apiId);

            clusterSyncUtil.setexInfoToHealthyNode(Const.REDIS_KEY_API_UPDATE, Const.SECOND_OF_HOUR, JsonUtil.fromJson(new ApiSync(SyncType.DELETE, apiInfo)));
            return jedis.hdel(Const.REDIS_KEY_INTERNAL_API_INFO, apiId) == 1;
        }
    }

    private ApiInfo convertedEnrollApiToApiInfo(String protocol, EnrollApi enrollApi) {
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
                .protocol(protocol)
                .transformData(enrollApi.getTransformData())
                .isOpenApi(enrollApi.isOpenApi())
                .build();
    }

}
