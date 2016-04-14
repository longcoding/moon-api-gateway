package com.longcoding.undefined.helpers;

import java.nio.charset.StandardCharsets;

/**
 * Created by longcoding on 16. 4. 5..
 */
public class Const {

    public static final String REQUEST_INFO_DATA = "requestInfo";
    public static final String RESPONSE_INFO_DATA = "responseInfo";
    public static final String IS_VALID_SERVICE_LIMIT = "isValidServiceLimit";

    public static final String ERROR_MEESAGE = "message";
    public static final String DETAIL_ERROR_MEESAGE = "detail_message";

    public static final String OBJECT_GET_REDIS_VALIDATION = "redisValidation";

    public static final String SERVER_DEFAULT_ENCODING_TYPE = StandardCharsets.UTF_8.name();

    public static final String API_MATCH_HTTP_GET_MAP = "httpGET";
    public static final String API_MATCH_HTTP_POST_MAP = "httpPOST";
    public static final String API_MATCH_HTTP_PUT_MAP = "httpPUT";
    public static final String API_MATCH_HTTP_DELETE_MAP = "httpDELETE";

    public static final String API_MATCH_HTTPS_GET_MAP = "httpsGET";
    public static final String API_MATCH_HTTPS_POST_MAP = "httpsPOST";
    public static final String API_MATCH_HTTPS_PUT_MAP = "httpsPUT";
    public static final String API_MATCH_HTTPS_DELETE_MAP = "httpsDELETE";

    public static final String REQUEST_ACCEPT = "accept";
    public static final String REQUEST_USER_AGENT = "user-agent";

    public static final String REQUEST_RESPONSE_CONTENT_TYPE = "Content-Type";

    public static final String HEADER_CUSTOMIZE_REMOTE_IP = "x-remote-ip";
    public static final String HEADER_CUSTOMIZE_REMOTE_AGENT = "x-user-agent";

    public static final String REDIS_SERVICE_CAPACITY_MINUTELY = "service_capacity_minutely";
    public static final String REDIS_SERVICE_CAPACITY_DAILY = "service_capacity_daily";

    public static final String REDIS_APP_RATELIMIT_MINUTELY = "app_ratelimit_minutely";
    public static final String REDIS_APP_RATELIMIT_DAILY = "app_ratelimit_daily";

}