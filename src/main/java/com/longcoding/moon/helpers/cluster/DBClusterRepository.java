package com.longcoding.moon.helpers.cluster;

import com.longcoding.moon.exceptions.ExceptionType;
import com.longcoding.moon.exceptions.GeneralException;
import com.longcoding.moon.models.ehcache.ApiInfo;
import com.longcoding.moon.models.ehcache.AppInfo;
import com.longcoding.moon.models.ehcache.ServiceInfo;
import com.longcoding.moon.models.repository.ApiInfoRepository;
import com.longcoding.moon.models.repository.AppInfoRepository;
import com.longcoding.moon.models.repository.ServiceInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.List;
import java.util.Optional;

@Configuration
@ConditionalOnProperty(prefix = "moon.service.cluster", name = "repository-type", havingValue = "rdb")
@Import({DataSourceAutoConfiguration.class})
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.longcoding.moon.models.repository")
public class DBClusterRepository implements IClusterRepository {

    @Autowired
    AppInfoRepository appInfoRepository;

    @Autowired
    ApiInfoRepository apiInfoRepository;

    @Autowired
    ServiceInfoRepository serviceInfoRepository;

    @Override
    public boolean setApiInfo(ApiInfo apiInfo) {
        boolean exists = apiInfoRepository.existsById(apiInfo.getApiId());
        if (!exists) apiInfoRepository.saveAndFlush(apiInfo);
        return true;
    }

    @Override
    public ApiInfo getApiInfo(int apiId) {
        Optional<ApiInfo> apiInfoOpt = apiInfoRepository.findById(apiId);
        return apiInfoOpt.orElseThrow(() -> new GeneralException(ExceptionType.E_1004_RESOURCE_NOT_FOUND));
    }

    @Override
    public List<ApiInfo> getAllApiInfo() {
        return apiInfoRepository.findAll();
    }

    @Override
    public boolean removeApiInfo(int apiId) {
        apiInfoRepository.deleteById(apiId);
        return true;
    }

    @Override
    public AppInfo setAppInfo(AppInfo appInfo) {
        boolean exists = appInfoRepository.existsById(appInfo.getAppId());
        if (!exists) return appInfoRepository.saveAndFlush(appInfo);
        else return appInfo;
    }

    @Override
    public AppInfo getAppInfo(int appId) {
        Optional<AppInfo> appInfoOpt = appInfoRepository.findById(appId);
        return appInfoOpt.orElseThrow(() -> new GeneralException(ExceptionType.E_1004_RESOURCE_NOT_FOUND));
    }

    @Override
    public List<AppInfo> getAllAppInfo() {
        return appInfoRepository.findAll();
    }

    @Override
    public boolean removeAppInfo(int appId) {
        appInfoRepository.deleteById(appId);
        return true;
    }

    @Override
    public boolean modifyAppInfo(AppInfo appInfo) {
        appInfoRepository.saveAndFlush(appInfo);
        return true;
    }

    @Override
    public boolean setServiceInfo(ServiceInfo serviceInfo) {
        boolean exists = serviceInfoRepository.existsById(serviceInfo.getServiceId());
        if (!exists) serviceInfoRepository.saveAndFlush(serviceInfo);
        return true;
    }

    @Override
    public ServiceInfo getServiceInfo(int serviceId) {
        Optional<ServiceInfo> serviceInfoOpt = serviceInfoRepository.findById(serviceId);
        return serviceInfoOpt.orElseThrow(() -> new GeneralException(ExceptionType.E_1004_RESOURCE_NOT_FOUND));
    }

    @Override
    public List<ServiceInfo> getAllServiceInfo() {
        return serviceInfoRepository.findAll();
    }
}
