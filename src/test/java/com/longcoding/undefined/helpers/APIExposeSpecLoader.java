package com.longcoding.undefined.helpers;

import com.google.common.collect.Lists;
import com.longcoding.undefined.configs.APIExposeSpecConfig;
import com.longcoding.undefined.exceptions.ExceptionType;
import com.longcoding.undefined.exceptions.GeneralException;
import com.longcoding.undefined.models.apis.TransformData;
import com.longcoding.undefined.models.cluster.ApiSync;
import com.longcoding.undefined.models.ehcache.ApiInfo;
import com.longcoding.undefined.models.ehcache.ServiceInfo;
import com.longcoding.undefined.models.ehcache.ServiceRoutingInfo;
import com.longcoding.undefined.models.enumeration.*;
import com.longcoding.undefined.services.sync.SyncService;
import lombok.extern.slf4j.Slf4j;
import org.ehcache.impl.internal.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by longcoding on 19. 1. 1..
 */

@Slf4j
@Component
@EnableConfigurationProperties(APIExposeSpecConfig.class)
public class APIExposeSpecLoader {


}
