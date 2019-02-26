package com.longcoding.moon.helpers;

import com.longcoding.moon.configs.JettyClientConfig;
import org.eclipse.jetty.client.HttpClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Class for managing connection pool of jetty client.
 * The api-gateway uses the jetty client to send api requests to the outbound service.
 *
 * @author longcoding
 */
@Component
@EnableConfigurationProperties(JettyClientConfig.class)
public class JettyClientFactory implements InitializingBean {

    @Autowired
    MessageManager messageManager;

    @Autowired
    JettyClientConfig jettyClientConfig;

    private static HttpClient httpClient;


    /**
     * Get one jetty client from the jetty client pool.
     *
     * @return Jetty client to send api call to outbound service.
     */
    public HttpClient getJettyClient() {
        return httpClient;
    }

    //Configuration information for jetty exists in application.yml.
    @Override
    public void afterPropertiesSet() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(jettyClientConfig.getThreadCount());
        httpClient = new HttpClient();
        try {
            httpClient.setMaxConnectionsPerDestination(jettyClientConfig.getMaxConnection());
            httpClient.setConnectTimeout(jettyClientConfig.getTimeout());
            httpClient.setExecutor(executor);
            httpClient.start();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
