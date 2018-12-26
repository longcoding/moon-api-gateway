package com.longcoding.undefined.helpers.jetty;

import com.longcoding.undefined.helpers.MessageManager;
import org.eclipse.jetty.client.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by longcoding on 16. 4. 6..
 * Updated by longcoding on 18. 12. 26..
 */
@Component
public class JettyClientFactory {

    private final MessageManager messageManager;
    private static HttpClient httpClient;

    @Autowired
    public JettyClientFactory(MessageManager messageManager) {
        this.messageManager = messageManager;
    }

    @PostConstruct
    private void initializeNettyClient() {

        int THREAD_POOL_COUNT = messageManager.getIntProperty("undefined.netty.thread.count");
        int NETTY_MAX_CONNECTION = messageManager.getIntProperty("undefined.netty.max.connection");
        long NETTY_HTTP_TIMEOUT = messageManager.getLongProperty("undefined.netty.http.timeout");

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_COUNT);
        httpClient = new HttpClient();
        try {
            httpClient.setMaxConnectionsPerDestination(NETTY_MAX_CONNECTION);
            httpClient.setConnectTimeout(NETTY_HTTP_TIMEOUT);
            httpClient.setExecutor(executor);
            httpClient.start();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public HttpClient getNettyClient() {
        return httpClient;
    }

}
