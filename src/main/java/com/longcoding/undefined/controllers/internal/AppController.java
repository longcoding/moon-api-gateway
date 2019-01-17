package com.longcoding.undefined.controllers.internal;

import com.longcoding.undefined.models.ehcache.AppInfo;
import com.longcoding.undefined.models.internal.EnrollApp;
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
    AppInfo createApp(@RequestBody EnrollApp enrollApp) { return appService.createApp(enrollApp); }

    @RequestMapping(value = "{appId}", method = RequestMethod.DELETE)
    boolean deleteApp(@PathVariable String appId) {
        return appService.deleteApp(appId);
    }

    @RequestMapping(value = "{appId}", method = RequestMethod.GET)
    AppInfo getAppInfo(@PathVariable String appId) {
        return appService.getAppInfo(appId);
    }

    @RequestMapping(value = "{appId}/appKey", method = RequestMethod.DELETE)
    boolean expireAppKey(@PathVariable String appId) {
        return appService.expireAppKey(appId);
    }

    @RequestMapping(value = "{appId}/appKey", method = RequestMethod.PUT)
    AppInfo refreshAppKey(@PathVariable String appId) {
        return appService.refreshAppKey(appId);
    }

}
