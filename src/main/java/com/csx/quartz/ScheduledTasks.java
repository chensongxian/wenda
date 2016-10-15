package com.csx.quartz;

import com.csx.service.CommentService;
import com.csx.service.QuestionService;
import com.csx.util.JedisSingleAdapter;
import com.csx.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Created by csx on 2016/10/4.
 */
@Component
@Configurable
@EnableScheduling
public class ScheduledTasks {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

    @Autowired
    QuestionService questionService;
    @Autowired
    CommentService commentService;

    @Autowired
    JedisSingleAdapter jedisAdapter;

    public void work(){
        //这儿插入具体的调度任务
        updateQuestionScore();
        updateCommentScore();
        delCache();

    }

    public void updateQuestionScore(){
        int count= (int) jedisAdapter.zcard(RedisKeyUtil.getBizQustionhot());
        if(count==0){
            return;
        }
        Set<String> set=jedisAdapter.zrange(RedisKeyUtil.getBizQustionhot(),0,count);
        if(set==null||set.size()==0){
            return;
        }
        for(String str:set){
            int id=Integer.parseInt(str);
            double score=jedisAdapter.zscore(RedisKeyUtil.getBizQustionhot(),str);
//            System.out.println(score);
            questionService.updateScore(id,score);
        }
        logger.info("更新数据库---更新问题打分");
    }

    public void updateCommentScore(){

        Set<String> set=jedisAdapter.getAll(RedisKeyUtil.getBizCommentsort(""));
        if(set==null||set.size()==0){
            return;
        }
        for(String key:set){
            int count= (int) jedisAdapter.zcard(key);
            Set<String> idSet=jedisAdapter.zrange(key,0,count);
            for(String str:idSet){
                int id=Integer.parseInt(str);
                double score=jedisAdapter.zscore(key,str);
//                System.out.println(score);
                commentService.updateScore(id,score);
            }
        }
        logger.info("更新数据库---更新评论打分");
    }

    public void delCache(){
        boolean isDelQuestionHotSet=jedisAdapter.delAll(RedisKeyUtil.getBizQuestionhotset(""));
        if(!isDelQuestionHotSet){
            logger.error("删除问题缓存失败");
        }
        boolean isDelQuestionHot=jedisAdapter.delAll(RedisKeyUtil.getBizQustionhot());
        if(!isDelQuestionHotSet){
            logger.error("删除问题排序失败");
        }

        boolean isDelCommentHotSet=jedisAdapter.delAll(RedisKeyUtil.getBizCommentset(""));
        if(!isDelCommentHotSet){
            logger.error("删除评论缓存失败");
        }

        boolean isDelCommentHot=jedisAdapter.delAll(RedisKeyUtil.getBizCommentsort(""));

        if(!isDelCommentHot){
            logger.error("删除评论失败");
        }

        logger.info("删除缓存成功");

    }



}
