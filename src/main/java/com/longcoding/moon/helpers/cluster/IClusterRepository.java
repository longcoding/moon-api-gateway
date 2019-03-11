package com.longcoding.moon.helpers.cluster;

import com.longcoding.moon.models.ehcache.ApiInfo;
import com.longcoding.moon.models.ehcache.AppInfo;
import com.longcoding.moon.models.ehcache.ServiceInfo;

import java.util.List;

public interface IClusterRepository {

    boolean setApiInfo(ApiInfo apiInfo);

    ApiInfo getApiInfo(String apiId);

    List<ApiInfo> getAllApiInfo();

    boolean removeApiInfo(String apiId);

    AppInfo setAppInfo(AppInfo appInfo);

    AppInfo getAppInfo(String appId);

    List<AppInfo> getAllAppInfo();

    boolean removeAppInfo(String appId);

    boolean modifyAppInfo(AppInfo appInfo);

    boolean setServiceInfo(ServiceInfo serviceInfo);

    ServiceInfo getServiceInfo(String serviceId);

    List<ServiceInfo> getAllServiceInfo();

}
