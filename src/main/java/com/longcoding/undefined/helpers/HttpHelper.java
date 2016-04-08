package com.longcoding.undefined.helpers;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by longcoding on 16. 4. 9..
 */
public class HttpHelper {

    private static final String CONST_UTIL_EXTRACT_URL = "/";
    private static final String DELIMITER_PROTOCOL = "://";
    private static final String DELIMITER_QUERY_STRING = "=";
    private static final String DELIMITER_QUERY_STRING_END = "&";

    public static String[] extractURL(String URL) {
        return URL.split(CONST_UTIL_EXTRACT_URL);
    }

    public static String createURI(String protocol, String url, Map<String, String> map) {

        StringBuffer requestURLBuffer =
                new StringBuffer()
                        .append(protocol)
                        .append(DELIMITER_PROTOCOL)
                        .append(url);

        //TODO : wish upgrade logic.
        int index = 0;
        int lastIndex = map.size();
        StringBuffer queryStringBuffer = new StringBuffer();
        for (String queryKey : map.keySet()) {
            if (index++ > 0 && index <= lastIndex) queryStringBuffer.append(DELIMITER_QUERY_STRING_END);
            queryStringBuffer.append(queryKey).append(DELIMITER_QUERY_STRING).append(map.get(queryKey));
        }

        String queryString = null;
        try {
            queryString = URLEncoder.encode(queryStringBuffer.toString(), Const.SERVER_DEFAULT_ENCODING_TYPE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return requestURLBuffer.toString() + "?" + queryString;
    }
}
