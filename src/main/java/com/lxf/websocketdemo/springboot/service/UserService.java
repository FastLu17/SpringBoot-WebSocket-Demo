package com.lxf.websocketdemo.springboot.service;

import com.lxf.websocketdemo.springboot.endpoint.WebSocketServer;
import com.lxf.websocketdemo.springboot.entity.WebMessage;
import com.lxf.websocketdemo.springboot.entity.WebUser;
import com.lxf.websocketdemo.springboot.repository.MessageRepository;
import com.lxf.websocketdemo.springboot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author 小66
 * @create 2019-06-27 15:50
 **/
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;


    public WebUser addUser(WebUser webUser) {
        webUser.setId(System.currentTimeMillis());
        WebUser user = userRepository.saveAndFlush(webUser);

        if (user.getPassword().equals(webUser.getPassword())) {

            sendMessage(user.getId(), "恭喜你:" + user.getUserName() + ",已于" + LocalDateTime.now() + "成功完成用户注册。");
        }

        return user;
    }

    public WebUser verifyUser(WebUser webUser) {
        System.out.println("webUser = " + webUser);
        WebUser user = userRepository.findByUserName(webUser.getUserName());

        if (user != null) {
            System.out.println("user = " + user);
            if (user.getPassword().equals(webUser.getPassword())) {

                System.out.println("通过密码校验、准备发送消息。");
                sendMessage(user.getId(), "欢迎" + user.getUserName() + ",于" + LocalDateTime.now() + "登录,请注意核实。");
                return user;
            }
        } else {
            System.out.println("user is null");
        }

        return user;
    }

    private void sendMessage(Long userId, String messageContent) {
        CopyOnWriteArrayList<WebSocketServer> webSocketSet = WebSocketServer.webSocketSet;

        System.out.println("webSocketSet.size() = " + webSocketSet.size());

        for (WebSocketServer server : webSocketSet) {
        /*
            如果不进行userId判断,则是进行消息群发(普通消息).
         */
            if (server.getUserId().equals(String.valueOf(userId))) {//用户ID:1001肯定也是动态数据。不是所有用户都发送充值之类的消息、
                try {

                    server.getSession().getBasicRemote().sendText(messageContent);

                    WebMessage webMessage = new WebMessage();
                    webMessage.setContent(messageContent);
                    webMessage.setUserId(userId);
                    WebMessage saveAndFlush = messageRepository.saveAndFlush(webMessage);
                    System.out.println("存入数据:webMessage = " + saveAndFlush);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public WebUser updateUser(WebUser user) {
        Optional<WebUser> byId = userRepository.findById(user.getId());
        if (!byId.isPresent()) return null;
        WebUser oldUser = byId.get();

        WebUser save = userRepository.save(user);

        System.out.println("更改后的信息：save = " + save);

        sendMessage(oldUser.getId(), "用户:" + oldUser.getUserName() + "于" + LocalDateTime.now() + "更改用户信息成功,用户名改为:" + save.getUserName() + "用户密码改完:" + save.getPassword());

        return save;
    }
}
