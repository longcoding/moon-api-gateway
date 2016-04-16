# undefined-gateway

##Introduction
Asynchronous API Gateway with netty client, spring framework 4, servlet 3.
The Gateway is a network gateway created to provide a single access point for real-time web based protocol elevation that supports load balancing, clustering, and lots of validation. It is designed to make the best performance to deliver open API.

##Features
* Lightweight API Gateway
* High Performance/Scalability
* Request Validation by Spring Interceptor(Easy to insert new validation)
    - Header, Query, Path.
    - Service Daily, Minutely Capacity.
    - App Daily, Minutely Ratelimit.
* Use Asynchronous Http Client with Netty
* Use Asynchronous Processing in Servlet 3

##Dependency
* Spring Framework 4.2.5
* Servlet 3
* Ehcache 3
* Jedis 2.8

##Configuration
There are 5 required setting to run undefined gateway in ehcache. `when cluster function is developed by future update. It will be easy to insert and sync the below data`

A. first - 
    * To set appDistinction Cache Object in EhcacheFactory Class. 
    * This Cache convert 'appKey' to 'appId'. 

    Cache<String, String> appDistinction = getAppDistinctionCache();
    //(appKey, appId)
    //from appKey to appId
    appDistinction.put("1000-1000-1000-1000", "100");

B. second -
    * To set AppInfoCache Cache Object in EhcacheFactory Class.
    * The Gateway takes App information from AppInfoCache.

    //appId : 100
    //appKey : 1000-1000-1000-1000
    //appName : TestApp
    //appDailyRatelimit : 10000
    //appMinutelyRatelimit : 1500
    AppInfoCache appInfoCache = new AppInfoCache("100", "1000-1000-1000-1000", "TestApp", "10000", "1500");
    getAppInfoCache().put(appInfoCache.getAppId(), appInfoCache); 

C. third -  
    * To set ApiInfoCache Cache Object in EhcacheFactory Class.
    * The Gateway takes Api information from ApiInfoCache. boolean variable means mandatory or not.

    //Query Parameter Setting.
    ConcurrentHashMap<String, Boolean> queryParams = new ConcurrentHashMap<>();
    queryParams.put("version", false);
    queryParams.put("site", true);
    //Header Setting.
    ConcurrentHashMap<String, Boolean> headers = new ConcurrentHashMap<>();
    headers.put("page", false);
    headers.put("votes", false);
    String inboundURL = "localhost:8080/stackoverflow/2.2/question/:first";
    String outboundURL = "api.stackexchange.com/2.2/questions";
    ApiInfoCache apiInfoCache = new ApiInfoCache("200", "TestAPI", "300", headers, queryParams, inboundURL, outboundURL, "GET", "GET", "http", true);
    getApiInfoCache().put(apiInfoCache.getApiId(), apiInfoCache);

D. fourth -
    * To set ServiceInfoCache Cache Object in EhcacheFactory Class.
    * The Gateway takes Service information from ServiceInfoCache.

    //serviceId : 300
    //serviceName : stackoverflow
    //serviceDailyCapacity : 10000
    //serviceMinutelyCapacity : 2000
    ServiceInfoCache serviceInfoCache = new ServiceInfoCache("300", "stackoverflow", "10000", "2000");
    getServiceInfoCache().put(serviceInfoCache.getServiceId(), serviceInfoCache);

E. fifth -
    * To set appDistinction Cache Object in EhcacheFactory Class.
    * The Gateway recognize request api by appDistinction.
    
    //in case of httpGet
    apiMatchHttpGet.put("localhost:8080/stackoverflow/2.2/question/[a-zA-Z0-9]+", "200");

    //in case of httpPost
    apiMatchHttpPost.put(key, value);

    //in case of httpsGet
    apiMatchHttpsGet.put(key, value);

Undefined-gateway supports the following protocol and method.

* apiMatchHttpGet (http - get)
* apiMatchHttpPost (http - post)
* apiMatchHttpPut (http - put)
* apiMatchHttpDelete (http - delete)
* apiMatchHttpsGet (https - get)
* apiMatchHttpsPost (https - post)
* apiMatchHttpsPut (https - put)
* apiMatchHttpsDelete (https - delete)

##Usage/Test

#####Test Case - stackoverflow API.

To run undefined-gateway

    maven clean -DskipTests jetty:run

* you can also deploy on tomcat.

To use restclient like Postman. To set method and scheme.

    GET, http 

To input URL.

    http://localhost:8080/stackoverflow/2.2/question/test

To input URL parameter. ( site is mandatory query parameter )

    site = stackoverflow

or You can input URL like that.

    http://localhost:8080/stackoverflow/2.2/question/test?site=stackoverflow

andthen input header fields. ( appkey is mandatory header.(or queryparam) )

    appkey, 1000-1000-1000-1000
    page, 5
    votes, 1

To execute request and check response code and content.

##Future update
* log function
* Authentication for private API
* Cluster
    - by Ehcache RMI replication
    - by RabbitMq

##Contact
For any inquiries, you can reach me at longcoding@gmail.com

##License
undefined-gateway is released under the MIT license. See LICENSE for details.
