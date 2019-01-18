package com.longcoding.undefined.helpers;

import com.longcoding.undefined.configs.JettyClientConfig;
import org.eclipse.jetty.client.HttpClient;
import org.springframework.beans.factory.InitializingBean;
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
public class JettyClientFactory implements InitializingBean {

    @Autowired
    MessageManager messageManager;

    @Autowired
    JettyClientConfig jettyClientConfig;

    private static HttpClient httpClient;
    
    public HttpClient getJettyClient() {
        return httpClient;
    }

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
