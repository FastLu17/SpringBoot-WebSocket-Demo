package com.lxf.websocketdemo.springboot.controller;

import com.lxf.websocketdemo.springboot.entity.WebUser;
import com.lxf.websocketdemo.springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 小66
 * @create 2019-06-27 16:35
 **/
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public WebUser login(String userName, String password) {
        WebUser user = new WebUser();
        user.setPassword(password);
        user.setUserName(userName);
        return userService.verifyUser(user);
    }

    @GetMapping("/add")
    public String add(String userName, String password) {
        WebUser user = new WebUser();
        user.setPassword(password);
        user.setUserName(userName);

        WebUser addUser = userService.addUser(user);

        return String.valueOf(addUser.getId());
    }

    @GetMapping("/update")
    public String update(String userId,String userName, String password) {
        WebUser user = new WebUser();
        user.setId(Long.parseLong(userId));
        user.setPassword(password);
        user.setUserName(userName);

        WebUser updateUser = userService.updateUser(user);

        return String.valueOf(updateUser.getId());
    }
}
