package com.longcoding.undefined.services.internal;

import com.longcoding.undefined.helpers.ClusterSyncUtil;
import com.longcoding.undefined.helpers.Constant;
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

import java.util.stream.Collectors;

/**
 * When a request is received, this is a service class that is responsible for sending events to all nodes in the cluster.
 *
 * @author longcoding
 */

@Slf4j
@Service
public class ApiService {

    @Autowired
    JedisFactory jedisFactory;

    @Autowired
    ClusterSyncUtil clusterSyncUtil;

    /**
     * Create new api specification information and reflect it in redis.
     * It sends events to all nodes in the cluster using redis.
     *
     * @param syncType Enum type to select CRUD.
     * @param enrollApi model for client request.
     * @return Reflected api specification information model.
     */
    public ApiInfo createOrModifyApi(SyncType syncType, EnrollApi enrollApi) {
        ApiInfo apiInfo;
        try (Jedis jedis = jedisFactory.getInstance()) {
            //TODO: validation

            apiInfo = convertedEnrollApiToApiInfo(enrollApi);
            ApiSync apiSync = new ApiSync(syncType, apiInfo);

            jedis.hset(Constant.REDIS_KEY_INTERNAL_API_INFO, apiInfo.getApiId(), JsonUtil.fromJson(apiInfo));
            clusterSyncUtil.setexInfoToHealthyNode(Constant.REDIS_KEY_API_UPDATE, Constant.SECOND_OF_HOUR, JsonUtil.fromJson(apiSync));
        }

        return apiInfo;
    }

    /**
     * Delete existing api specification information from redis.
     * It then sends a delete event to all nodes in the cluster using redis.
     *
     * @param apiId Api Id.
     * @return Returns success or failure.
     */
    public boolean deleteApi(String apiId) {
        try (Jedis jedis = jedisFactory.getInstance()) {

            ApiInfo apiInfo = new ApiInfo();
            apiInfo.setApiId(apiId);

            clusterSyncUtil.setexInfoToHealthyNode(Constant.REDIS_KEY_API_UPDATE, Constant.SECOND_OF_HOUR, JsonUtil.fromJson(new ApiSync(SyncType.DELETE, apiInfo)));
            return jedis.hdel(Constant.REDIS_KEY_INTERNAL_API_INFO, apiId) == 1;
        }
    }

    /**
     * Change the model for client request to model for service layer.
     * For internal convenience, the int type may be changed to the string type,
     * or the string type may be changed to the corresponding enum type.
     *
     * @param enrollApi model for client request.
     * @return The newly created api specification model.
     */
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

    /**
     * Retrieves and returns API information in redis(persistence layer).
     *
     * @param apiId Api Id.
     * @return It is the api information model that is inquired.
     */
    public ApiInfo selectApi(String apiId) {
        try (Jedis jedis = jedisFactory.getInstance()) {
            String apiInfoInString = jedis.hget(Constant.REDIS_KEY_INTERNAL_API_INFO, apiId);

            return JsonUtil.fromJson(apiInfoInString, ApiInfo.class);
        }
    }

}
