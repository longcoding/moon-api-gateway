package com.longcoding.undefined.helpers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.UUID;

@Slf4j
@Component
public class ClusterSyncUtil {

    @Autowired
    JedisFactory jedisFactory;

    @Value("${undefined.service.cluster.enable}")
    boolean clusterEnable;

    public void setexInfoToHealthyNode(String redisKey, int seconds, String info) {

        if (clusterEnable) {
            try (Jedis jedisClient = jedisFactory.getInstance()) {
                jedisClient.keys(Constant.REDIS_KEY_CLUSTER_SERVER_HEALTH + "*").forEach(nodeKey -> {
                    String[] redisKeyInArray = nodeKey.split(":");
                    String nodeName = redisKeyInArray[redisKeyInArray.length - 1];
                    String uuidByNode = String.join("-", nodeName, UUID.randomUUID().toString());

                    jedisClient.setex(String.join("-", redisKey, uuidByNode), seconds, info);
                });
            }
        }
    }
}
