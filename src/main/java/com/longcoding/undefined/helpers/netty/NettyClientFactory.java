package com.longcoding.undefined.helpers.netty;

import com.longcoding.undefined.helpers.Const;
import org.eclipse.jetty.client.HttpClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by longcoding on 16. 4. 6..
 */
@Component
public class NettyClientFactory {

    private static final ExecutorService executor;
    private static final HttpClient httpClient = new HttpClient();

    static {
        executor = Executors.newFixedThreadPool(100);
        try {
            httpClient.setMaxConnectionsPerDestination(Const.NETTY_MAX_CONNECTION);
            httpClient.setExecutor(executor);
            httpClient.setConnectTimeout(Const.NETTY_HTTP_TIMEOUT);
            httpClient.start();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private NettyClientFactory() {}

    public HttpClient getNettyClient() {
        return httpClient;
    }

}