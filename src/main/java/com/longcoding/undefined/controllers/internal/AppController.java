package com.longcoding.undefined.controllers.internal;

import com.longcoding.undefined.models.ehcache.AppInfo;
import com.longcoding.undefined.models.internal.EnrollApp;
import com.longcoding.undefined.models.internal.EnrollWhitelistIp;
import com.longcoding.undefined.services.internal.AppService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Internal API for managing Application information.
 * Any node in the cluster can receive these requests.
 * When it receives the request, it sends the event to other nodes.
 *
 * The model object for the client request differs from the model object for the service layer.
 *
 * @author longcoding
 */

@Slf4j
@RestController
@RequestMapping(value = "/internal/apps")
public class AppController {

    @Autowired
    AppService appService;

    /**
     * Register a new application. This action does not directly apply to the cache.
     * When a new application registration request is received, it issues an event to other nodes including itself.
     *
     * @param enrollApp Application information model for client requests.
     * @return An application information model for internal processing.
     */
    @RequestMapping(method = RequestMethod.POST)
    public AppInfo createApp(@RequestBody EnrollApp enrollApp) { return appService.createApp(enrollApp); }

    /**
     * Delete existing application information. This action does not directly apply to the cache.
     * When a new application update request is received, it issues an event to other nodes including itself.
     *
     * @param appId Application Id.
     * @return Returns success or failure.
     */
    @RequestMapping(value = "{appId}", method = RequestMethod.DELETE)
    public boolean deleteApp(@PathVariable String appId) {
        return appService.deleteApp(appId);
    }

    /**
     * View existing application information.
     *
     * @param appId Application Id.
     * @return An application information model for internal processing.
     */
    @RequestMapping(value = "{appId}", method = RequestMethod.GET)
    public AppInfo getAppInfo(@PathVariable String appId) { return appService.getAppInfo(appId); }

    /**
     * Destroys the api key of the corresponding application.
     * When a new application update request is received, it issues an event to other nodes including itself.
     *
     * @param appId Application Id.
     * @return Returns success or failure.
     */
    @RequestMapping(value = "{appId}/apiKey", method = RequestMethod.DELETE)
    public boolean expireApiKey(@PathVariable String appId) {
        return appService.expireApiKey(appId);
    }

    /**
     * Regenerate the api key of the corresponding application.
     * This includes creating a new UUID for the api key.
     * When a new application update request is received, it issues an event to other nodes including itself.
     *
     * @param appId Application Id.
     * @return An application information model for internal processing.
     */
    @RequestMapping(value = "{appId}/apiKey", method = RequestMethod.PUT)
    public AppInfo refreshApiKey(@PathVariable String appId) { return appService.refreshApiKey(appId); }

    /**
     * Add a new whitelist ip to the application.
     * When a new application update request is received, it issues an event to other nodes including itself.
     * When a new whitelist ip is actually registered on the node, the client can call api with that ip.
     *
     * @param enrollWhitelistIps The application whitelist model for client requests. It contains an appId.
     * @return Returns success or failure.
     */
    @RequestMapping(value = "{appId}/whitelist", method = RequestMethod.POST)
    public boolean addWhitelistIps(@RequestBody EnrollWhitelistIp enrollWhitelistIps) { return appService.addWhiteIps(enrollWhitelistIps); }

    /**
     * Delete the new whitelist ip in the application.
     * When a new application update request is received, it issues an event to other nodes including itself.
     * If the deleted whitelist ip is actually applied to the node cache, the client will not be able to call api with that ip.
     *
     * @param enrollWhitelistIps The application whitelist model for client requests. It contains an appId.
     * @return Returns success or failure.
     */
    @RequestMapping(value = "{appId}/whitelist", method = RequestMethod.DELETE)
    public boolean removeWhitelistIps(@RequestBody EnrollWhitelistIp enrollWhitelistIps) { return appService.removeWhiteIps(enrollWhitelistIps); }

}
