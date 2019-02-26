package com.longcoding.moon.models.apis;

import com.longcoding.moon.helpers.APIExposeSpecLoader;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * It is information about service specification. It is an object based on application-apis.yml.
 *
 * It will be loaded into the cache in future APIExposeSpecLoader.
 * If the type of application-apis.yml changes, you can change the object.
 * It is an object that has List<APIExpose>.
 *
 * @see APIExpose
 * @see APIExposeSpecLoader
 *
 * @author longcoding
 */

@Data
public class ServiceExpose implements Serializable, Cloneable {

    private static final long serialVersionUID = 3156858586955443057L;

    String serviceId;
    String serviceName;
    String servicePath;
    String outboundServiceHost;
    int serviceMinutelyCapacity;
    int serviceDailyCapacity;
    boolean onlyPassRequestWithoutTransform = false;
    List<APIExpose> apis;

}
