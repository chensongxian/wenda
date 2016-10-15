package com.csx.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.csx.dao.QuestionDAO;
import com.csx.model.EntityType;
import com.csx.model.Question;
import com.csx.util.JedisSingleAdapter;
import com.csx.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by csx on 2016/7/15.
 */
@Service
public class QuestionService {
    private static final Logger logger = LoggerFactory.getLogger(QuestionService.class);
    @Autowired
    QuestionDAO questionDAO;

    @Autowired
    SensitiveService sensitiveService;

    @Autowired
    FollowService followService;

    @Autowired
    JedisSingleAdapter jedisAdapter;


    public Question getById(int id) {
        String json=jedisAdapter.get(RedisKeyUtil.getBizQuestionhotset(String.valueOf(id)));
        if(json!=null) {
            Question question=JSON.parseObject(json,Question.class);
            if(question!=null) {
                return question;
            }
        }
        return questionDAO.getById(id);
    }

    public int addQuestion(Question question) {
        question.setTitle(HtmlUtils.htmlEscape(question.getTitle()));
        question.setContent(HtmlUtils.htmlEscape(question.getContent()));
        // 敏感词过滤
        question.setTitle(sensitiveService.filter(question.getTitle()));
        question.setContent(sensitiveService.filter(question.getContent()));
        question.setTopicId(question.getTopicId());
        logger.info(""+question.getUserId());
        int id=questionDAO.addQuestion(question) > 0 ? question.getId() : 0;
        if(id!=0){
            String questionJson=JSON.toJSONString(question);
            jedisAdapter.set(RedisKeyUtil.getBizQuestionhotset(String.valueOf(id)),questionJson);
        }
        return id;
    }

    public List<Question> getLatestQuestionsByScore(int userId, int offset, int limit) {
        //添加缓存
        Set<String> idSet = jedisAdapter.zrevrange(RedisKeyUtil.getBizQustionhot(), offset, offset + limit);
        if(idSet==null||idSet.size()==0){
            List<Question> questions=questionDAO.selectQuestionsByScore(userId, offset, limit);
            addQuestionCache(questions);
            return questions;
        }
        List<Question> questionList = new ArrayList<>();
        for (String str : idSet) {
            String json = jedisAdapter.get(RedisKeyUtil.getBizQuestionhotset(str));
            if(json==null){
                break;
            }
            Question question = JSON.parseObject(json, Question.class);
            questionList.add(question);
        }
        if (questionList.size() < limit) {
            logger.info("不使用缓存");
            List<Question> questions=questionDAO.selectQuestionsByScore(userId, offset, limit);
            addQuestionCache(questions);
            return questions;
        } else {
            logger.info("使用缓存");
            return questionList;
         }
    }

    public List<Question> getLatestQuestions(int userId, int offset, int limit) {
        return questionDAO.selectLatestQuestions(userId, offset, limit);
    }

    public List<Question> getLatestQuestionsByScoreNoCache(int userId, int offset, int limit) {
        return questionDAO.selectQuestionsByScore(userId, offset, limit);
    }

    //加载问题和问题相关的信息
    public List<JSONObject> getLatestQuestionsAndInfo(int userId, int offset, int limit) {
        List<JSONObject> questionInfos = new ArrayList<JSONObject>();
        List<Question> questions = questionDAO.selectLatestQuestions(userId, offset, limit);
        for (Question question : questions) {
            JSONObject vo = new JSONObject();
            vo.put("question", question);
            vo.put("followCount", followService.getFollowerCount(EntityType.ENTITY_QUESTION, question.getId()));
            questionInfos.add(vo);
        }
        return questionInfos;
    }

    public int updateCommentCount(int id, int count) {
        jedisAdapter.set(RedisKeyUtil.getBizCommentcount(String.valueOf(id)),String.valueOf(count));
        return questionDAO.updateCommentCount(id, count);
    }

    public List<Question> getLatestQuestionsByTopicId(int topicId, int offset, int limit) {
        return questionDAO.selectLatestQuestionsByTopicId(topicId, offset, limit);
    }

    public long qView(int userId, int entityType, int entityId) {
        String qViewKey = RedisKeyUtil.getBizQviews(entityType, entityId);
        jedisAdapter.sadd(qViewKey, String.valueOf(userId));
        return jedisAdapter.scard(qViewKey);
    }

    public long getQViewKey(int entityType, int entityId) {
        String qViewKey = RedisKeyUtil.getBizQviews(entityType, entityId);
        return jedisAdapter.scard(qViewKey);
    }

    public int updateScore(int id, double score) {
        return questionDAO.updateScore(id, score);
    }


    public void addQuestionCache(List<Question> questions){
        for(Question question:questions){
            String questionJson=JSON.toJSONString(question);
            jedisAdapter.zadd(RedisKeyUtil.getBizQustionhot(),question.getScore(),String.valueOf(question.getId()));
            jedisAdapter.set(RedisKeyUtil.getBizQuestionhotset(String.valueOf(question.getId())),questionJson);
        }
    }
}
