package com.longcoding.moon.controllers.internal;

import com.longcoding.moon.models.ehcache.ApiInfo;
import com.longcoding.moon.models.enumeration.SyncType;
import com.longcoding.moon.models.internal.EnrollApi;
import com.longcoding.moon.services.internal.ApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Internal API for managing API specifications
 * Any node in the cluster can receive these requests.
 * When it receives the request, it sends the event to other nodes.
 *
 * The model object for the client request differs from the model object for the service layer.
 *
 * @author longcoding
 */

@Slf4j
@RestController
@RequestMapping(value = "/internal/apis")
public class ApiController {

    @Autowired
    ApiService apiService;

    /**
     * Register a new API specification. This job does not load into the cache.
     * When a new API specification request is received, it serves to issue events to other nodes, including itself.
     *
     * @param enrollApi API specification model for client requests.
     * @return API specification model for internal processing.
     */
    @RequestMapping(method = RequestMethod.POST)
    public ApiInfo createApi(@RequestBody EnrollApi enrollApi) { return apiService.createOrModifyApi(SyncType.CREATE,  enrollApi); }

    /**
     * Delete the existing api specification. This action does not directly apply to the cache.
     * When a new api specification update request is received, it is responsible for issuing an event to other nodes including itself.
     *
     * @param apiId Application Id.
     * @return Returns success or failure.
     */
    @RequestMapping(value = "{apiId}", method = RequestMethod.DELETE)
    public boolean deleteApi(@PathVariable String apiId) { return apiService.deleteApi(apiId); }

    /**
     * Update existing api specifications. This action does not directly apply to the cache.
     * When a new api specification update request is received, it is responsible for issuing an event to other nodes including itself.
     *
     * @param apiId Application Id.
     * @param enrollApi API specification model for client requests.
     * @return API specification model for internal processing.
     */
    @RequestMapping(value = "{apiId}", method = RequestMethod.PUT)
    public ApiInfo updateApi(@PathVariable String apiId, @RequestBody EnrollApi enrollApi) { return apiService.createOrModifyApi(SyncType.UPDATE, enrollApi); }

    /**
     * Query the corresponding api specification.
     *
     * @param apiId Application Id.
     * @return API specification model for internal processing.
     */
    @RequestMapping(value = "{apiId}", method = RequestMethod.GET)
    public ApiInfo getApi(@PathVariable String apiId) { return apiService.selectApi(apiId); }

}
