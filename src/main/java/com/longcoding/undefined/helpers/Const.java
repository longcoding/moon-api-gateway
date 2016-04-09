package com.longcoding.undefined.helpers;

import java.nio.charset.StandardCharsets;

/**
 * Created by longcoding on 16. 4. 5..
 */
public class Const {

    public static final String REQUEST_INFO_DATA = "requestInfo";
    public static final String RESPONSE_INFO_DATA = "responseInfo";

    public static final String OBJECT_GET_REDIS_VALIDATION = "redisValidation";

    public static final String SERVER_DEFAULT_ENCODING_TYPE = StandardCharsets.UTF_8.name();

    public static final Integer API_MATCH_HTTP_GET_MAP = 0;
    public static final Integer API_MATCH_HTTP_POST_MAP = 1;
    public static final Integer API_MATCH_HTTP_PUT_MAP = 2;
    public static final Integer API_MATCH_HTTP_DELETE_MAP = 3;

    public static final Integer API_MATCH_HTTPS_GET_MAP = 4;
    public static final Integer API_MATCH_HTTPS_POST_MAP = 5;
    public static final Integer API_MATCH_HTTPS_PUT_MAP = 6;
    public static final Integer API_MATCH_HTTPS_DELETE_MAP = 7;

    public static final String REQUEST_ACCEPT = "accept";
    public static final String REQUEST_USER_AGENT = "user-agent";

    public static final String REQUEST_RESPONSE_CONTENT_TYPE = "Content-Type";

    public static final String HEADER_CUSTOMIZE_REMOTE_IP = "x-remote-ip";
    public static final String HEADER_CUSTOMIZE_REMOTE_AGENT = "x-user-agent";

}