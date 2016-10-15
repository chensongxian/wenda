package com.csx.controller;

import com.alibaba.fastjson.JSONObject;
import com.csx.async.EventModel;
import com.csx.async.EventProducer;
import com.csx.async.EventType;
import com.csx.model.*;
import com.csx.service.CommentService;
import com.csx.service.QiniuService;
import com.csx.service.QuestionService;
import com.csx.service.UserService;
import com.csx.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by csx on 2016/7/24.
 */
@Controller
public class CommentController {
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);
    @Autowired
    HostHolder hostHolder;

    @Autowired
    CommentService commentService;

    @Autowired
    QuestionService questionService;

    @Autowired
    EventProducer eventProducer;

    @Autowired
    QiniuService qiniuService;

    @Autowired
    UserService userService;

    @RequestMapping(path = {"/addComment"}, method = {RequestMethod.POST})
    @ResponseBody
    public String addComment(@RequestParam("questionId") int questionId,
                             @RequestParam("content") String content) {
        try {
            Comment comment = new Comment();
            System.out.println(content);
            comment.setContent(content);
            if (hostHolder.getUser() != null) {
                comment.setUserId(hostHolder.getUser().getId());
            } else {
                comment.setUserId(WendaUtil.ANONYMOUS_USERID);
                // return "redirect:/reglogin";
            }
            comment.setCreatedDate(new Date());
            comment.setEntityType(EntityType.ENTITY_QUESTION);
            comment.setEntityId(questionId);
            int id=commentService.addComment(comment);
            System.out.println("id"+id);

            int count = commentService.getCommentCount(comment.getEntityId(), comment.getEntityType());
            questionService.updateCommentCount(comment.getEntityId(), count);

            eventProducer.fireEvent(new EventModel(EventType.COMMENT).setActorId(comment.getUserId())
                    .setEntityId(questionId).setEntityOwnerId(comment.getId()).setExt("comment_content",content));

            JSONObject vo=new JSONObject();
            vo.put("comment",comment);
            vo.put("user", userService.getUser(comment.getUserId()));
            return WendaUtil.getJSONString(0,vo);
        } catch (Exception e) {
            logger.error("增加评论失败" + e.getMessage());
            return WendaUtil.getJSONString(1, "添加失败");
        }
    }

    @RequestMapping(value = "/getUser/{like}", method = {RequestMethod.GET})
    @ResponseBody
    public String getUser(@PathVariable("like") String like){
        List<User> users=userService.getUserByLike(like);
        List<JSONObject> vos=new ArrayList<JSONObject>();
        for(User user:users){
            JSONObject vo=new JSONObject();
            vo.put("id",user.getId());
            vo.put("name",user.getName());
            vos.add(vo);
        }
        JSONObject json=new JSONObject();
        json.put("vos",vos);
        return json.toJSONString();
    }

    @RequestMapping(path = {"/image"}, method = {RequestMethod.GET})
    @ResponseBody
    public void getImage(@RequestParam("name") String imageName, HttpServletResponse response) {
        try {
            response.setContentType("image/jpeg");
            StreamUtils.copy(new FileInputStream(new File(WendaUtil.IMAGE_DIR + imageName)), response.getOutputStream());
        } catch (IOException e) {
            logger.error("读取图片失败" + e.getMessage());
        }
    }

    @RequestMapping(path = {"/uploadImage/"}, method = {RequestMethod.POST})
    @ResponseBody
    public String uploadImage(@RequestParam("file") MultipartFile multipartFile) {
        try {
            String fileUrl = qiniuService.upload(multipartFile);
            if (fileUrl == null) {
                return WendaUtil.getJSONString(1, "上传失败");
            }
            return WendaUtil.getJSONString(0, fileUrl);
        } catch (Exception e) {
            logger.error("上传图片失败" + e.getMessage());
            return WendaUtil.getJSONString(1, "上传失败");
        }
    }
}
