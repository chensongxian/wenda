package com.csx.async.handler;

import com.csx.async.EventHandler;
import com.csx.async.EventModel;
import com.csx.async.EventType;
import com.csx.util.MailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by csx on 2016/7/30.
 */
@Component
public class LoginExceptionHandler implements EventHandler {
    @Autowired
    MailSender mailSender;

    @Override
    public void doHandle(EventModel model) {
        // xxxx判断发现这个用户登陆异常
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("username", model.getExt("username"));
        mailSender.sendWithHTMLTemplate(model.getExt("email"), "登陆IP异常", "mails/login_exception.html", map);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LOGIN);
    }
}
