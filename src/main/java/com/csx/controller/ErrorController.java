package com.csx.controller;

import org.apache.http.HttpResponse;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by csx on 2016/10/5.
 */
@Controller
public class ErrorController implements org.springframework.boot.autoconfigure.web.ErrorController{
    private static final String ERROR_PATH = "/error";

    @RequestMapping(value=ERROR_PATH)
    public String handleError(Model model){

        model.addAttribute("message","<strong style='color:red'>404错误</strong>");
        return "error";
    }

    @Override
    public String getErrorPath() {
        // TODO Auto-generated method stub
        return ERROR_PATH;
    }
}
