package com.longcoding.undefined.helpers;

import lombok.extern.slf4j.Slf4j;
import org.ehcache.Cache;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Class for Application Ip ACLs.
 * AppInfo inside the cache contains ip acl information.
 * Contains ip acl information in the node's cache. The initial load is done via InitAppLoader.
 *
 * @author longcoding
 */

@Component
@Slf4j
public class AclIpChecker implements InitializingBean {

    @Autowired
    APIExposeSpecification apiExposeSpecification;

    private static Cache<String, String> ACL_IP_CACHE;

    /**
     * Create a cache for the IP_ACL in the node.
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        ResourcePoolsBuilder resourcePoolsBuilder = ResourcePoolsBuilder.newResourcePoolsBuilder()
                .heap(10000, EntryUnit.ENTRIES)
                .disk(1000000, MemoryUnit.MB, false);

        ACL_IP_CACHE = apiExposeSpecification.getCacheManager().createCache(Constant.ACL_IP_CHECKER, CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, String.class, resourcePoolsBuilder).build());
    }

    /**
     * Method that determines if the application can request with this remote IP.
     *
     * @param appId The application Id of the client.
     * @param remoteIp The client's IP.
     */
    public boolean isAllowedPartnerAndIp(String appId, String remoteIp) {
        String aclKey = getAclKey(appId, remoteIp);
        return ACL_IP_CACHE.containsKey(aclKey);
    }

    // TODO: synchronizedIP List between Server and DB(or another persistence)
    private void synchronizedIpMap() { }

    /**
     * Register new ips in the whitelist. The criteria is application Id.
     *
     * @param appId The application Id of the client.
     * @param remoteIp The client's IPs.
     */
    protected void enrolledInitAclIp(String appId, List<String> remoteIp) {
        remoteIp.forEach(ip -> enrolledInitAclIp(appId, ip));
    }

    /**
     * Register new ips in the whitelist. The criteria is application Id.
     *
     * @param appId The application Id of the client.
     * @param remoteIp The client's IP.
     */
    protected void enrolledInitAclIp(String appId, String remoteIp) {
        ACL_IP_CACHE.putIfAbsent(getAclKey(appId, remoteIp), "");
    }

    /**
     * Create a key based on appId and remoteIp.
     * The key is a key type managed by the IP-ACL cache.
     *
     * @param appId The application Id of the client.
     * @param remoteIp The client's IP.
     */
    private String getAclKey(String appId, String remoteIp) {
        return String.join("-", appId, remoteIp);
    }

}
