// controller/ErrorController.java
package com.doubao.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        // 将所有错误重定向到首页，由前端路由处理
        return "forward:/index.html";
    }


//    @RequestMapping("/")
//    public String getIndex(HttpServletRequest request) {
//        // 将所有错误重定向到首页，由前端路由处理
//        return "forward:/index.html";
//    }
}
