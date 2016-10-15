package com.csx.async.handler;

import com.csx.async.EventHandler;
import com.csx.async.EventModel;
import com.csx.async.EventType;
import com.csx.model.Comment;
import com.csx.model.EntityType;
import com.csx.model.Question;
import com.csx.service.CommentService;
import com.csx.service.FollowService;
import com.csx.service.LikeService;
import com.csx.service.QuestionService;
import com.csx.util.JedisSingleAdapter;
import com.csx.util.RedisKeyUtil;
import com.csx.util.ScoreUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by csx on 2016/10/3.
 */
@Component
public class ScoreQuestionHandle implements EventHandler{
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ScoreQuestionHandle.class);
    @Autowired
    QuestionService questionService;
    @Autowired
    CommentService commentService;
    @Autowired
    JedisSingleAdapter jedisAdapter;
    @Autowired
    FollowService followService;
    @Autowired
    LikeService likeService;


    //根据不同的事件获取其对应的提问
    private Question buildQuestion(EventModel model) {


        if(model.getType()==EventType.QVIEW){
            Question question=questionService.getById(model.getEntityId());
            if(question==null){
                return null;
            }
            return question;
        }
        if (model.getType() == EventType.COMMENT ) {
            Question question = questionService.getById(model.getEntityId());
            if (question == null) {
                return null;
            }
            return question;
        }

        if(model.getType() == EventType.FOLLOW  && model.getEntityType() == EntityType.ENTITY_QUESTION){
            Question question = questionService.getById(model.getEntityId());
            if (question == null) {
                return null;
            }

            return question;
        }

        if(model.getType()==EventType.ADD_QUESTION){
            Question question = questionService.getById(model.getEntityId());
            if (question == null) {
                return null;
            }
            return question;
        }
        if(model.getType()==EventType.LIKE||model.getType()==EventType.DISLIKE){
            int questionId=Integer.parseInt(model.getExt("questionId"));
            Question question=questionService.getById(questionId);
            if(question==null){
                return null;
            }
            return question;

        }

        return null;
    }

    @Override
    public void doHandle(EventModel model) {

        Question question=buildQuestion(model);
        if(question==null){
            return;
        }
        long qView=questionService.getQViewKey(EntityType.ENTITY_QUESTION,question.getId());
        int qAnswers=question.getCommentCount();
        long qScore=followService.getFollowerCount(EntityType.ENTITY_QUESTION,question.getId());
        long aScore=likeService.getQuestionLikeCount(question.getId());

        Date date_ask=question.getCreatedDate();

        Date date_active=null;
        if(qAnswers!=0) {
            List<Comment> comments=commentService.getCommentsByEntity(question.getId(),EntityType.ENTITY_QUESTION,0,1);
            date_active = comments.get(0).getCreatedDate();
        }else{
            date_active=date_ask;
        }
        double score= ScoreUtil.getScoreQuestion(qView,qAnswers,qScore,aScore,date_ask,date_active);
        jedisAdapter.zadd(RedisKeyUtil.getBizQustionhot(),score,String.valueOf(question.getId()));

        logger.info("重新打分");
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(new EventType[]{EventType.COMMENT,EventType.ADD_QUESTION,EventType.FOLLOW,EventType.LIKE,EventType.QVIEW,EventType.DISLIKE});
    }
}
