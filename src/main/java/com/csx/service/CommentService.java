package com.csx.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.csx.dao.CommentDAO;
import com.csx.model.Comment;
import com.csx.model.EntityType;
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
 * Created by csx on 2016/7/24.
 */
@Service
public class CommentService {
    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);
    @Autowired
    CommentDAO commentDAO;

    @Autowired
    SensitiveService sensitiveService;

    @Autowired
    QuestionService questionService;

    @Autowired
    LikeService likeService;

    @Autowired
    JedisSingleAdapter jedisAdapter;

    public List<Comment> getCommentsByEntity(int entityId, int entityType,int offset,int limit) {
        Set<String> ids=jedisAdapter.zrevrange(RedisKeyUtil.getBizCommentsort(String.valueOf(entityId)),offset,offset+limit);
        if(ids==null||ids.size()==0){
            List<Comment> comments=commentDAO.selectCommentByEntityAndScore(entityId, entityType,offset,limit);
            addCommentCache(comments);
            return comments;
        }
        List<Comment> commentList=new ArrayList<>();
        for(String idStr:ids){
            int id=Integer.parseInt(idStr);
            String commentJson=jedisAdapter.get(RedisKeyUtil.getBizCommentset(String.valueOf(id)));
            if(commentJson==null){
                break;
            }
            Comment comment=JSON.parseObject(commentJson,Comment.class);
            commentList.add(comment);
        }
        if(commentList.size()<limit){
            List<Comment> comments=commentDAO.selectCommentByEntityAndScore(entityId, entityType,offset,limit);
            addCommentCache(comments);
            logger.info("不使用缓存");
            return comments;
        }else {
            logger.info("使用缓存");
            return commentList;
        }
    }

    public int addComment(Comment comment) {
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveService.filter(comment.getContent()));
        int id=commentDAO.addComment(comment) > 0 ? comment.getId() : 0;
        if(id!=0){
            String commentJson= JSON.toJSONString(comment);
            jedisAdapter.set(RedisKeyUtil.getBizCommentset(String.valueOf(comment.getId())),commentJson);
        }
        return id;
    }

    public int getCommentCount(int entityId, int entityType) {
        return commentDAO.getCommentCount(entityId, entityType);
    }

    public int getUserCommentCount(int userId) {
        return commentDAO.getUserCommentCount(userId);
    }

    public boolean deleteComment(int commentId) {
        return commentDAO.updateStatus(commentId, 1) > 0;
    }

    public Comment getCommentById(int id) {
        String commentJSON=jedisAdapter.get(RedisKeyUtil.getBizCommentset(String.valueOf(id)));
        if(commentJSON!=null) {
            Comment comment = JSON.parseObject(commentJSON,Comment.class);
            if(comment!=null){
                return comment;
            }
        }

        return commentDAO.getCommentById(id);
    }


    public List<Comment> getCommentByUserId(int userId,int offset,int limit){
        return commentDAO.selectByUserId(userId,offset,limit);
    }

    public List<JSONObject> getCommentAndInfoByUserId(int userId, int offset, int limit) {
        List<JSONObject> commentInfos=new ArrayList<JSONObject>();
        List<Comment> comments=commentDAO.selectByUserId(userId, offset, limit);
        for(Comment comment:comments){
            JSONObject vo=new JSONObject();
            vo.put("comment",comment);
            vo.put("question",questionService.getById(comment.getEntityId()));
            vo.put("like",likeService.getLikeCount(EntityType.ENTITY_COMMENT,comment.getId()));
            commentInfos.add(vo);
        }
        return commentInfos;
    }

    public List<Comment> selectAll(){
        return commentDAO.selectAll();
    }

    public void addCommentCache(List<Comment> comments){
        for(Comment comment:comments){
            String commentJson= JSON.toJSONString(comment);
            jedisAdapter.zadd(RedisKeyUtil.getBizCommentsort(String.valueOf(comment.getEntityId())),comment.getScore(),String.valueOf(comment.getId()));
            jedisAdapter.set(RedisKeyUtil.getBizCommentset(String.valueOf(comment.getId())),commentJson);
        }
    }


    public int updateScore(int id,double score){
        return commentDAO.updateScore(id,score);
    }
}

