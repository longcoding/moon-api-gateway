package com.longcoding.moon.services.internal;

import com.longcoding.moon.helpers.Constant;
import com.longcoding.moon.helpers.JsonUtil;
import com.longcoding.moon.helpers.ClusterSyncUtil;
import com.longcoding.moon.helpers.IClusterRepository;
import com.longcoding.moon.models.cluster.ServiceSync;
import com.longcoding.moon.models.ehcache.ServiceInfo;
import com.longcoding.moon.models.enumeration.RoutingType;
import com.longcoding.moon.models.enumeration.SyncType;
import com.longcoding.moon.models.internal.EnrollService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by longcoding on 19. 6. 11..
 */

@Slf4j
@Service
public class ServiceService {

    @Autowired
    IClusterRepository clusterRepository;

    @Autowired
    ClusterSyncUtil clusterSyncUtil;

    public ServiceInfo createOrUpdateService(SyncType syncType, EnrollService enrollService) {
        ServiceInfo serviceInfo = convertedEnrollServiceToServiceInfo(enrollService);

        ServiceInfo storedServiceInfo = clusterRepository.setServiceInfo(serviceInfo);

        if (SyncType.CREATE == syncType) clusterRepository.setServiceInfo(storedServiceInfo);
        else if (SyncType.UPDATE == syncType) clusterRepository.modifyServiceInfo(storedServiceInfo);
        ServiceSync serviceSync = new ServiceSync(syncType, storedServiceInfo);
        clusterSyncUtil.setexInfoToHealthyNode(Constant.REDIS_KEY_SERVICE_UPDATE, Constant.SECOND_OF_HOUR, JsonUtil.fromJson(serviceSync));
        return storedServiceInfo;
    }

    public ServiceInfo getServiceInfo(int serviceId) {
        return clusterRepository.getServiceInfo(serviceId);
    }

    private ServiceInfo convertedEnrollServiceToServiceInfo(EnrollService enrollService) {
        return ServiceInfo.builder()
                .serviceId(enrollService.getServiceId())
                .serviceName(enrollService.getServiceName())
                .servicePath(enrollService.getServicePath())
                .routingType(enrollService.isSkipApiTransform()? RoutingType.SKIP_API_TRANSFORM: RoutingType.API_TRANSFER)
                .outboundServiceHost(enrollService.getOutboundServiceHost())
                .dailyCapacity(enrollService.getDailyCapacity())
                .minutelyCapacity(enrollService.getMinutelyCapacity())
                .build();
    }

}

