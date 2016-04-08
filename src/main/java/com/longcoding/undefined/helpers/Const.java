package com.longcoding.undefined.helpers;

import java.nio.charset.StandardCharsets;

/**
 * Created by longcoding on 16. 4. 5..
 */
public class Const {

    public static final long   NETTY_HTTP_TIMEOUT = 30000l;
    public static final int    NETTY_MAX_CONNECTION = 400;

    public static final String REQUEST_INFO_DATA = "requestInfo";

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

}