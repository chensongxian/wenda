package com.csx.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.*;

import java.util.Iterator;
import java.util.List;
import java.util.Set;


/**
 * Created by csx on 2016/7/30.
 */
@Service
public class JedisClusterAdapter {
    private static final Logger logger = LoggerFactory.getLogger(JedisClusterAdapter.class);
    private JedisPool pool;

    @Autowired
    private JedisCluster cluster;

    @Autowired
    private RedisOperator redisOperator;


    //删除所有已pre_str为前缀的所有key
    public boolean delAll(String pre_str) {
        Set<String> set = redisOperator.keys(pre_str + "*");
        Iterator<String> it = set.iterator();
        while (it.hasNext()) {
            String keyStr = it.next();
            cluster.del(keyStr);
        }
        return true;
    }

    //获取以pre_str为前缀的所有key
    public Set<String> getAll(String pre_str) {
        Set<String> set = redisOperator.keys(pre_str + "*");
        return set;
    }

    public String set(String key, String value) {
        return cluster.set(key, value);
    }

    public long incr(String key) {
        return cluster.incr(key);
    }

    //减
    public long decr(String key) {
        return cluster.decr(key);
    }

    public String get(String key) {
        return cluster.get(key);
    }

    public long sadd(String key, String value) {
        return cluster.sadd(key, value);
    }

    public long srem(String key, String value) {
        return cluster.srem(key, value);
    }

    public long scard(String key) {
        return cluster.scard(key);
    }

    public boolean sismember(String key, String value) {
        return cluster.sismember(key, value);
    }

    public List<String> brpop(int timeout, String key) {
        return cluster.brpop(timeout, key);
    }

    public long lpush(String key, String value) {
        return cluster.lpush(key, value);
    }

    public List<String> lrange(String key, int start, int end) {
        return cluster.lrange(key, start, end);
    }

    public long zadd(String key, double score, String value) {
        return cluster.zadd(key, score, value);
    }


    public long zrem(String key, String value) {
        return cluster.zrem(key, value);
    }


    public Set<String> zrange(String key, int start, int end) {
        return cluster.zrange(key, start, end);
    }

    public Set<String> zrevrange(String key, int start, int end) {
        return cluster.zrevrange(key, start, end);
    }

    public long zcard(String key) {
        return cluster.zcard(key);
    }

    public Double zscore(String key, String member) {
        return cluster.zscore(key, member);
    }

    public String setEx(String key, int time, String member) {
        return cluster.setex(key, time, member);
    }

    public String getEx(String key) {
        return cluster.get(key);
    }
}
