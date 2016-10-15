package com.csx;

import com.csx.util.MailSender;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by csx on 2016/9/7.
 */
public class TestEmail {
    @Autowired
    MailSender mailSender;
    @Test
    public void testEmail(){
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("username","csx");
        mailSender.sendWithHTMLTemplate("1164077611@qq.com", "登陆IP异常", "mails/login_exception.html", map);
    }

    @Test
    public void testDate() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss" );
        Date date = dateFormat.parse("2016-09-16 20:59:14" );

        Date dateNow=new Date();
        long val=(dateNow.getTime()-date.getTime())/ (3600);
        System.out.println(val);

    }




}
