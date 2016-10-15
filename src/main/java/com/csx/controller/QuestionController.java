package com.csx.controller;

import com.alibaba.fastjson.JSONObject;
import com.csx.async.EventModel;
import com.csx.async.EventProducer;
import com.csx.async.EventType;
import com.csx.model.*;
import com.csx.service.*;
import com.csx.util.WendaUtil;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by csx on 2016/7/22.
 */
@Controller
public class QuestionController {
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    QuestionService questionService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    UserService userService;

    @Autowired
    CommentService commentService;

    @Autowired
    FollowService followService;

    @Autowired
    LikeService likeService;

    @Autowired
    EventProducer eventProducer;

    @Autowired
    private VelocityEngine velocityEngine;


    @RequestMapping(value = "/question/{qid}", method = {RequestMethod.GET})
    public String toDetail(Model model, @PathVariable("qid") int qid) {
        Question question = questionService.getById(qid);
        model.addAttribute("question", question);

        List<ViewObject> followUsers = new ArrayList<ViewObject>();
        // 获取关注的用户信息
        List<Integer> users = followService.getFollowers(EntityType.ENTITY_QUESTION, qid, 20);
        for (Integer userId : users) {
            ViewObject vo = new ViewObject();
            User u = userService.getUser(userId);
            if (u == null) {
                continue;
            }
            vo.set("name", u.getName());
            vo.set("headUrl", u.getHeadUrl());
            vo.set("id", u.getId());
            followUsers.add(vo);
        }
        model.addAttribute("followUsers", followUsers);
        if (hostHolder.getUser() != null) {
            model.addAttribute("followed", followService.isFollower(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, qid));
        } else {
            model.addAttribute("followed", false);
        }

        //增加访问量
        questionService.qView(hostHolder.getUser().getId(),EntityType.ENTITY_QUESTION,qid);

        //给此问题重新打分
        eventProducer.fireEvent(new EventModel(EventType.QVIEW)
                .setEntityId(qid)
                .setEntityType(EntityType.ENTITY_QUESTION).setEntityOwnerId(qid));
        return "detail";
    }

//    @RequestMapping(value = "/question/{qid}/{offset}/{limit}", method = {RequestMethod.GET})
//    @ResponseBody
//    public String questionDetail(Model model, @PathVariable("qid") int qid,@PathVariable("offset") int offset,@PathVariable("limit") int limit) {
//
//        JSONObject json=new JSONObject();
//
//        List<Comment> commentList = commentService.getCommentsByEntity(qid, EntityType.ENTITY_QUESTION,0,10);
//        if(commentList.size()<10){
//            json.put("hasNextComment",false);
//        }else{
//            json.put("hasNextComment",true);
//        }
//        List<JSONObject> comments = new ArrayList<JSONObject>();
//        for (Comment comment : commentList) {
//            JSONObject vo = new JSONObject();
//            vo.put("comment", comment);
//            if (hostHolder.getUser() == null) {
//                vo.put("liked", 0);
//            } else {
//                vo.put("liked", likeService.getLikeStatus(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, comment.getId()));
//            }
//
//            vo.put("likeCount", likeService.getLikeCount(EntityType.ENTITY_COMMENT, comment.getId()));
//            vo.put("user", userService.getUser(comment.getUserId()));
//            comments.add(vo);
//        }
//
//        json.put("comments",comments);
//
//
//
//        return json.toJSONString();
//    }

    @RequestMapping(value = "/question/{qid}/{offset}/{limit}", method = {RequestMethod.GET})
    @ResponseBody
    public String questionDetail(Model model, @PathVariable("qid") int qid,@PathVariable("offset") int offset,@PathVariable("limit") int limit) {


        List<Comment> commentList = commentService.getCommentsByEntity(qid, EntityType.ENTITY_QUESTION,offset,limit);
        JSONObject json=new JSONObject();
        if(commentList.size()<10){
            json.put("hasNext",false);
        }else{
            json.put("hasNext",true);
        }
        List<ViewObject> comments = new ArrayList<ViewObject>();
        for (Comment comment : commentList) {
            ViewObject vo = new ViewObject();
            vo.set("comment", comment);
            if (hostHolder.getUser() == null) {
                vo.set("liked", 0);
            } else {
                vo.set("liked", likeService.getLikeStatus(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, comment.getId()));
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


            String createTime = sdf.format(comment.getCreatedDate());
            vo.set("createTime",createTime);
            vo.set("likeCount", likeService.getLikeCount(EntityType.ENTITY_COMMENT, comment.getId()));
            vo.set("user", userService.getUser(comment.getUserId()));
            comments.add(vo);
        }

//        model.addAttribute("comments", comments);
        Map map=new HashMap();
        map.put("comments",comments);

        String result = VelocityEngineUtils
                .mergeTemplateIntoString(velocityEngine,"comment.vm", "UTF-8",map);


        json.put("html",result);
        return json.toJSONString();
    }


    @RequestMapping(value = "/question/add", method = {RequestMethod.POST})
    @ResponseBody
    public String addQuestion(@RequestParam("title") String title, @RequestParam("content") String content,@RequestParam("topicId") int topicId) {
        try {
            Question question = new Question();
            question.setContent(content);
            question.setCreatedDate(new Date());
            question.setTitle(title);
            question.setTopicId(topicId);
            if (hostHolder.getUser() == null) {
                question.setUserId(WendaUtil.ANONYMOUS_USERID);
                // return WendaUtil.getJSONString(999);
            } else {
                question.setUserId(hostHolder.getUser().getId());
            }

            if (questionService.addQuestion(question) > 0) {
                int questionId=question.getId();
                eventProducer.fireEvent(new EventModel(EventType.ADD_QUESTION)
                        .setActorId(question.getUserId()).setEntityId(questionId)
                        .setExt("title", question.getTitle()).setExt("content", question.getContent()));

                //自己关注自己
                int userId=hostHolder.getUser().getId();
                boolean ret = followService.follow(userId, EntityType.ENTITY_QUESTION, questionId);

                eventProducer.fireEvent(new EventModel(EventType.FOLLOW)
                        .setActorId(userId).setEntityId(questionId)
                        .setEntityType(EntityType.ENTITY_QUESTION).setEntityOwnerId(questionId));
                return WendaUtil.getJSONString(0);
            }


        } catch (Exception e) {
            logger.error("增加题目失败" + e.getMessage());
        }
        return WendaUtil.getJSONString(1, "失败");
    }

}
