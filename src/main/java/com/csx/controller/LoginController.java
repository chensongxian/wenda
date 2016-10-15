package com.csx.controller;

import com.csx.async.EventModel;
import com.csx.async.EventProducer;
import com.csx.async.EventType;
import com.csx.service.UserService;
import com.csx.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.exception.MacroOverflowException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by csx on 2016/7/2.
 */
@Controller
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    UserService userService;

    @Autowired
    EventProducer eventProducer;

    @Autowired
    JedisSingleAdapter jedisAdapter;

    @Autowired
    MailSender mailSender;

    @RequestMapping(path = {"/reg/"}, method = {RequestMethod.POST})
    public String reg(Model model, @RequestParam("username") String username,
                      @RequestParam("email") String email,
                      @RequestParam("password") String password,
                      @RequestParam("code") String code,
                      @RequestParam("next") String next,
                      @RequestParam(value="rememberme", defaultValue = "false") boolean rememberme,
                      HttpServletResponse response) {
        try {
            Map<String, Object> map = userService.register(username,email,password,code.trim());
            if (map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
                cookie.setPath("/");
                if (rememberme) {
                    cookie.setMaxAge(3600*24*5);
                }
                response.addCookie(cookie);
                int userId=Integer.parseInt((String) map.get("userId"));
                //添加索引
                eventProducer.fireEvent(new EventModel(EventType.REG).setActorId(userId)
                        .setEntityId(userId).setEntityOwnerId(userId).setExt("username",username));
                if (StringUtils.isNotBlank(next)) {
                    return "redirect:" + next;
                }
                return "redirect:/";
            } else {
                model.addAttribute("msg", map.get("msg"));
                return "reg";
            }

        } catch (Exception e) {
            logger.error("注册异常" + e.getMessage());
            model.addAttribute("msg", "服务器错误");
            return "reg";
        }
    }

    @RequestMapping(path = {"/reglogin"}, method = {RequestMethod.GET})
    public String regloginPage(Model model, @RequestParam(value = "next", required = false) String next) {
        model.addAttribute("next", next);
        model.addAttribute("msg","请登录");
        return "login";
    }

    @RequestMapping(path = {"/toReg/"}, method = {RequestMethod.GET})
    public String toReg() {
        return "reg";
    }

    //发送邮件验证码
    @RequestMapping(path = {"/sendCode/"},method = {RequestMethod.POST})
    @ResponseBody
    public String sendCode(Model model,@RequestParam("username") String username,
                           @RequestParam("email") String email){
        try {
            String code=RandomCode.randomCode(5);
            jedisAdapter.setEx(email,60,code);
            Map<String,Object> map=new HashMap<String,Object>();
            map.put("username",username);
            map.put("code",code);
            boolean isSuccess=mailSender.sendWithHTMLTemplate(email, "验证码", "mails/reg.html", map);
            if(isSuccess){
                return WendaUtil.getJSONString(0);
            }else{
                return WendaUtil.getJSONString(1, "发送验证码失败");
            }
        }catch (Exception e){
            logger.error("发送消息失败" + e.getMessage());
            return WendaUtil.getJSONString(1, "发送验证码失败");
        }

    }

    @RequestMapping(path = {"/login/"}, method = {RequestMethod.POST})
    public String login(Model model, @RequestParam("email") String email,
                        @RequestParam("password") String password,
                        @RequestParam(value="next", required = false) String next,
                        @RequestParam(value="rememberme", defaultValue = "false") boolean rememberme,
                        HttpServletResponse response) {
        try {
            Map<String, Object> map = userService.login(email, password);
            if (map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
                cookie.setPath("/");
                if (rememberme) {
                    cookie.setMaxAge(3600*24*5);
                }
                response.addCookie(cookie);
//                User user=userService.selectByEmail(email);
//                eventProducer.fireEvent(new EventModel(EventType.LOGIN)
//                        .setExt("username", user.getName()).setExt("email",email)
//                        .setActorId((int)map.get("userId")));

                if (StringUtils.isNotBlank(next)) {
                    return "redirect:" + next;
                }
                return "redirect:/";
            } else {
                model.addAttribute("msg", map.get("msg"));
                return "login";
            }

        } catch (Exception e) {
            logger.error("登陆异常" + e.getMessage());
            return "login";
        }
    }

    @RequestMapping(path = {"/logout"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        return "redirect:/";
    }

}
