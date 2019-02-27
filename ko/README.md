# Moon-API-gateway

[![Build Status](https://travis-ci.org/longcoding/undefined-api-gateway.svg?branch=master&maxAge=2592000)](https://travis-ci.org/longcoding/undefined-api-gateway.svg?branch=master)
[![codecov](https://codecov.io/gh/longcoding/undefined-api-gateway/branch/master/graph/badge.svg?maxAge=2592000)](https://codecov.io/gh/longcoding/undefined-api-gateway/branch/master/graph/badge.svg)
[![Release](https://img.shields.io/github/release/longcoding/undefined-api-gateway.svg?maxAge=2592000)](https://img.shields.io/github/release/longcoding/undefined-api-gateway.svg)
[![HitCount](http://hits.dwyl.io/longcoding@gmail.com/longcoding/undefined-api-gateway.svg)](http://hits.dwyl.io/longcoding@gmail.com/longcoding/undefined-api-gateway.svg)
[![LastCommit](https://img.shields.io/github/last-commit/longcoding/undefined-api-gateway.svg)](https://img.shields.io/github/last-commit/longcoding/undefined-api-gateway.svg)
[![TotalCommit](https://img.shields.io/github/commit-activity/y/longcoding/undefined-api-gateway.svg)](https://img.shields.io/github/commit-activity/y/longcoding/undefined-api-gateway.svg)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?maxAge=2592000)]()

![feature](https://user-images.githubusercontent.com/3271895/51427833-6d132780-1c3f-11e9-8f73-7112a7f0da0c.png)


## Introduction
비동기 API Gateway(이하 게이트웨이) with Spring boot 2.1, Servlet 4, jetty 9 client <br />
게이트웨이는 로드밸런싱, 클러스터링 및 여러 유효성 검증을 지원하는 실시간 높은 수준의 웹 기반 프로토콜 단일 엑세스 접근을 보장하는 네트워크 게이트웨이입니다. 게이트웨이는 open API를 제공하기 위해서 최고의 성능으로 동작하도록 설계되었습니다.

* 경량 API Gateway
* 고성능
* 확장성

## New Feature(January-2019)
* API 관리 기능
* 서버 클러스터 지원

## Features
Moon-API-Gateway는 강력하지만, 가볍고 빠른 기능을 제공합니다.

* **Request Validation** - Request 요청에 대한 여러가지 유효성 검증 기능을 사용할 수 있습니다. 또한 새로운 기능에 쉽게 적용하고 제거할 수 있습니다.
    - Header, Query, Path Param
* **Rate Limiting** - API 사용자들에 대한 강력한 사용빈도 제한이 가능합니다. Redis-based 클러스터 서버들은 키 기반으로 사용빈도 제한 정보를 공유할 수 있습니다.
    - App 일단위 사용빈도 제한
    - App 분단위 사용빈도 제한
* **Service Capacity** - 서비스의 안정적인 동작을 위해 API 게이트웨이에 연결된 서비스 수용량(Capacity)을 관리합니다.
    - Service 일단위 수용량
    - Service 분단위 수용량
* **Service Contract(agreement)** - (Optional) API, App 사용자들은 계약 관계나 계약 기간에 부합하는 API만 호출할 수 있습니다.
* **Request Transform** - (Optional) Header, Query, Path Param, URI 변경을 지원합니다. 이는 Moon-API-Gateway에 관련된 서비스의 사용자 request를 적절하게 변경합니다.
* **IP Whitelisting** - 보다 안전한 상호작용을 위해 키 단위로 신뢰할 수 없는 IP 주소의 접근을 차단합니다.
* **Management API** - API 게이트웨이 관리를 위한 강력한 Rest API를 제공합니다.
    - API Add/Delete/Change
    - APP Add/Delete/Change
    - IP Whitelist Add/Delete
    - Key Expiry/Regenerate
* **Supported Server Cluster** - API 게이트웨이 클러스터를 관리할 수 있습니다. 관리(Management) API를 이용해서 변경사항을 모든 서버에 적용할 수 있습니다. 즉, 사용빈도, 서비스 수용량 정보를 모든 서버가 공유할 수 있습니다.

## Dependency
* Spring Boot 2.1
* Servlet 4
* Ehcache 3
* Jetty 9 client
* Jedis 3.0

## Configuration
Moon-API-Gateway 실행을 위한 필요한 설정있습니다.
이를통해 관리(management) API를 사용해서 초기화를 할 필요가 없습니다.  

### Step 1
```
- Please set the global application first in application.yaml

moon:
  service:       
    ip-acl-enable: false
    cluster:
      enable: false
      sync-interval: 300000       
    proxy-timeout: 20000

jedis-client:      
  host: '127.0.0.1'
  port: 6379
  timeout: 1000
  database: 0
```
- ip-acl-enable: IP 화이트리스트 기능을 설정합니다. 이는 APP 기반으로 동작합니다.
- cluster/enable: 서버 클러스터 설정을 사용한다면 데몬 스레드가 Service, App, API 정보들을 가져옵니다.
- cluster/sync-interval: 클러스터의 동기화 시간을 설정할 수 있습니다.
- proxy-timeout: 서비스 로테이션 타임아웃 시간을 설정할 수 있습니다.  
- **jedis-client**: Moon-API-Gateway에서 Redis 설정은 반드시 필요합니다.
- jedis-client/host: Redis 호스트 정보를 설정합니다.
- jedis-client/port: Redis 포트 정보를 설정합니다.

### B. Step 2
```
- Please set the initial application registration in application-apps.yaml
- (These settings are optional)

init-apps:
  init-enable: true
  apps:
    -
      app-id: 0
      app-name: TestApp
      api-key: 1000-1000-1000-1000
      app-minutely-ratelimit: 2000
      app-daily-ratelimit: 10000
      app-service-contract: [1, 2, 3]
      app-ip-acl: ['192.168.0.1', '127.0.0.1']
    -
      app-id: 1
      app-name: BestApp
      api-key: e3938427-1e27-3a37-a854-0ac5a40d84a8
      app-minutely-ratelimit: 1000
      app-daily-ratelimit: 50000
      app-service-contract: [1, 2]
      app-ip-acl: ['127.0.0.1']
```

- init-enable: init-apps 설정 사용 여부를 설정합니다.
- app-service-contract: 사용권한이 있는 app API 서비스 목록을 설정합니다.
- app-ip-acl: API 키를 사용할 수 있는 IP 화이트리스트 목록을 설정합니다.
- app minutely/daily ratelimit: app에서 호출 가능한 API 호출빈도를 설정합니다.

### Step 3
```
- Set up service and API specification configurations in application-apis.yml
- The API Gateway obtains Service and API information through the APIExposeSpecLoader.

api-spec:
  init-enable: true
  services:
    -
      service-id: 1
      service-name: stackoverflow
      service-minutely-capacity: 10000
      service-daily-capacity: 240000
      service-path: /stackoverflow
      outbound-service-host: api.stackexchange.com
      apis:
        -
          api-id: 101
          api-name: getInfo
          protocol: http, https
          method: get
          inbound-url: /2.2/question/:first
          outbound-url: /2.2/questions
          header: page, votes
          header-required: ""
          query-param: version, site
          query-param-required: site
        -
          api-id: 202
          api-name: getQuestions
          protocol: https
          method: put
          inbound-url: /2.2/question/:first
          outbound-url: /2.2/questions
          header: page, votes
          header-required: ""
          query-param: version, site
          query-param-required: site
    -
      service-id: 2
      service-name: stackoverflow2
      service-minutely-capacity: 5000
      service-daily-capacity: 100000
      service-path: /another
      outbound-service-host: api.stackexchange.com
      apis:
        -
          api-id: 201
          api-name: transformTest
          protocol: http, https
          method: get
          inbound-url: /2.2/haha/question/:site
          outbound-url: /:page/:site
          header: page, votes
          header-required: ""
          query-param: version, site
          query-param-required: site
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
```
- init-enable: api-spec 사용 여부를 설정합니다.
- service-path: URL 경로의 첫번째 파라미터를 설정합니다. API는 해당 경로로 등록된 서비스로 라우팅됩니다.
- service minutely/daily capacity: 서비스의 분/일 단위 수용량을 설정합니다.
- outbound-service-host: 서비스 API의 응답이 라우팅되는 외부 도메인을 설정합니다.
- apis/inbound-url: 외부로 노출할 API URL 경로를 명세합니다. `:?`에 설정합니다.
- apis/outbound-url: The actual url path of the service connected to the api-gateway.
- apis/header: This is the header that can be received when requesting API.
- apis/header-required: API 요청의 필수 헤더를 설정합니다.
- apis/query-param: URL 쿼리 파라미터를 설정합니다.
- apis/query-param-required: 필수 URL 쿼리 파라미터를 설정합니다.
- transform: 수신한 요청의 파라미터를 라우팅과 동시에 다른 파라미터로 변환되도록 설정합니다.
    - 사용 가능한 옵션: **header**, **param_path**, **param_query**, **body_json**
    - 사용 방법: [source, destination] 형태로 설정합니다. 예: [header, param_path]
    - body_json 타입을 사용하는 경우, `content-type`은 `application/json`을 사용해야합니다.
- only-pass-request-without-transform: 게이트웨이에 접속하는 모든 API가 어떤 분석이나 변환이 없이 서비스로 라우팅되도록 설정합니다.


Moon-api-gateway 는 아래의 프로토콜과 메소드를 지원합니다.

* 프로토콜
    - HTTP, HTTPS
* 메소드
    - GET, POST, PUT, DELETE

## API Gateway Cluster
Moon API Gateway supports clusters. Each node synchronizes API, APP, IP Whitelist and App Key (= API Key) information in near real time.
Cluster nodes also work together to calculate the correct API Ratelimiting, Service Capacity.

- **Service, API, APP, IP Whitelist Interval Sync**

![feature](https://user-images.githubusercontent.com/3271895/51427837-7b614380-1c3f-11e9-82f3-5a668ba63f00.png)


## Management REST API
The Management API helps manage a single gateway or cluster group.

**APP Management**
  - **APP Register** - [POST] /internal/apps
  - **APP UnRegister** - [DELETE] /internal/apps/{appId}
  - **APP Update** - [Future Feature]
  - **API Key Expiry** - [DELETE] /internal/apps/{appId}/apikey
  - **API Key Regenerate** - [PUT] /internal/apps/{appId}
  - **Add NEW IP Whitelist** - [POST] /internal/apps/{appId}/whitelist
  - **Remove IP Whitelist** - [DELETE] /internal/apps/{appId}/whitelist

**API Management**
  - **New API Register** - [POST] /internal/apis
  - **API UnRegister** - [DELETE] /internal/apis/{apiId}
  - **API Update** - [PUT] /internal/apis/{apiId}

**Service Group API Will be updated**



## Usage/Test

##### API - stackoverflow API.

Service And API Expose Specification for stackoverflow


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
              query-param: version, site
              query-param-required: site


- 1) The api service path we want to call is '/stackoverflow'
- 2) The inbound url path following the service path is '/2.2/question/:first'
- 3) ': first' is the path parameter and we declare it 'test'.
- 4) The calling protocol supports http and https, and we will call http.
- 5) Set the header and url query parameters.
- 6) When you call the API, the gateway will route the call to api.stackexchange.com set to outbound-service-host.
- 7) When calling the API, the domain is api.stackexchange.com and the destination url path is '/2.2/questions' set to outbound-url.

##### 1) Use Test Case - Run moon-api-gateway by gradle

    ./gradlew test

##### 2) Use rest-client like Postman.
To set method and scheme.

    GET, http

Input URL.

    http://localhost:8080/stackoverflow/2.2/question/test

Input URL parameter. ( site is mandatory query parameter )

    site = stackoverflow

OR you can input URL like below.

    http://localhost:8080/stackoverflow/2.2/question/test?site=stackoverflow

and then input header fields. ( apikey is mandatory header.(or Query Parameter) )

    apikey, 1000-1000-1000-1000
    page, 5
    votes, 1

Execute request and check response code and content.

##### 3) Use cUrl.

    curl -X GET -H "Content-type: application/json" -H "apikey: 1000-1000-1000-1000" -H "page: 5" -H "votes: 1" http://localhost:8080/stackoverflow/2.2/question/test?site=stackoverflow

## Future update
* Authentication for Private API
* Docker-Compose
    - Easy To Run

## Contact
For any inquiries, you can reach me at longcoding@gmail.com

## License
moon-api-gateway is released under the MIT license. See LICENSE for details.
