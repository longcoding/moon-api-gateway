package com.longcoding.moon.helpers.cluster;

import com.longcoding.moon.helpers.Constant;
import com.longcoding.moon.helpers.JedisFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.UUID;

/**
 * A utility class for cluster synchronization.
 * When a new information change request is received from internal api, it is sent to other nodes.
 * Sends an event to the nodes whose health check is normal.
 *
 * @author longcoding
 */

@Slf4j
@Component
public class ClusterSyncUtil {

    @Autowired
    JedisFactory jedisFactory;

    @Value("${moon.service.cluster.enable}")
    boolean clusterEnable;

    /**
     * Sends an event to healthy nodes.
     *
     * @param redisKey The key to be used for the event. Node name, event name, and so on.
     * @param seconds redis TTL. Determine how long to keep the event data.
     * @param info Contains the object to be used for the event.
     */
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
