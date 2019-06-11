package com.longcoding.moon.controllers.internal;

import com.longcoding.moon.models.ehcache.ServiceInfo;
import com.longcoding.moon.models.enumeration.SyncType;
import com.longcoding.moon.models.internal.EnrollService;
import com.longcoding.moon.services.internal.ServiceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by longcoding on 19. 6. 11..
 */

@Slf4j
@RestController
@RequestMapping(value = "/internal/services")
public class ServiceController {

    @Autowired
    ServiceService serviceService;

    @RequestMapping(method = RequestMethod.POST)
    public ServiceInfo createService(@RequestBody EnrollService enrollService) { return serviceService.createOrUpdateService(SyncType.CREATE, enrollService); }

    @RequestMapping(value = "{serviceId}", method = RequestMethod.GET)
    public ServiceInfo getServiceInfo(@PathVariable int serviceId) { return serviceService.getServiceInfo(serviceId); }

    @RequestMapping(value = "{serviceId}", method = RequestMethod.PUT)
    public ServiceInfo updateService(@RequestBody EnrollService enrollService) { return serviceService.createOrUpdateService(SyncType.UPDATE, enrollService); }

}
