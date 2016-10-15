package com.csx.controller;

import com.alibaba.fastjson.JSONObject;
import com.csx.model.EntityType;
import com.csx.model.Question;
import com.csx.model.Topic;
import com.csx.service.FollowService;
import com.csx.service.QuestionService;
import com.csx.service.TopicService;
import com.csx.service.UserService;
import com.csx.util.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by csx on 2016/9/13.
 */
@Controller
public class TopicController {


    @Autowired
    TopicService topicService;

    @Autowired
    QuestionService questionService;

    @Autowired
    FollowService followService;

    @Autowired
    UserService userService;

    @RequestMapping(path = "/topic",method = {RequestMethod.GET})
    public String topic(Model model){
        List<Topic> topicList=topicService.getTopicByLike("");
        model.addAttribute("topics",topicList);
        return "topic";
    }

    private List<JSONObject> getTopicJson(String like) {
        List<Topic> topicList = topicService.getTopicByLike(like);
        List<JSONObject> vos = new ArrayList<>();
        for (Topic topic : topicList) {
            JSONObject vo = new JSONObject();
            vo.put("topicId",topic.getTopicId());
            vo.put("topic",topic.getTopic());
            vos.add(vo);
        }
        return vos;
    }

    @RequestMapping(path = "/getTopic/{like}/",method = {RequestMethod.GET})
    @ResponseBody
    public String getTopic(@PathVariable("like") String like){
        List<JSONObject> topics=getTopicJson(like);
        return WendaUtil.getTopicListJson(topics);
    }

    private List<JSONObject> getQuestionsJson(int topicId, int offset, int limit) {
        List<Question> questionList = questionService.getLatestQuestionsByTopicId(topicId, offset, limit);
        List<JSONObject> vos = new ArrayList<>();
        for (Question question : questionList) {
            JSONObject vo = new JSONObject();
            vo.put("question", question);
            vo.put("topic",topicService.getTopicById(question.getTopicId()));
            vo.put("followCount", followService.getFollowerCount(EntityType.ENTITY_QUESTION, question.getId()));
            vo.put("user", userService.getUser(question.getUserId()));
            vos.add(vo);
        }
        return vos;
    }



    @RequestMapping(path = {"/loadMoreByTopicId/{topicId}/{offset}/{limit}/"}, method = {RequestMethod.GET})
    @ResponseBody
    public String loadMoreByTopicId(Model model,
                                    @PathVariable("topicId") int topicId,
                                    @PathVariable("offset") int offset,
                                    @PathVariable("limit") int limit) {
        List<JSONObject> list=getQuestionsJson(topicId,offset,limit);
        return  WendaUtil.getJSONString(list);
    }
}
