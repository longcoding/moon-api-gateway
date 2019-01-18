package com.longcoding.undefined.helpers;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.ehcache.Cache;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
@Slf4j
public class AclIpChecker implements InitializingBean {

    @Autowired
    APIExposeSpecification apiExposeSpecification;

    private static Cache<String, String> ACL_IP_CACHE;

    @Override
    public void afterPropertiesSet() throws Exception {
        ResourcePoolsBuilder resourcePoolsBuilder = ResourcePoolsBuilder.newResourcePoolsBuilder()
                .heap(10000, EntryUnit.ENTRIES)
                .disk(1000000, MemoryUnit.MB, false);

        ACL_IP_CACHE = apiExposeSpecification.getCacheManager().createCache(Const.ACL_IP_CHECKER, CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, String.class, resourcePoolsBuilder).build());
    }

    public boolean isAllowedPartnerAndIp(String appId, String remoteIp) {
        String aclKey = getAclKey(appId, remoteIp);
        return ACL_IP_CACHE.containsKey(aclKey);
    }

    // TODO: synchronizedIP List between Server and DB(or another persistence)
    private void synchronizedIpMap() { }

    protected void enrolledInitAclIp(String appId, List<String> remoteIp) {
        remoteIp.forEach(ip -> enrolledInitAclIp(appId, ip));
    }

    protected void enrolledInitAclIp(String appId, String remoteIp) {
        ACL_IP_CACHE.putIfAbsent(getAclKey(appId, remoteIp), "");
    }

    private String getAclKey(String appId, String remoteIp) {
        return String.join("-", appId, remoteIp);
    }

}
