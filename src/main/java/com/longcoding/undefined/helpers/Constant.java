package com.longcoding.undefined.helpers;

import java.nio.charset.StandardCharsets;

/**
 * Created by longcoding on 16. 4. 5..
 */
public class Constant {

    public static final String REQUEST_INFO_DATA = "requestInfo";
    public static final String RESPONSE_INFO_DATA = "responseInfo";

    public static final String OBJECT_GET_REDIS_VALIDATION = "redisValidation";

    public static final String SERVER_DEFAULT_ENCODING_TYPE = StandardCharsets.UTF_8.name();

    public static final String ACL_IP_CHECKER = "ACL_IP_CHECKER";

    public static final String API_MATCH_HTTP_GET_MAP = "HTTPGET";
    public static final String API_MATCH_HTTP_POST_MAP = "HTTPPOST";
    public static final String API_MATCH_HTTP_PUT_MAP = "HTTPPUT";
    public static final String API_MATCH_HTTP_DELETE_MAP = "HTTPDELETE";

    public static final String API_MATCH_HTTPS_GET_MAP = "HTTPSGET";
    public static final String API_MATCH_HTTPS_POST_MAP = "HTTPSPOST";
    public static final String API_MATCH_HTTPS_PUT_MAP = "HTTPSPUT";
    public static final String API_MATCH_HTTPS_DELETE_MAP = "HTTPSDELETE";

    public static final String REQUEST_ACCEPT = "accept";
    public static final String REQUEST_USER_AGENT = "user-agent";

    public static final String[] HEADER_NEED_TO_REMOVE_LIST = {"apikey", "host", "user-agent"};

    public static final String HEADER_CUSTOMIZE_REMOTE_IP = "x-remote-ip";
    public static final String HEADER_CUSTOMIZE_REMOTE_AGENT = "x-user-agent";
    public static final String HEADER_CUSTOMIZE_APP_ID = "x-app-id";

    public static final String REDIS_SERVICE_CAPACITY_MINUTELY = "service_capacity:minutely";
    public static final String REDIS_SERVICE_CAPACITY_MINUTELY_TTL = "service_capacity:minutely-ttl";
    public static final String REDIS_SERVICE_CAPACITY_DAILY = "service_capacity:daily";
    public static final String REDIS_SERVICE_CAPACITY_DAILY_TTL = "service_capacity:daily-ttl";

    public static final String REDIS_APP_RATELIMIT_MINUTELY = "app_ratelimit:minutely";
    public static final String REDIS_APP_RATELIMIT_MINUTELY_TTL = "app_ratelimit:minutely-ttl";
    public static final String REDIS_APP_RATELIMIT_DAILY = "app_ratelimit:daily";
    public static final String REDIS_APP_RATELIMIT_DAILY_TTL = "app_ratelimit:daily-ttl";

    public static final int SECOND_OF_DAY = 60*60*24;
    public static final int SECOND_OF_MINUTE = 60;
    public static final int SECOND_OF_HOUR = 60*60;

    public static final String REDIS_KEY_INTERNAL_APP_INFO = "internal:apps";
    public static final String REDIS_KEY_INTERNAL_API_INFO = "internal:apis";
    public static final String REDIS_KEY_INTERNAL_SERVICE_INFO = "internal:services";
    public static final String REDIS_KEY_CLUSTER_SERVER_HEALTH = "internal:sync-server-health";

    public static final String REDIS_KEY_APP_UPDATE = "internal:sync:update:apps";
    public static final String REDIS_KEY_API_UPDATE = "internal:sync:update:apis";
    public static final String REDIS_KEY_APP_WHITELIST_UPDATE = "internal:sync:update:whitelist";

    public static final String CONTENT_TYPE_EXTRACT_DELIMITER = ";";



}
