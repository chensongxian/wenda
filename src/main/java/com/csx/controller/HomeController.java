package com.csx.controller;

import com.alibaba.fastjson.JSONObject;
import com.csx.model.*;
import com.csx.service.*;
import com.csx.util.JedisSingleAdapter;
import com.csx.util.RedisKeyUtil;
import com.csx.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by csx on 2016/7/15.
 */
@Controller
public class HomeController {
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    QuestionService questionService;

    @Autowired
    UserService userService;

    @Autowired
    FollowService followService;

    @Autowired
    CommentService commentService;

    @Autowired
    TopicService topicService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    JedisSingleAdapter jedisSingleAdapter;

    private List<ViewObject> getQuestions(int userId, int offset, int limit) {
        List<Question> questionList = questionService.getLatestQuestionsByScore(userId, offset, limit);
        List<ViewObject> vos = new ArrayList<>();
        for (Question question : questionList) {
            ViewObject vo = new ViewObject();
            vo.set("question", question);
            vo.set("commentCount",jedisSingleAdapter.get(RedisKeyUtil.getBizCommentcount(String.valueOf(question.getId()))));
            vo.set("topic",topicService.getTopicById(question.getTopicId()));
            vo.set("followCount", followService.getFollowerCount(EntityType.ENTITY_QUESTION, question.getId()));
            vo.set("user", userService.getUser(question.getUserId()));
            if (hostHolder.getUser() != null) {
                vo.set("followed", followService.isFollower(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, question.getId()));
            } else {
                vo.set("followed", false);
            }
            vos.add(vo);
        }
        return vos;
    }

    private List<JSONObject> getQuestionsJson(int userId, int offset, int limit) {
        List<Question> questionList = questionService.getLatestQuestionsByScore(userId, offset, limit);
        List<JSONObject> vos = new ArrayList<>();
        for (Question question : questionList) {
            JSONObject vo = new JSONObject();
            vo.put("question", question);
            vo.put("commentCount",jedisSingleAdapter.get(RedisKeyUtil.getBizCommentcount(String.valueOf(question.getId()))));
            vo.put("topic",topicService.getTopicById(question.getTopicId()));
            vo.put("followCount", followService.getFollowerCount(EntityType.ENTITY_QUESTION, question.getId()));
            vo.put("user", userService.getUser(question.getUserId()));
            if (hostHolder.getUser() != null) {
                vo.put("followed", followService.isFollower(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, question.getId()));
            } else {
                vo.put("followed", false);
            }
            vos.add(vo);
        }
        return vos;
    }

    @RequestMapping(path = {"/", "/index"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String index(Model model,
                        @RequestParam(value = "pop", defaultValue = "0") int pop) {
        model.addAttribute("vos", getQuestions(0, 0, 10));
        return "index";
    }

    //主页加载问题
    @RequestMapping(path = {"/loadMore/{offset}/{limit}/"}, method = {RequestMethod.GET})
    @ResponseBody
    public String loadMore(Model model,
                        @PathVariable("offset") int offset,
                           @PathVariable("limit") int limit) {
        List<JSONObject> list=getQuestionsJson(0,offset,limit);
        return  WendaUtil.getJSONString(list);
    }

    //个人主页
    @RequestMapping(path = {"/user/{userId}"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String userIndex(Model model, @PathVariable("userId") int userId) {
        model.addAttribute("vos", getQuestions(userId, 0, 3));

        User user = userService.getUser(userId);
        ViewObject vo = new ViewObject();
        vo.set("user", user);
        vo.set("commentCount", commentService.getUserCommentCount(userId));
        vo.set("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, userId));
        vo.set("followeeCount", followService.getFolloweeCount(userId, EntityType.ENTITY_USER));
        vo.set("questionInfos",questionService.getLatestQuestionsAndInfo(userId,0,3));
        vo.set("commentInfos",commentService.getCommentAndInfoByUserId(userId,0,3));
        if (hostHolder.getUser() != null) {
            vo.set("followed", followService.isFollower(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId));
        } else {
            vo.set("followed", false);
        }
        model.addAttribute("profileUser", vo);
        return "profile";
    }

    //ajax请求个人主页的个人回答信息
    @RequestMapping(path = {"/user/{userId}/answer/{offset}/{limit}"},method = {RequestMethod.GET})
    @ResponseBody
    public String loadMoreProfileAnswer(@PathVariable("userId") int userId,@PathVariable("offset") int offset,@PathVariable("limit")int limit){
        JSONObject vo=new JSONObject();
        List<JSONObject> commentInfos=commentService.getCommentAndInfoByUserId(userId,offset,limit);
        vo.put("commentInfos",commentInfos);
        if(commentInfos.size()<10){
            vo.put("hasNext",false);
        }else{
            vo.put("hasNext",true);
        }
        return vo.toJSONString();
    }

    //跳到个人主页的更多回答页面
    @RequestMapping(path = {"/user/profileAnswer/{userId}"},method = {RequestMethod.GET})
    public String toprofileAnswer(Model model,@PathVariable("userId") int userId){
        model.addAttribute("userId",userId);
        return "profileAnswer";
    }


    //ajax请求个人主页的个人提问信息
    @RequestMapping(path = {"/user/{userId}/question/{offset}/{limit}"},method = {RequestMethod.GET})
    @ResponseBody
    public String loadMoreProfileQuestion(@PathVariable("userId") int userId,@PathVariable("offset") int offset,@PathVariable("limit")int limit){
        JSONObject vo=new JSONObject();
        List<JSONObject> questionInfos=questionService.getLatestQuestionsAndInfo(userId,offset,limit);
        vo.put("questionInfos",questionInfos);
        if(questionInfos.size()<10){
            vo.put("hasNext",false);
        }else{
            vo.put("hasNext",true);
        }
        return vo.toJSONString();
    }

    //跳到个人主页的更多提问页面
    @RequestMapping(path = {"/user/profileQuestion/{userId}"},method = {RequestMethod.GET})
    public String toprofileQuestion(Model model,@PathVariable("userId") int userId){
        model.addAttribute("userId",userId);
        return "profileQuestion";
    }
    @RequestMapping(path = {"/test"},method = {RequestMethod.GET})
    public String toTest(){
        return "test";
    }
}
