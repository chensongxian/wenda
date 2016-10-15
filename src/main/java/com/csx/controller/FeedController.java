package com.csx.controller;

import com.alibaba.fastjson.JSONObject;
import com.csx.async.EventType;
import com.csx.model.EntityType;
import com.csx.model.Feed;
import com.csx.model.HostHolder;
import com.csx.service.FeedService;
import com.csx.service.FollowService;
import com.csx.util.JedisSingleAdapter;
import com.csx.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by csx on 2016/7/15.
 */
@Controller
public class FeedController {
    private static final Logger logger = LoggerFactory.getLogger(FeedController.class);

    @Autowired
    FeedService feedService;

    @Autowired
    FollowService followService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    JedisSingleAdapter jedisAdapter;

    @RequestMapping(path = {"/pushfeeds"}, method = {RequestMethod.GET, RequestMethod.POST})
    private String getPushFeeds(Model model) {
        int localUserId = hostHolder.getUser() != null ? hostHolder.getUser().getId() : 0;
        List<String> feedIds = jedisAdapter.lrange(RedisKeyUtil.getTimelineKey(localUserId), 0, 10);
        List<Feed> feeds = new ArrayList<Feed>();
        for (String feedId : feedIds) {
            Feed feed = feedService.getById(Integer.parseInt(feedId));
            if (feed != null) {
                feeds.add(feed);
            }
        }
        model.addAttribute("feeds", feeds);
        return "feeds";
    }

    @RequestMapping(path = {"/loadMoreFeeds/{offset}/{limit}"}, method = {RequestMethod.GET})
    @ResponseBody
    private String loadMoreFeeds(@PathVariable("offset") int offset,@PathVariable("limit") int limit) {
        int localUserId = hostHolder.getUser() != null ? hostHolder.getUser().getId() : 0;
        List<Integer> followees = new ArrayList<>();
        if (localUserId != 0) {
            // 关注的人
            followees = followService.getFollowees(localUserId, EntityType.ENTITY_USER, Integer.MAX_VALUE);
        }
        List<Integer> types=new ArrayList<>();
        types.add(EventType.ADD_QUESTION.getValue());
        types.add(EventType.COMMENT.getValue());
        types.add(EventType.FOLLOW.getValue());
        types.add(EventType.LIKE.getValue());

        List<Feed> feeds = feedService.getUserFeeds(Integer.MAX_VALUE, followees,types,offset,limit);
        JSONObject vos=new JSONObject();
        if(feeds.size()<10){
            vos.put("hasNext",false);
        }else {
            vos.put("hasNext",true);
        }
        vos.put("feeds", feeds);
        return vos.toString();
    }


    @RequestMapping(path = {"/pullfeeds"}, method = {RequestMethod.GET, RequestMethod.POST})
    private String getPullFeeds(Model model) {
        int localUserId = hostHolder.getUser() != null ? hostHolder.getUser().getId() : 0;
        List<Integer> followees = new ArrayList<>();
        if (localUserId != 0) {
            // 关注的人
            followees = followService.getFollowees(localUserId, EntityType.ENTITY_USER, Integer.MAX_VALUE);
        }
        List<Integer> types=new ArrayList<>();
        types.add(EventType.ADD_QUESTION.getValue());
        types.add(EventType.COMMENT.getValue());
        types.add(EventType.FOLLOW.getValue());
        types.add(EventType.LIKE.getValue());

        List<Feed> feeds = feedService.getUserFeeds(Integer.MAX_VALUE, followees,types, 0,10);
        model.addAttribute("feeds", feeds);
        return "feeds";
    }


    /**
     * 加载关注你的新鲜事
     * @param offset
     * @param limit
     * @return  json字符串
     */
    @RequestMapping(path = {"/loadMoreFollowee/{offset}/{limit}"}, method = {RequestMethod.GET})
    @ResponseBody
    private String loadMoreFollowee(@PathVariable("offset") int offset,@PathVariable("limit") int limit) {
        int localUserId = hostHolder.getUser() != null ? hostHolder.getUser().getId() : 0;

        List<Integer> types=new ArrayList<>();
        types.add(EventType.FOLLOW.getValue());

        List<Feed> feeds = feedService.getMyFeeds(Integer.MAX_VALUE,localUserId,types,offset,limit);

        JSONObject vos=new JSONObject();
        if(feeds.size()<10){
            vos.put("hasNext",false);
        }else {
            vos.put("hasNext",true);
        }
        vos.put("feeds", feeds);
        return vos.toString();
    }

    /**
     * 加载赞你的赞
     * @param offset
     * @param limit
     * @return json字符串
     */

    @RequestMapping(path = {"/loadMoreAgree/{offset}/{limit}"}, method = {RequestMethod.GET})
    @ResponseBody
    private String loadMoreAgree(@PathVariable("offset") int offset,@PathVariable("limit") int limit) {
        int localUserId = hostHolder.getUser() != null ? hostHolder.getUser().getId() : 0;

        List<Integer> types=new ArrayList<>();
        types.add(EventType.LIKE.getValue());

        List<Feed> feeds = feedService.getMyFeeds(Integer.MAX_VALUE,localUserId,types,offset,limit);

        JSONObject vos=new JSONObject();
        if(feeds.size()<10){
            vos.put("hasNext",false);
        }else {
            vos.put("hasNext",true);
        }
        vos.put("feeds", feeds);
        return vos.toString();
    }




}
