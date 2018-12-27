package com.longcoding.undefined.services;

import com.longcoding.undefined.UndefinedApplication;
import com.longcoding.undefined.helpers.APISpecification;
import com.longcoding.undefined.helpers.Const;
import org.apache.catalina.startup.Tomcat;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

/**
 * Created by longcoding on 16. 4. 14..
 * Updated by longcoding on 18. 12. 27..
 */
@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = UndefinedInitializer.class, inheritInitializers = true)
@SpringBootTest(classes = UndefinedApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ProxyServiceTest {

    //Default Test Setting
    //
    //App name : TestApp
    //App key : 1000-1000-1000-1000
    //App Id : 100
    //App daily ratelimit quota : 10000
    //App minutely ratelimit quota : 1500
    //
    //
    //Service Id : 300
    //Service name : stackoverflow
    //Service daily capacity quota : 10000
    //Service minutely capacity quota : 2000
    //
    //
    //Test #1 API api Id : 200
    //            api name : TestAPI
    //            inbound path : /stackoverflow/2.2/question/:path
    //            outbound path : http://api.stackexchange.com/2.2/questions
    //            mandatory queryparam : site
    //            option queryparam : version
    //            mandatory header : none
    //            option header : page, votes
    //

    @Autowired
    APISpecification apiSpecification;

    @Before
    public void setUp() throws Exception {
        //input redis data
        //this action is not needed. just for understanding.
        JedisPool jedisPool = new JedisPool("127.0.0.1", 6379);
        Jedis jedis = jedisPool.getResource();
        Pipeline pipeline = jedis.pipelined();
        pipeline.hset(Const.REDIS_SERVICE_CAPACITY_DAILY, "300", "10000");
        pipeline.hset(Const.REDIS_SERVICE_CAPACITY_MINUTELY, "300", "2000");
        pipeline.hset(Const.REDIS_APP_RATELIMIT_DAILY, "100", "10000");
        pipeline.hset(Const.REDIS_APP_RATELIMIT_MINUTELY, "100", "1500");
        pipeline.sync();
        jedis.close();

        apiSpecification.insertEhcacheTestCase();
    }

    @Test
    public void testCallHttpGet() throws Exception {

        HttpClient httpClient = HttpClientBuilder.create().build();

        //inbound path : /stackoverflow/2.2/question/:path
        //outbound path : http://api.stackexchange.com/2.2/questions
        URIBuilder uriBuilder = new URIBuilder();
        uriBuilder.setScheme("http")
                .setHost("localhost")
                .setPort(8080)
                .setPath("/stackoverflow/2.2/question/test")
                //Insert mandatory query param
                .setParameter("site", "stackoverflow")
                //Insert option query param
                .setParameter("page", "2")
                .setParameter("votes", "1");


        HttpGet httpGet = new HttpGet(uriBuilder.build());

        //this action is not needed. just for understanding.
        httpGet.setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON.toString());
        httpGet.setHeader("appKey", "1000-1000-1000-1000");

        HttpResponse response = httpClient.execute(httpGet);
        HttpEntity httpEntity = response.getEntity();

        System.out.println(response.getEntity().getContent());
        System.out.println(response.getStatusLine());

        Assert.assertTrue(response.containsHeader(HttpHeaders.CONTENT_TYPE));
        Assert.assertEquals(200, response.getStatusLine().getStatusCode());

    }
}
