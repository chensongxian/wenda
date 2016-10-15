package com.csx.service;

import com.csx.dao.LoginTicketDAO;
import com.csx.dao.UserDAO;
import com.csx.model.HostHolder;
import com.csx.model.LoginTicket;
import com.csx.model.User;
import com.csx.util.JedisSingleAdapter;
import com.csx.util.WendaUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by csx on 2016/7/2.
 */
@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserDAO userDAO;

    @Autowired
    private LoginTicketDAO loginTicketDAO;

    @Autowired
    JedisSingleAdapter jedisAdapter;

    @Autowired
    HostHolder hostHolder;

    public User selectByName(String name) {
        return userDAO.selectByName(name);
    }
    public User selectByEmail(String email) {
        return userDAO.selectByEmail(email);
    }

    public Map<String, Object> register(String username,String email, String password,String code) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (StringUtils.isBlank(username)) {
            map.put("msg", "用户名不能为空");
            return map;
        }

        if (StringUtils.isBlank(email)) {
            map.put("msg", "邮箱不能为空");
            return map;
        }

        if (StringUtils.isBlank(password)) {
            map.put("msg", "密码不能为空");
            return map;
        }

        if (StringUtils.isBlank(code)) {
            map.put("msg", "验证码不能为空");
            return map;
        }

        User user = userDAO.selectByName(username);

        if (user != null) {
            map.put("msg", "用户名已经被注册");
            return map;
        }

        User user1=userDAO.selectByEmail(email);

        if(user1!=null){
            map.put("msg","邮箱已被注册");
            return map;
        }

        String scode=jedisAdapter.getEx(email);
        if(!code.equals(scode)){
            map.put("msg","验证码错误");
            return map;
        }

        // 密码强度
        user = new User();
        user.setName(username);
        user.setSalt(UUID.randomUUID().toString().substring(0, 5));
        String head = String.format("http://images.csx.com/head/%dt.png", new Random().nextInt(1000));
        user.setHeadUrl(head);
        user.setPassword(WendaUtil.MD5(password+user.getSalt()));
        user.setEmail(email);
        userDAO.addUser(user);

        // 登陆
        String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);
        return map;
    }


    public Map<String, Object> login(String email, String password) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (StringUtils.isBlank(email)) {
            map.put("msg", "用户名不能为空");
            return map;
        }

        if (StringUtils.isBlank(password)) {
            map.put("msg", "密码不能为空");
            return map;
        }

        User user = userDAO.selectByEmail(email);

        if (user == null) {
            map.put("msg", "用户名不存在");
            return map;
        }

        if (!WendaUtil.MD5(password+user.getSalt()).equals(user.getPassword())) {
            map.put("msg", "密码不正确");
            return map;
        }

        String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);
        map.put("userId", user.getId());
        return map;
    }

    private String addLoginTicket(int userId) {
        LoginTicket ticket = new LoginTicket();
        ticket.setUserId(userId);
        Date date = new Date();
        date.setTime(date.getTime() + 1000*3600*24);
        ticket.setExpired(date);
        ticket.setStatus(0);
        ticket.setTicket(UUID.randomUUID().toString().replaceAll("-", ""));
        loginTicketDAO.addTicket(ticket);
        return ticket.getTicket();
    }

    public User getUser(int id) {
        return userDAO.selectById(id);
    }

    public void logout(String ticket) {
        loginTicketDAO.updateStatus(ticket, 1);
    }

    public List<User> getUserByLike(String like){
        return userDAO.selectByLike(like);
    }

    public void updateEmail(int id,String email){
        userDAO.updateEmail(id,email);
    }

    public void updatePw(User user){
        userDAO.updatePassword(user);
    }

    public void updateUserInfo(User user){
        userDAO.updateUserInfo(user);
    }

    public void updateHeadUrl(User user){
        userDAO.updateHeadUrl(user);
    }
}
