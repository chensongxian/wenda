package com.csx.async.handler;

import com.alibaba.fastjson.JSONObject;
import com.csx.async.EventHandler;
import com.csx.async.EventModel;
import com.csx.async.EventType;
import com.csx.model.*;
import com.csx.service.*;
import com.csx.util.JedisSingleAdapter;
import com.csx.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by csx on 2016/7/30.
 */
@Component
public class FeedHandler implements EventHandler {
    @Autowired
    FollowService followService;

    @Autowired
    UserService userService;

    @Autowired
    FeedService feedService;

    @Autowired
    JedisSingleAdapter jedisAdapter;

    @Autowired
    QuestionService questionService;

    @Autowired
    CommentService commentService;


    private String buildFeedData(EventModel model) {
        Map<String, String> map = new HashMap<String ,String>();
        // 触发用户是通用的
        User actor = userService.getUser(model.getActorId());
        if (actor == null) {
            return null;
        }
        map.put("userId", String.valueOf(actor.getId()));
        map.put("userHead", actor.getHeadUrl());
        map.put("userName", actor.getName());

        if (model.getType() == EventType.COMMENT ) {
            Comment comment=commentService.getCommentById(model.getEntityOwnerId());
            Question question = questionService.getById(model.getEntityId());
            if (question == null) {
                return null;
            }
            map.put("questionId", String.valueOf(question.getId()));
            map.put("questionTitle", question.getTitle());
            map.put("comment",comment.getContent());
            return JSONObject.toJSONString(map);
        }

        if(model.getType() == EventType.FOLLOW  && model.getEntityType() == EntityType.ENTITY_QUESTION){
            Question question = questionService.getById(model.getEntityId());
            if (question == null) {
                return null;
            }
            map.put("questionId", String.valueOf(question.getId()));
            map.put("questionTitle", question.getTitle());
            map.put("entityType",String.valueOf(EntityType.ENTITY_QUESTION));

            //发送关注提醒
            String newFeed=RedisKeyUtil.getBizNewfeed(model.getEntityOwnerId());
            jedisAdapter.incr(newFeed);

            return JSONObject.toJSONString(map);
        }

        if(model.getType() == EventType.FOLLOW && model.getEntityType()==EntityType.ENTITY_USER){
            User user=userService.getUser(model.getEntityOwnerId());
            map.put("entityType",String.valueOf(EntityType.ENTITY_USER));
            map.put("entityUserName",user.getName());
            String newFeed=RedisKeyUtil.getBizNewfeed(model.getEntityOwnerId());
            jedisAdapter.incr(newFeed);
            return JSONObject.toJSONString(map);
        }

        if(model.getType()==EventType.ADD_QUESTION){
            Question question = questionService.getById(model.getEntityId());
            if (question == null) {
                return null;
            }
            map.put("questionId", String.valueOf(question.getId()));
            map.put("questionTitle", question.getTitle());
            return JSONObject.toJSONString(map);
        }
        if(model.getType()==EventType.LIKE){
            Comment comment=commentService.getCommentById(model.getEntityId());
            int questionId=Integer.parseInt(model.getExt("questionId"));
            Question question=questionService.getById(questionId);

            map.put("questionId",String.valueOf(questionId));
            map.put("questionTitle",question.getTitle());
            map.put("comment",comment.getContent());

            String newFeed=RedisKeyUtil.getBizNewfeed(model.getEntityOwnerId());
            jedisAdapter.incr(newFeed);
            return JSONObject.toJSONString(map);

        }
        return null;
    }

    @Override
    public void doHandle(EventModel model) {
        // 为了测试，把model的userId随机一下
//        Random r = new Random();
//        model.setActorId(1+r.nextInt(10));

        // 构造一个新鲜事
        Feed feed = new Feed();
        feed.setCreatedDate(new Date());
        feed.setType(model.getType().getValue());
        feed.setTypeId(model.getEntityOwnerId());
        feed.setUserId(model.getActorId());
        feed.setData(buildFeedData(model));
        if (feed.getData() == null) {
            // 不支持的feed
            return;
        }
        feedService.addFeed(feed);

        // 获得所有粉丝
        List<Integer> followers = followService.getFollowers(EntityType.ENTITY_USER, model.getActorId(), Integer.MAX_VALUE);
        // 系统队列
        followers.add(0);
        // 给所有粉丝推事件
        for (int follower : followers) {
//            System.out.println("关注:"+follower);
            String timelineKey = RedisKeyUtil.getTimelineKey(follower);
            jedisAdapter.lpush(timelineKey, String.valueOf(feed.getId()));
            // 限制最长长度，如果timelineKey的长度过大，就删除后面的新鲜事

            //新消息提醒
            String newFeed=RedisKeyUtil.getBizNewfeed(follower);
            jedisAdapter.incr(newFeed);
        }
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(new EventType[]{EventType.COMMENT,EventType.ADD_QUESTION,EventType.FOLLOW,EventType.LIKE});
    }
}
