package com.longcoding.undefined.controllers.internal;

import com.longcoding.undefined.models.ehcache.ApiInfo;
import com.longcoding.undefined.models.enumeration.SyncType;
import com.longcoding.undefined.models.internal.EnrollApi;
import com.longcoding.undefined.services.internal.ApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by longcoding on 19. 1. 17..
 */

@Slf4j
@RestController
@RequestMapping(value = "/internal/apis")
public class ApiController {

    @Autowired
    ApiService apiService;

    @RequestMapping(method = RequestMethod.POST)
    public ApiInfo createApp(@RequestBody EnrollApi enrollApi) { return apiService.createOrModifyApi(SyncType.CREATE,  enrollApi); }

    @RequestMapping(value = "{apiId}", method = RequestMethod.DELETE)
    public boolean deleteApp(@PathVariable String apiId) { return apiService.deleteApi(apiId); }

    @RequestMapping(value = "{apiId}", method = RequestMethod.PUT)
    public ApiInfo updateApi(@PathVariable String apiId, @RequestBody EnrollApi enrollApi) { return apiService.createOrModifyApi(SyncType.UPDATE, enrollApi); }

    @RequestMapping(value = "{apiId}", method = RequestMethod.GET)
    public ApiInfo getApi(@PathVariable String apiId) { return apiService.selectApi(apiId); }

}
