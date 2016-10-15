package com.csx.controller;

import com.alibaba.fastjson.JSONObject;
import com.csx.model.*;
import com.csx.service.*;
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
 * Created by csx on 2016/7/24.
 */
@Controller
public class SearchController {
    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);
    @Autowired
    SearchService searchService;

    @Autowired
    FollowService followService;

    @Autowired
    UserService userService;

    @Autowired
    QuestionService questionService;

    @Autowired
    CommentService commentService;

    @Autowired
    LikeService likeService;

    @Autowired
    HostHolder hostHolder;

    /**
     * 用于ajax请求搜索结果
     * @param keyword
     * @param offset
     * @param limit
     * @return json字符串
     */
    @RequestMapping(path = {"/searchMore/{offset}/{limit}/{keyword}"}, method = {RequestMethod.GET})
    @ResponseBody
    public String searchMore(
                         @PathVariable("keyword") String keyword,
                         @PathVariable("offset") int offset,
                         @PathVariable("limit") int limit) {
        try {
            List<SearchResult> resultList = searchService.searchContent(keyword, offset, limit,
                    "<em style='color:red'>", "</em>");
            List<JSONObject> vos = new ArrayList<>();
            for (SearchResult result : resultList) {
                if (result.getType() == EntityType.ENTITY_QUESTION) {
                    Question question = result.getQuestion();
                    Question q = questionService.getById(question.getId());
                    JSONObject vo = new JSONObject();
                    if (question.getContent() != null) {
                        q.setContent(question.getContent());
                    }
                    if (question.getTitle() != null) {
                        q.setTitle(question.getTitle());
                    }
                    vo.put("type",EntityType.ENTITY_QUESTION);
                    vo.put("question", q);
                    vo.put("followCount", followService.getFollowerCount(EntityType.ENTITY_QUESTION, question.getId()));
                    vo.put("user", userService.getUser(q.getUserId()));
                    vos.add(vo);
                } else if (result.getType() == EntityType.ENTITY_COMMENT) {
                    Comment comment = result.getComment();
                    Comment c = commentService.getCommentById(comment.getId());
                    if (comment.getContent() != null) {
                        c.setContent(comment.getContent());
                    }
                    JSONObject vo=new JSONObject();
                    vo.put("type", EntityType.ENTITY_COMMENT);
                    vo.put("comment", c);
                    Question question = questionService.getById(c.getEntityId());
                    vo.put("question", question);
                    vo.put("user", userService.getUser(c.getUserId()));
                    vo.put("likeCount", likeService.getLikeCount(EntityType.ENTITY_COMMENT, c.getId()));
                    vos.add(vo);
                }
            }
            JSONObject json=new JSONObject();
            json.put("vos", vos);
            json.put("keyword", keyword);
            return json.toJSONString();
        } catch (Exception e) {
            logger.error("搜索评论失败" + e.getMessage());
            return WendaUtil.getJSONString(1,"搜索错误");
        }
    }


    /**
     * 搜索
     * @param model
     * @param keyword
     * @param offset
     * @param count
     * @return
     */
    @RequestMapping(path = {"/search"}, method = {RequestMethod.GET})
    public String search(Model model, @RequestParam("q") String keyword,
                         @RequestParam(value = "type",defaultValue = "content") String type,
                         @RequestParam(value = "offset", defaultValue = "0") int offset,
                         @RequestParam(value = "count", defaultValue = "10") int count) {
        try {
            type=type.trim();
            if("".equals(keyword.trim())){
                return "result";
            }
            if(type.equalsIgnoreCase("content")){
                List<SearchResult> resultList = searchService.searchContent(keyword, offset, count,
                        "<em style='color:red;'>", "</em>");
                List<ViewObject> vos = new ArrayList<>();
                for (SearchResult result : resultList) {
                    if (result.getType() == EntityType.ENTITY_QUESTION) {
                        Question question = result.getQuestion();
                        Question q = questionService.getById(question.getId());
                        ViewObject vo = new ViewObject();
                        if (question.getContent() != null) {
                            q.setContent(question.getContent());
                        }
                        if (question.getTitle() != null) {
                            q.setTitle(question.getTitle());
                        }
                        vo.set("type",EntityType.ENTITY_QUESTION);
                        vo.set("question", q);
                        vo.set("followCount", followService.getFollowerCount(EntityType.ENTITY_QUESTION, question.getId()));
                        vo.set("user", userService.getUser(q.getUserId()));
                        vos.add(vo);
                    } else if (result.getType() == EntityType.ENTITY_COMMENT) {
                        Comment comment = result.getComment();
                        Comment c = commentService.getCommentById(comment.getId());
                        if (comment.getContent() != null) {
                            c.setContent(comment.getContent());
                        }
                        ViewObject vo=new ViewObject();
                        vo.set("type", EntityType.ENTITY_COMMENT);
                        vo.set("comment", c);
                        Question question = questionService.getById(c.getEntityId());
                        vo.set("question", question);
                        vo.set("user", userService.getUser(c.getUserId()));
                        vo.set("likeCount", likeService.getLikeCount(EntityType.ENTITY_COMMENT, c.getId()));
                        vos.add(vo);
                    }
                }
                model.addAttribute("vos", vos);
                model.addAttribute("keyword", keyword);
                return "result";
            }else if (type.equalsIgnoreCase("user")){
                List<User> userList = searchService.searchUser(keyword, offset, count);
                List<ViewObject> vos = new ArrayList<>();
                for(User user:userList){
                    User u=userService.getUser(user.getId());
                    ViewObject vo=new ViewObject();
                    vo.set("user",user);
                    vo.set("commentCount", commentService.getUserCommentCount(user.getId()));
                    vo.set("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, user.getId()));
                    vo.set("followeeCount", followService.getFolloweeCount(user.getId(), EntityType.ENTITY_USER));
                    int localUserId=hostHolder.getUser().getId();
                    if (localUserId != 0) {
                        vo.set("followed", followService.isFollower(localUserId, EntityType.ENTITY_USER, user.getId()));
                    } else {
                        vo.set("followed", false);
                    }
                    vos.add(vo);
                }
                model.addAttribute("userList",vos);
                model.addAttribute("keyword", keyword);
                return "resultUser";
            }

        } catch (Exception e) {
            logger.error("搜索评论失败" + e.getMessage());
        }
        return "result";

    }




    @RequestMapping(path = {"/searchUser/{offset}/{limit}/{keyword}"}, method = {RequestMethod.GET})
    @ResponseBody
    public String searchUser(
            @PathVariable("keyword") String keyword,
            @PathVariable("offset") int offset,
            @PathVariable("limit") int limit) {
        try {
            List<User> userList = searchService.searchUser(keyword, offset, limit);
            List<JSONObject> vos = new ArrayList<>();
            for(User user:userList){
                User u=userService.getUser(user.getId());
                JSONObject vo=new JSONObject();
                vo.put("user",u);
                vo.put("commentCount", commentService.getUserCommentCount(user.getId()));
                vo.put("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, user.getId()));
                vo.put("followeeCount", followService.getFolloweeCount(user.getId(), EntityType.ENTITY_USER));
                int localUserId=hostHolder.getUser().getId();
                if (localUserId != 0) {
                    vo.put("followed", followService.isFollower(localUserId, EntityType.ENTITY_USER, user.getId()));
                } else {
                    vo.put("followed", false);
                }
                vos.add(vo);
            }
            JSONObject json=new JSONObject();
            json.put("userList",vos);
            return json.toJSONString();
        } catch (Exception e) {
            logger.error("搜索用户失败" + e.getMessage());
            return WendaUtil.getJSONString(1,"搜索错误");
        }
    }



}
