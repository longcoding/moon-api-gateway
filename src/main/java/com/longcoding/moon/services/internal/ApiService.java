package com.longcoding.moon.services.internal;

import com.longcoding.moon.helpers.Constant;
import com.longcoding.moon.helpers.JsonUtil;
import com.longcoding.moon.helpers.ClusterSyncUtil;
import com.longcoding.moon.helpers.IClusterRepository;
import com.longcoding.moon.models.cluster.ApiSync;
import com.longcoding.moon.models.ehcache.ApiInfo;
import com.longcoding.moon.models.ehcache.ApiMetaInfo;
import com.longcoding.moon.models.enumeration.ProtocolType;
import com.longcoding.moon.models.enumeration.SyncType;
import com.longcoding.moon.models.internal.EnrollApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
    ClusterSyncUtil clusterSyncUtil;

    @Autowired
    IClusterRepository clusterRepository;

    /**
     * Create new api specification information and reflect it in redis.
     * It sends events to all nodes in the cluster using redis.
     *
     * @param syncType Enum type to select CRUD.
     * @param enrollApi model for client request.
     * @return Reflected api specification information model.
     */
    public ApiInfo createOrModifyApi(SyncType syncType, EnrollApi enrollApi) {
        ApiInfo apiInfo = convertedEnrollApiToApiInfo(enrollApi);
        ApiSync apiSync = new ApiSync(syncType, apiInfo);

        if (SyncType.CREATE == syncType) clusterRepository.setApiInfo(apiInfo);
        else if (SyncType.UPDATE == syncType) clusterRepository.modifyApiInfo(apiInfo);
        clusterSyncUtil.setexInfoToHealthyNode(Constant.REDIS_KEY_API_UPDATE, Constant.SECOND_OF_HOUR, JsonUtil.fromJson(apiSync));

        return apiInfo;
    }

    /**
     * Delete existing api specification information from redis.
     * It then sends a delete event to all nodes in the cluster using redis.
     *
     * @param apiId Api Id.
     * @return Returns success or failure.
     */
    public boolean deleteApi(int apiId) {
        ApiInfo apiInfo = new ApiInfo();
        apiInfo.setApiId(apiId);

        clusterSyncUtil.setexInfoToHealthyNode(Constant.REDIS_KEY_API_UPDATE, Constant.SECOND_OF_HOUR, JsonUtil.fromJson(new ApiSync(SyncType.DELETE, apiInfo)));
        return clusterRepository.removeApiInfo(apiId);
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
                .apiId(enrollApi.getApiId())
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
    public ApiInfo selectApi(int apiId) {
        return clusterRepository.getApiInfo(apiId);
    }

    public List<ApiMetaInfo> getAllApiInfo() {
        return clusterRepository.getAllApiInfo().stream().map(apiInfo -> new ApiMetaInfo(
                apiInfo.getApiId(),
                apiInfo.getApiName(),
                apiInfo.getInboundURL(),
                apiInfo.getOutboundURL(),
                apiInfo.getInboundMethod(),
                apiInfo.getOutboundMethod(),
                apiInfo.isOpenApi()
        )).collect(Collectors.toList());
    }

}
