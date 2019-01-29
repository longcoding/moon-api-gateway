package com.longcoding.undefined.helpers;

import org.apache.logging.log4j.util.Strings;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by longcoding on 16. 4. 9..
 * Updated by longcoding on 19. 1. 29..
 */
public class HttpHelper {

    private static final String CONSTANT_UTIL_EXTRACT_URL = "/";
    private static final String DELIMITER_PROTOCOL = "://";
    private static final String DELIMITER_QUERY_STRING = "=";
    private static final String DELIMITER_QUERY_STRING_END = "&";

    public static String[] extractURL(String URL) {
        return URL.split(CONSTANT_UTIL_EXTRACT_URL);
    }

    public static String createURI(String protocol, String url) {
        return protocol + DELIMITER_PROTOCOL + url;
    }

    public static String getHostName() {
        String hostname;

        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "UnknownHost";
        }

        return hostname;
    }

    public static String getHostIp() {
        String hostname;

        try {
            hostname = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "UnknownHost";
        }

        return hostname;
    }

    public static String getRoutingRegex(String routingUrl) {
        StringBuilder routingPathInRegex = new StringBuilder();
        StringTokenizer st = new StringTokenizer(routingUrl, "/");
        while (st.hasMoreTokens()) {
            String element = st.nextToken();
            if (element.startsWith(":")) element = "[a-zA-Z0-9-_]+";

            routingPathInRegex.append("/").append(element);
        }

        return routingPathInRegex.toString();
    }

}
