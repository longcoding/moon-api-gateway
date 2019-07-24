package com.longcoding.moon.services;

import com.longcoding.moon.MoonApplication;
import com.longcoding.moon.helpers.APIExposeSpecification;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayOutputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * Created by longcoding on 16. 4. 14..
 * Updated by longcoding on 18. 12. 27..
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {MoonApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("local")
public class ProxyServiceTest {

    protected final Logger logger = LogManager.getLogger(ProxyServiceTest.class);

//    @Bean
//    @Primary
//    public JedisFactory jedisFactory() {
//        return new JedisFactory();
//    }

/*
    Default Test Setting

    App name : TestApp
    App key : 1000-1000-1000-1000
    App Id : 100
    App daily ratelimit quota : 10000
    App minutely ratelimit quota : 1500


    Service Id : 300
    Service name : stackoverflow
    Service daily capacity quota : 10000
    Service minutely capacity quota : 2000


    Test #1 API api Id : 200
                api name : TestAPI
                inbound path : /stackoverflow/2.2/question/:path
                outbound path : http://api.stackexchange.com/2.2/questions
                mandatory queryparam : site
                option queryparam : version
                mandatory header : none
                option header : page, votes
*/


    @Autowired
    APIExposeSpecification apiExposeSpecification;


    @Before
    public final void setUp() throws Exception {
    }

    @Test
    public void testCallHttpGet_API_TRANSFER_MODE() throws Exception {

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
        httpGet.setHeader("apiKey", "1000-1000-1000-1000");

        HttpResponse response = httpClient.execute(httpGet);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        response.getEntity().writeTo(outputStream);
        logger.info("Response: {}", outputStream);

        assertThat(response.containsHeader(HttpHeaders.CONTENT_TYPE), is(true));
        assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
    }

    @Test
    public void testCallHttpGet_SKIP_API_TRANSFORM() throws Exception {

        HttpClient httpClient = HttpClientBuilder.create().build();

        //inbound path : /service3/2.2/questions?order=desc&sort=activity&site=stackoverflow
        //outbound path : http://api.stackexchange.com/2.2/questions?order=desc&sort=activity&site=stackoverflow
        URIBuilder uriBuilder = new URIBuilder();
        uriBuilder.setScheme("http")
                .setHost("localhost")
                .setPort(8080)
                .setPath("/service3/2.2/questions")
                //Insert mandatory query param
                .setParameter("site", "stackoverflow")
                //Insert option query param
                .setParameter("sort", "activity")
                .setParameter("order", "desc");


        HttpGet httpGet = new HttpGet(uriBuilder.build());

        //this action is not needed. just for understanding.
        httpGet.setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON.toString());
        httpGet.setHeader("apiKey", "1000-1000-1000-1000");

        HttpResponse response = httpClient.execute(httpGet);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        response.getEntity().writeTo(outputStream);
        logger.info("Response: {}", outputStream);

        assertThat(response.containsHeader(HttpHeaders.CONTENT_TYPE), is(true));
        assertThat(response.getStatusLine().getStatusCode(), equalTo(200));

    }
}
