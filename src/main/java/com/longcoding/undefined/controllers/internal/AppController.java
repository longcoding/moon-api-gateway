package com.longcoding.undefined.controllers.internal;

import com.longcoding.undefined.models.ehcache.AppInfo;
import com.longcoding.undefined.models.internal.EnrollApp;
import com.longcoding.undefined.models.internal.EnrollWhitelistIp;
import com.longcoding.undefined.services.internal.AppService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by longcoding on 19. 1. 7..
 */

@Slf4j
@RestController
@RequestMapping(value = "/internal/apps")
public class AppController {

    @Autowired
    AppService appService;

    @RequestMapping(method = RequestMethod.POST)
    public AppInfo createApp(@RequestBody EnrollApp enrollApp) { return appService.createApp(enrollApp); }

    @RequestMapping(value = "{appId}", method = RequestMethod.DELETE)
    public boolean deleteApp(@PathVariable String appId) {
        return appService.deleteApp(appId);
    }

    @RequestMapping(value = "{appId}", method = RequestMethod.GET)
    public AppInfo getAppInfo(@PathVariable String appId) { return appService.getAppInfo(appId); }

    @RequestMapping(value = "{appId}/apiKey", method = RequestMethod.DELETE)
    public boolean expireApiKey(@PathVariable String appId) {
        return appService.expireApiKey(appId);
    }

    @RequestMapping(value = "{appId}/apiKey", method = RequestMethod.PUT)
    public AppInfo refreshApiKey(@PathVariable String appId) { return appService.refreshApiKey(appId); }

    @RequestMapping(value = "{appId}/whitelist", method = RequestMethod.POST)
    public boolean addWhitelistIps(@RequestBody EnrollWhitelistIp enrollWhitelistIps) { return appService.addWhiteIps(enrollWhitelistIps); }

    @RequestMapping(value = "{appId}/whitelist", method = RequestMethod.DELETE)
    public boolean removeWhitelistIps(@RequestBody EnrollWhitelistIp enrollWhitelistIps) { return appService.removeWhiteIps(enrollWhitelistIps); }

}
