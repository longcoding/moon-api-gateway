package com.longcoding.undefined.helpers.jetty;

import com.longcoding.undefined.helpers.MessageManager;
import org.eclipse.jetty.client.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by longcoding on 16. 4. 6..
 * Updated by longcoding on 18. 12. 26..
 */
@Component
@EnableConfigurationProperties(JettyClientConfig.class)
public class JettyClientFactory {

    @Autowired
    MessageManager messageManager;

    @Autowired
    JettyClientConfig jettyClientConfig;

    private static HttpClient httpClient;

    @PostConstruct
    private void initializeJettyClient() {
        ExecutorService executor = Executors.newFixedThreadPool(jettyClientConfig.threadCount);
        httpClient = new HttpClient();
        try {
            httpClient.setMaxConnectionsPerDestination(jettyClientConfig.maxConnection);
            httpClient.setConnectTimeout(jettyClientConfig.timeout);
            httpClient.setExecutor(executor);
            httpClient.start();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public HttpClient getJettyClient() {
        return httpClient;
    }

}
