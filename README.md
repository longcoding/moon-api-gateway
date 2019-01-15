# undefined-API-gateway

![feature](https://user-images.githubusercontent.com/38850896/50330803-f13e9600-053f-11e9-9b2b-cd2f1d5de76d.png)


## Introduction
Asynchronous API Gateway with Spring boot 2.1, Servlet 4, jetty 9 client <br />
The Gateway is a network gateway created to provide a single access point for real-time web based protocol elevation that supports load balancing, clustering, and lots of validations. It is designed to make the best performance to deliver open API.

## New Feature(2019-1-15)
* Management API
* Supported Server Cluster

## Features
* Lightweight API Gateway
* High Performance/Scalability
* Request Validation by Spring Interceptor(Easy to insert new validation)
    - Header, Query, Path.
    - Appkey
    - Service Daily, Minutely Capacity.
    - App Daily, Minutely Ratelimit.
    - Service Contract(agreement)
    - Request Transform
* Use Asynchronous Processing in Servlet 4
* Management API
* Supported Server Cluster

## Dependency
* Spring Boot 2.1
* Servlet 4
* Ehcache 3
* Jetty 9 client
* Jedis 3.0

## Configuration
There are 5 required settings to run undefined-gateway in ehcache. `when cluster function is developed by future update. It will be easy to insert and sync the below data.`

A. First
 <br />
    - Enroll init APP configuration in application-apps.yaml <br />
    - (enroll init APP process is optional)

        init-apps:
        init-enable: true
        apps:
            -
            app-id: 1
            app-name: TestApp
            app-key: 1000-1000-1000-1000
            app-minutely-ratelimit: 2000
            app-daily-ratelimit: 10000
            app-service-contract: ['01', '02']
            app-ip-acl: ['192.168.0.1', '127.0.0.1']
            -
            app-id: 2
            app-name: BestApp
            app-key: e3938427-1e27-3a37-a854-0ac5a40d84a8
            app-minutely-ratelimit: 1000
            app-daily-ratelimit: 50000
            app-service-contract: ['01', '02']
            app-ip-acl: ['127.0.0.1']

B. Second
 <br />
    - Set up Service and API expose configuration in application-apis.yml <br />
    - The Gateway brings Service and API information to APIExposeSpecification By APIExposeSpecLoader.

    api-spec:
      services:
        -
          service-id: 01
          service-name: stackoverflow
          service-minutely-capacity: 10000
          service-daily-capacity: 240000
          service-path: stackoverflow
          apis:
            -
              api-id: 0101
              api-name: getInfo
              protocol: http, https
              method: get
              inbound-url: /2.2/question/:first
              outbound-url: api.stackexchange.com/2.2/questions
              header: page, votes
              header-required: ""
              url-param: version, site
              url-param-required: site
            -
              api-id: '0201'
              api-name: getList
              protocol: http, https
              method: get
              inbound-url: /2.2/question/:first
              outbound-url: api.stackexchange.com/2.2/:page
              header: page, votes
              header-required: ""
              url-param: version, site
              url-param-required: site
              transform:
                page: [header, param_query]
                site: [param_path, header]
            -
              ...
                          


Undefined-gateway supports the following protocol and method.

* Protocol
    - http, https
* Method
    - Get, Post, Put, Delete


## Usage/Test

##### Test Case - stackoverflow API.

Run undefined-api-gateway

    ./gradlew test

Use rest-client like Postman. To set method and scheme.

    GET, http 

Input URL.

    http://localhost:8080/stackoverflow/2.2/question/test

Input URL parameter. ( site is mandatory query parameter )

    site = stackoverflow

OR you can input URL like below.

    http://localhost:8080/stackoverflow/2.2/question/test?site=stackoverflow

and then input header fields. ( appkey is mandatory header.(or Query Parameter) )

    appkey, 1000-1000-1000-1000
    page, 5
    votes, 1

Execute request and check response code and content.

## Future update
* Authentication for private API
* Docker-compose
    - Easy To Run

## Contact
For any inquiries, you can reach me at longcoding@gmail.com 

## License
undefined-gateway is released under the MIT license. See LICENSE for details.
