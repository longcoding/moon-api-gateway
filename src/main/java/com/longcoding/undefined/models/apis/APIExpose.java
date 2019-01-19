package com.longcoding.undefined.models.apis;

import lombok.Data;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by longcoding on 19. 1. 1..
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
