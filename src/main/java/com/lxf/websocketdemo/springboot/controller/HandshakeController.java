package com.lxf.websocketdemo.springboot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 用于连接WebSocket的Controller
 *
 * @author 小66
 * @create 2019-06-26 17:04
 **/

@Controller
public class HandshakeController {

    @GetMapping("/web/{userId}")
    public String test1(Model model, @PathVariable(value = "userId") String userId) {
        model.addAttribute("userId", userId);
        return "web";
    }

    @GetMapping("/web2/{userId}")
    public String test2(Model model, @PathVariable(value = "userId") String userId) {
        model.addAttribute("userId", userId);
        return "web2";
    }

    @GetMapping("/index")
    public String index(){

        return "index";
    }

}
