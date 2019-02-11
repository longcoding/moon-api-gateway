package com.longcoding.undefined.helpers;

import com.longcoding.undefined.models.CommonResponseEntity;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

/**
 * Utility class.
 *
 * @author longcoding
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

    /**
     * Get the host name of the node.
     *
     * @return hostName
     */
    public static String getHostName() {
        String hostname;

        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "UnknownHost";
        }

        return hostname;
    }

    /**
     * Get the host ip of the node.
     *
     * @return hostIp
     */
    public static String getHostIp() {
        String hostname;

        try {
            hostname = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "UnknownHost";
        }

        return hostname;
    }

    /**
     * Obtain the routing regex to match the api path.
     * Split by '/' and make variable into regex form.
     * The method reassembles these again.
     *
     * @return patterned routing path In String.
     */
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

    public static ResponseEntity newResponseEntityWithId(HttpStatus httpStatus, CommonResponseEntity response) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(Constant.REQUEST_ID, MDC.get(Constant.REQUEST_ID));
        return new ResponseEntity<>(response, httpHeaders, httpStatus);
    }


}
