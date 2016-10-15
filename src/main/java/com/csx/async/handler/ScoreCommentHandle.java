package com.csx.async.handler;

import com.csx.async.EventHandler;
import com.csx.async.EventModel;
import com.csx.async.EventType;
import com.csx.model.EntityType;
import com.csx.model.Question;
import com.csx.service.CommentService;
import com.csx.service.LikeService;
import com.csx.service.QuestionService;
import com.csx.util.JedisSingleAdapter;
import com.csx.util.RedisKeyUtil;
import com.csx.util.ScoreUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Created by csx on 2016/10/3.
 */
@Component
public class ScoreCommentHandle implements EventHandler{
    private static final Logger logger = LoggerFactory.getLogger(ScoreCommentHandle.class);
    @Autowired
    QuestionService questionService;
    @Autowired
    LikeService likeService;
    @Autowired
    JedisSingleAdapter jedisAdapter;

    @Autowired
    CommentService commentService;

    //根据不同的事件获取其对应的提问
    private Question buildQuestion(EventModel model) {


        if (model.getType() == EventType.COMMENT ) {
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
        int commentId=0;
        if(model.getType()==EventType.COMMENT) {
            commentId= model.getEntityOwnerId();
        }else if(model.getType()==EventType.LIKE||model.getType()==EventType.DISLIKE){
            commentId=model.getEntityId();
        }

        Question question=buildQuestion(model);
        if(commentId==0||question==null){
            return;
        }

        long likeCount=likeService.getLikeCount(EntityType.ENTITY_COMMENT,commentId);
        long disLikeCount=likeService.getDisLikeCount(EntityType.ENTITY_COMMENT,commentId);

        double score= ScoreUtil.getScoreComment(likeCount,disLikeCount);
        String key= RedisKeyUtil.getBizCommentsort(String.valueOf(question.getId()));
        jedisAdapter.zadd(key,score,String.valueOf(commentId));

        logger.info("评论打分");

    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(new EventType[]{EventType.COMMENT,EventType.LIKE,EventType.DISLIKE});
    }
}
