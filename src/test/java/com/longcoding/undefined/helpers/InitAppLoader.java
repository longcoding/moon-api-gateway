package com.longcoding.undefined.helpers;

import com.longcoding.undefined.configs.InitAppConfig;
import com.longcoding.undefined.exceptions.ExceptionType;
import com.longcoding.undefined.exceptions.GeneralException;
import com.longcoding.undefined.models.cluster.AppSync;
import com.longcoding.undefined.models.ehcache.AppInfo;
import com.longcoding.undefined.models.enumeration.SyncType;
import com.longcoding.undefined.services.sync.SyncService;
import lombok.extern.slf4j.Slf4j;
import org.ehcache.Cache;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.stream.Collectors;

/**
 * Created by longcoding on 19. 1. 4..
 */


@Slf4j
@Component
@EnableConfigurationProperties(InitAppConfig.class)
public class InitAppLoader {

}
