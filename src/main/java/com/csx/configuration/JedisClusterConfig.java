package com.csx.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by csx on 2016/10/4.
 */
@Configuration
public class JedisClusterConfig {


    /**
     * 注意：
     * 这里返回的JedisCluster是单例的，并且可以直接注入到其他类中去使用
     * @return
     */
    @Bean
    public JedisCluster getJedisCluster() {
        //创建集群
        Set<HostAndPort> nodes = new HashSet<>();
        nodes.add(new HostAndPort("119.29.223.150", 7001));
        nodes.add(new HostAndPort("119.29.223.150", 7002));
        nodes.add(new HostAndPort("119.29.223.150", 7003));
        nodes.add(new HostAndPort("119.29.223.150", 7004));
        nodes.add(new HostAndPort("119.29.223.150", 7005));
        nodes.add(new HostAndPort("119.29.223.150", 7006));

        JedisCluster cluster = new JedisCluster(nodes);

        return cluster;
    }
}
