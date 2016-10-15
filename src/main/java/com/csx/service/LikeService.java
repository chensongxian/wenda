package com.csx.service;

import com.csx.util.JedisSingleAdapter;
import com.csx.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by csx on 2016/7/30.
 */
@Service
public class LikeService {
    @Autowired
    JedisSingleAdapter jedisAdapter;


    public long getLikeCount(int entityType, int entityId) {
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        return jedisAdapter.scard(likeKey);
    }


    public long getDisLikeCount(int entityType, int entityId) {
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        return jedisAdapter.scard(disLikeKey);
    }

    public int getLikeStatus(int userId, int entityType, int entityId) {
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        if (jedisAdapter.sismember(likeKey, String.valueOf(userId))) {
            return 1;
        }
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        return jedisAdapter.sismember(disLikeKey, String.valueOf(userId)) ? -1 : 0;
    }

    public long like(int userId, int entityType, int entityId,int questionId) {
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        jedisAdapter.sadd(likeKey, String.valueOf(userId));

        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        jedisAdapter.srem(disLikeKey, String.valueOf(userId));
        //赞评论的同时，加1用以统计赞总数
        String questionLikeKey=RedisKeyUtil.getBizQuestionlike(questionId);
        jedisAdapter.incr(questionLikeKey);
        return jedisAdapter.scard(likeKey);
    }

    public long disLike(int userId, int entityType, int entityId,int questionId) {
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        jedisAdapter.sadd(disLikeKey, String.valueOf(userId));

        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        jedisAdapter.srem(likeKey, String.valueOf(userId));


        //取消赞评论的同时，减1用以统计赞总数
        String questionLikeKey=RedisKeyUtil.getBizQuestionlike(questionId);
        jedisAdapter.decr(questionLikeKey);

        return jedisAdapter.scard(likeKey);
    }


    public int getQuestionLikeCount(int questionId){
        String questionLikeKey=RedisKeyUtil.getBizQuestionlike(questionId);
        String countStr=jedisAdapter.get(questionLikeKey);
        int count;
        if(countStr==null){
            count=0;
        }else {
            count = Integer.parseInt(countStr);
        }
        return count;
    }
}
