package com.csx.controller;

import com.alibaba.fastjson.JSONObject;
import com.csx.async.EventType;
import com.csx.model.*;
import com.csx.service.*;
import com.csx.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by csx on 2016/7/10.
 */
@Controller
public class SettingController {
    private static final Logger logger = LoggerFactory.getLogger(SettingController.class);
    @Autowired
    WendaService wendaService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    CommentService commentService;

    @Autowired
    FollowService followService;

    @Autowired
    JedisSingleAdapter jedisAdapter;

    @Autowired
    MailSender mailSender;

    @Autowired
    UserService userService;

    @Autowired
    QiniuService qiniuService;

    @Autowired
    FeedService feedService;

    @RequestMapping(path = {"/setting"}, method = {RequestMethod.GET})
    @ResponseBody
    public String setting(HttpSession httpSession) {
        return "Setting OK. " + wendaService.getMessage(1);
    }

    @RequestMapping(path = {"user/set"}, method = {RequestMethod.GET})
    public String toSet(Model model) {
        User user = null;
        if (hostHolder.getUser() != null) {
            user = hostHolder.getUser();
        } else {
            return "index";
        }

        int userId = user.getId();

        ViewObject vo = new ViewObject();
        vo.set("user", user);
        vo.set("commentCount", commentService.getUserCommentCount(userId));
        vo.set("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, userId));
        vo.set("followeeCount", followService.getFolloweeCount(userId, EntityType.ENTITY_USER));


        model.addAttribute("profileUser", vo);
        return "set";
    }

    @RequestMapping(path = {"/toSendEmail/{email}/"}, method = {RequestMethod.GET})
    public String toSendEmail(Model model,@PathVariable("email") String email) {
        try {
            User user = null;
            if (hostHolder.getUser() != null) {
                user = hostHolder.getUser();
            } else {
                return "index";
            }

            String code = RandomCode.randomCode(10);
            int id=user.getId();
            String key=code+ id;
            jedisAdapter.setEx(key, 180, email);

            System.out.println("key:"+key);
            //链接
            //// TODO: 2016/9/19 项目发布后改成网址 
            String link="http://localhost:8080/register/bind-email/"+id+"/"+code;
            System.out.println("link:"+link);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("username", user.getName());
            map.put("link", link);
            boolean isSuccess = mailSender.sendWithHTMLTemplate(email, "请激活邮箱", "mails/sendBindEmail.html", map);
            if (isSuccess) {
                model.addAttribute("email",email);
                return "sendEmail";
            } else {
                model.addAttribute("message","发送邮箱失败");
                return "info";
            }
        } catch (Exception e) {
            logger.error("发送邮箱失败" + e.getMessage());
            model.addAttribute("message","发送邮箱失败");
            return "info";
        }

    }

    @RequestMapping(path = {"/register/bind-email/{id}/{code}"}, method = {RequestMethod.GET})
    public String updateEmail(Model model,@PathVariable("id") int id,@PathVariable("code") String code){
        try {
            String key=code+id;
            String email=jedisAdapter.getEx(key);
            System.out.println("email是："+email+"---id是:"+id);
            if(email==null){
                model.addAttribute("message","激活失败");
                return "info";
            }
            userService.updateEmail(id,email);
            model.addAttribute("message","激活成功，邮箱已经更改");
            return "info";
        }catch (Exception e) {
            logger.error("更改邮箱失败" + e.getMessage());
            model.addAttribute("message","更改邮箱失败");
            return "info";
        }
    }

    @RequestMapping(path = {"/updatePw"},method = {RequestMethod.POST})
    @ResponseBody
    public String updatePw(@RequestParam("oldPw") String oldPw,@RequestParam("newPw") String newPw){
        User user=hostHolder.getUser();
        if(!WendaUtil.MD5(oldPw+user.getSalt()).equals(user.getPassword())){
            return WendaUtil.getJSONString(1,"密码不正确");
        }
        user.setPassword(WendaUtil.MD5(newPw+user.getSalt()));
        userService.updatePw(user);
        return WendaUtil.getJSONString(0);
    }


    @RequestMapping(path = {"/success"},method = {RequestMethod.GET})
    public String success(Model model){
        model.addAttribute("message","修改密码成功");
        return "info";
    }

    @RequestMapping(path = {"/updateInfo"},method = {RequestMethod.POST})
    @ResponseBody
    public String updateInfo(@RequestParam("name") String name,
                             @RequestParam("sex")boolean sex,
                             @RequestParam("introduction")String introduction,
                             @RequestParam("livePlace")String livePlace){

        User user=userService.selectByName(name);
        User user1=hostHolder.getUser();
        if(user!=null&&!user.getName().equalsIgnoreCase(user1.getName())){
            return WendaUtil.getJSONString(1,"用户名已存在");
        }

        user1.setName(name);
        user1.setSex(sex);
        user1.setIntroduction(introduction);
        user1.setLivePlace(livePlace);

        userService.updateUserInfo(user1);
        return WendaUtil.getJSONString(0);
    }

    @RequestMapping(path = {"/haveName"},method = {RequestMethod.POST})
    @ResponseBody
    public String updateInfo(@RequestParam("name") String name){

        User user=userService.selectByName(name);
        if(user!=null){
            return WendaUtil.getJSONString(1,"用户名已存在");
        }
        return WendaUtil.getJSONString(0);
    }


    @RequestMapping(path = {"/setHeadImg"},method = {RequestMethod.POST})
    @ResponseBody
    public String setHeadImg(@RequestParam("img") String img){

        try {

            img=img.split(",")[1];
            String fileUrl = null;
            fileUrl = qiniuService.uploadBase64(img);
            if (fileUrl == null) {
                logger.error("头像上传失败");
                return WendaUtil.getJSONString(1, "修改图像失败");
            }
            User user=hostHolder.getUser();
            user.setHeadUrl(fileUrl);
            userService.updateHeadUrl(user);
            return WendaUtil.getJSONString(0, fileUrl);
        } catch (Exception e) {
            logger.error("头像修改失败"+e.getMessage());
            return WendaUtil.getJSONString(1,"修改图像失败");
        }

    }


    @RequestMapping(path = {"/hasNew"},method = {RequestMethod.POST})
    @ResponseBody
    public String hasNew(){
        int localUserId = hostHolder.getUser() != null ? hostHolder.getUser().getId() : 0;
        String count=jedisAdapter.get(RedisKeyUtil.getBizNewfeed(localUserId));
        JSONObject vo=new JSONObject();
        if(count==null){
            vo.put("hasNew",false);
            return vo.toJSONString();
        }
        int number=Integer.parseInt(count);
        if(number==0){
            vo.put("hasNew",false);
            return vo.toJSONString();
        }
        vo.put("hasNew",true);
        vo.put("count",count);
        return vo.toJSONString();
    }


    @RequestMapping(path = {"/setNewRead"},method = {RequestMethod.POST})
    @ResponseBody
    public String setNewRead(){
        int localUserId = hostHolder.getUser() != null ? hostHolder.getUser().getId() : 0;
        jedisAdapter.set(RedisKeyUtil.getBizNewfeed(localUserId),"0");
        return WendaUtil.getJSONString(0);
    }

    @RequestMapping(path = {"/loadMoreProfileInfo/{offset}/{limit}"},method ={RequestMethod.GET})
    @ResponseBody
    public String loadMoreProfileInfo(@PathVariable("offset") int offset,@PathVariable("limit") int limit){
        int localUserId = hostHolder.getUser() != null ? hostHolder.getUser().getId() : 0;
        List<Integer> types=new ArrayList<>();
        types.add(EventType.ADD_QUESTION.getValue());
        types.add(EventType.COMMENT.getValue());
        types.add(EventType.FOLLOW.getValue());
        types.add(EventType.LIKE.getValue());

        List<Integer> followees=new ArrayList<>();
        followees.add(localUserId);

        List<Feed> feeds=feedService.getUserFeeds(Integer.MAX_VALUE,followees,types,offset,limit);

        JSONObject vos=new JSONObject();
        if(feeds.size()<10){
            vos.put("hasNext",false);
        }else {
            vos.put("hasNext",true);
        }
        vos.put("feeds", feeds);
        return vos.toString();
    }

}
