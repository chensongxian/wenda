package com.csx.util;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

import java.util.Map;
import java.util.TreeSet;

/**
 * Created by csx on 2016/10/4.
 */
@Service
public class RedisOperator implements IRedisOperator{
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(JedisClusterAdapter.class);

    @Autowired
    private JedisCluster jedisCluster;

    @Override
    public TreeSet<String> keys(String pattern){
        logger.debug("Start getting keys...");
        TreeSet<String> keys = new TreeSet<>();
        Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();
        for(String k : clusterNodes.keySet()){
//            System.out.println("k:"+k);
            logger.debug("Getting keys from: {}", k);
            //防止获取的地址是内网地址
            String[] ip=k.split(":");
            if(ip[0].equals("10.104.247.250")){
                continue;
            }
            JedisPool jp = clusterNodes.get(k);
            Jedis connection = jp.getResource();
            try {
                keys.addAll(connection.keys(pattern));
            } catch(Exception e){
                logger.error("Getting keys error: {}", e);
            } finally{
                logger.debug("Connection closed.");
                connection.close();//用完一定要close这个链接！！！
            }
        }
        logger.debug("Keys gotten!");
        return keys;
    }
}

