package com.longcoding.undefined.services;

import com.longcoding.undefined.configs.UndefinedInitializer;
import com.longcoding.undefined.helpers.Const;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
/**
 * Created by longcoding on 16. 4. 14..
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = UndefinedInitializer.class, inheritInitializers = true)
public class ProxyServiceTest {

    @Before
    public void setUp() throws Exception {
        //input ehcache data
//        //EhcacheFactory ehcacheFactory = applicationContext.getBean("ehcacheFactory", EhcacheFactory.class);
//        Cache<String, String> appIdDistinction = ehcacheFactory.getAppDistinctionCache();
//        appIdDistinction.put("9af18d4a-3a2e-3653-8548-b611580ba585", "100");
//        ehcacheFactory.getApiIdCache("httpGET").put("localhost:8080/undefined/[a-zA-Z0-9]+/test", "2000");
//        AppInfoCache appInfoCache = new AppInfoCache("100", "9af18d4a-3a2e-3653-8548-b611580ba585", "app", "1000000", "1000000");
//        ehcacheFactory.getAppInfoCache().put(appInfoCache.getAppId(), appInfoCache);
//
//        ConcurrentHashMap<String, Boolean> queryParams = new ConcurrentHashMap<>();
//        queryParams.put("version", true);
//        ConcurrentHashMap<String, Boolean> headers = new ConcurrentHashMap<>();
//        headers.put("Content-Type".toLowerCase(), true);
//        headers.put("appKey".toLowerCase(), true);
//        String inboundURL = "localhost:8080/undefined/:first/test";
//        String outboundURL = "172.19.107.67:9011/11st/common/categories";
//        //String outboundURL = "10.213.50.1:8080/undefined/test/:first";
//        ApiInfoCache apiInfoCache = new ApiInfoCache("2000", "TestAPI", "3000", headers, queryParams, inboundURL, outboundURL, "GET", "GET", "http", true);
//        ehcacheFactory.getApiInfoCache().put(apiInfoCache.getApiId(), apiInfoCache);
//
//        ServiceInfoCache serviceInfoCache = new ServiceInfoCache("3000", "undefined", "10000", "10000");
//        ehcacheFactory.getServiceInfoCache().put(serviceInfoCache.getServiceId(), serviceInfoCache);

        //input redis data
        JedisPool jedisPool = new JedisPool("127.0.0.1", 6379);
        Jedis jedis = jedisPool.getResource();
        Pipeline pipeline = jedis.pipelined();
        pipeline.hset(Const.REDIS_SERVICE_CAPACITY_DAILY, "3000", "1000000");
        pipeline.hset(Const.REDIS_SERVICE_CAPACITY_MINUTELY, "3000", "1000000");
        pipeline.hset(Const.REDIS_APP_RATELIMIT_DAILY, "100", "10000000");
        pipeline.hset(Const.REDIS_APP_RATELIMIT_MINUTELY, "100", "1000000");
        pipeline.sync();
        jedis.close();
    }

    @Test
    public void testCallHttpGet() throws Exception {

        HttpClient httpClient = new DefaultHttpClient();

        HttpGet httpGet = new HttpGet("http://localhost:8080/undefined/dd/test?version=1");
        httpGet.setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON.toString());
        httpGet.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());
        httpGet.setHeader("appKey", "9af18d4a-3a2e-3653-8548-b611580ba585");

        HttpResponse response = httpClient.execute(httpGet);
        HttpEntity httpEntity = response.getEntity();

        Assert.assertEquals(true, response.containsHeader(HttpHeaders.CONTENT_TYPE));
        Assert.assertEquals(200, response.getStatusLine().getStatusCode());

    }
}