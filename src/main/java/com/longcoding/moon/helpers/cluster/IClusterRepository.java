package com.longcoding.moon.helpers.cluster;

import com.longcoding.moon.models.ehcache.ApiInfo;
import com.longcoding.moon.models.ehcache.AppInfo;
import com.longcoding.moon.models.ehcache.ServiceInfo;

import java.util.List;

public interface IClusterRepository {

    boolean setApiInfo(ApiInfo apiInfo);

    boolean modifyApiInfo(ApiInfo apiInfo);

    ApiInfo getApiInfo(int apiId);

    List<ApiInfo> getAllApiInfo();

    boolean removeApiInfo(int apiId);

    AppInfo setAppInfo(AppInfo appInfo);

    AppInfo getAppInfo(int appId);

    List<AppInfo> getAllAppInfo();

    boolean removeAppInfo(int appId);

    boolean modifyAppInfo(AppInfo appInfo);

    ServiceInfo setServiceInfo(ServiceInfo serviceInfo);

    boolean modifyServiceInfo(ServiceInfo serviceInfo);

    ServiceInfo getServiceInfo(int serviceId);

    List<ServiceInfo> getAllServiceInfo();

}
