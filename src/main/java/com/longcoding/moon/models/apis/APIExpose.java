package com.longcoding.moon.models.apis;

import com.longcoding.moon.helpers.APIExposeSpecLoader;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Information about the API specification. It is an object based on application-apis.yml.
 *
 * It will be loaded into the cache in future APIExposeSpecLoader.
 * If the type of application-apis.yml changes, you can change the object.
 *
 * @see APIExposeSpecLoader
 *
 * @author longcoding
 */

@Data
public class APIExpose implements Serializable, Cloneable {

    private static final long serialVersionUID = -3037135041336335171L;

    String apiId;
    String apiName;
    List<String> protocol;
    String method;
    String inboundUrl;
    String outboundUrl;
    List<String> header;
    List<String> headerRequired;
    List<String> queryParam;
    List<String> queryParamRequired;
    Map<String, String[]> transform;

}
