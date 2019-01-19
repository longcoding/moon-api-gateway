# undefined-API-gateway

![feature](https://user-images.githubusercontent.com/38850896/50330803-f13e9600-053f-11e9-9b2b-cd2f1d5de76d.png)


## Introduction
Asynchronous API Gateway with Spring boot 2.1, Servlet 4, jetty 9 client <br />
The Gateway is a network gateway created to provide a single access point for real-time web based protocol elevation that supports load balancing, clustering, and lots of validations. It is designed to make the best performance to deliver open API.
* Lightweight API Gateway
* High Performance/Scalability

## New Feature(1-15-2019)
* Management API
* Supported Server Cluster

## Features
Undefined API Gateway offers powerful, yet lightweight feature.

* **Request Validation** - 요청에 다양한 검증기능을 사용할 수 있습니다. 또한 새로운 feature 를 추가하거나 제거하기 쉽습니다.
    - Header, Query, Path Param
* **Rate Limiting** - API Users 별 강력한 Rate limiting 을 제공합니다. Redis 베이스로 Cluster 서버들은 Ratelimiting 정보를 공유합니다. on a per-key basis
    - App Daily Rate Limiting
    - App Minutely  Rate Limiting
* **Service Capacity** - 연동된 서비스의 Capacity 를 관리하여 안정적인 운영이 가능하도록 합니다.
    - Service Daily Capacity
    - Service Minutely Capacity
* **Service Contract(agreement)** - (Optional) API, APP Users 들은 계약관계 혹은 약관에 동의한 API 만 호출 가능합니다.
* **Request Transform** - (Optional) URI 변경 뿐만 아니라 Header, Query, Path Param 의 변경을 지원합니다. 이는 User 의 요청을 undefiend api Gateway 와 연동된 서비스의 요청으로 적절하게 변경해줍니다.
* **IP Whitelisting** - Block access to non-trusted IP addresses for more secure interactions on a per-key basis
* **Management API** - API Gateway 를 관리하는 강력한 Rest API 를 제공합니다.
    - API 추가/삭제/변경
    - APP 추가/삭제/변경 
    - IP Whitelist 추가/삭제
    - Key Expiry/Regenerate
* **Supported Server Cluster** - API Gateway Cluster 를 구성할 수 있습니다. Management API 를 사용하면 모든 서버에서 변경사항이 적용됩니다. Ratelimiting, Service Capacity 정보도 모두 공유됩니다. 

## Dependency
* Spring Boot 2.1
* Servlet 4
* Ehcache 3
* Jetty 9 client
* Jedis 3.0

## Configuration
There are required settings to run undefined-api-gateway

A. First
 <br />
    - Enroll init APP configuration in application-apps.yaml <br />
    - (enroll init APP process is optional)

    init-apps:
      init-enable: true
      apps:
        -
          app-id: 0
          app-name: TestApp
          app-key: 1000-1000-1000-1000
          app-minutely-ratelimit: 2000
          app-daily-ratelimit: 10000
          app-service-contract: ['01', '02', '03']
          app-ip-acl: ['192.168.0.1', '127.0.0.1']
        -
          app-id: 1
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
      init-enable: true
      services:
        -
          service-id: '01'
          service-name: stackoverflow
          service-minutely-capacity: 10000
          service-daily-capacity: 240000
          service-path: /stackoverflow
          outbound-service-host: api.stackexchange.com
          apis:
            -
              api-id: '0101'
              api-name: getInfo
              protocol: http, https
              method: get
              inbound-url: /2.2/question/:first
              outbound-url: /2.2/questions
              header: page, votes
              header-required: ""
              url-param: version, site
              url-param-required: site
            -
              api-id: '0202'
              api-name: getQuestions
              protocol: https
              method: put
              inbound-url: /2.2/question/:first
              outbound-url: /2.2/questions
              header: page, votes
              header-required: ""
              url-param: version, site
              url-param-required: site
        -
          service-id: '02'
          service-name: stackoverflow2
          service-minutely-capacity: 5000
          service-daily-capacity: 100000
          service-path: /another
          outbound-service-host: api.stackexchange.com
          apis:
            -
              api-id: '0201'
              api-name: transformTest
              protocol: http, https
              method: get
              inbound-url: /2.2/haha/question/:site
              outbound-url: /:page/:site
              header: page, votes
              header-required: ""
              url-param: version, site
              url-param-required: site
              transform:
                page: [header, param_path]
                site: [param_path, header]
        -
          service-id: '03'
          service-name: service3
          service-minutely-capacity: 5000
          service-daily-capacity: 100000
          service-path: /service3
          outbound-service-host: api.stackexchange.com
          only-pass-request-without-transform: true

                          


Undefined-api-gateway supports the following protocol and method.

* Protocol
    - http, https
* Method
    - Get, Post, Put, Delete
    
## API Gateway Cluster
Undefined API Gateway supports clusters. Each node synchronizes API, APP, IP Whitelist and App Key (= API Key) information in near real time.
Cluster nodes also work together to calculate the correct API Ratelimiting, Service Capacity.


## Management REST API
The Management API helps manage a single gateway or cluster group.

**APP Management**
  - APP Register - [POST] /internal/apps
  - APP UnRegister - [DELETE] /internal/apps/{appId}
  - APP Update - [Future Feature]
  - Appkey(=API Key) Expiry - [DELETE] /internal/apps/{appId}/appkey
  - Appkey(=API Key) Regenerate - [PUT] /internal/apps/{appId}
  - Add NEW IP Whiltelist - [POST] /internal/apps/{appId}/whitelist
  - Remove IP Whiltelist - [DELETE] /internal/apps/{appId}/whitelist
**API Management**
  - New API Register - [POST] /internal/apis
  - API UnRegister - [DELETE] /internal/apis/{apiId}
  - API Update - [PUT] /internal/apis/{apiId}

**Service Group API Will be updated**

    

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
