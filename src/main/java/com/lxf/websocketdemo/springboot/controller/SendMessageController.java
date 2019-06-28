package com.lxf.websocketdemo.springboot.controller;

import com.lxf.websocketdemo.springboot.endpoint.WebSocketServer;
import com.lxf.websocketdemo.springboot.entity.WebMessage;
import com.lxf.websocketdemo.springboot.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 *  真实情况不应该写为Controller、
 *      只会在服务器内部调用. 可以改为Component/Service即可.
 *
 * @author 小66
 * @create 2019-06-27 0:59
 **/
@RestController
public class SendMessageController {

    @Autowired
    private MessageRepository messageRepository;


    @GetMapping("/send")
    public String method(){
        CopyOnWriteArrayList<WebSocketServer> webSocketSet = WebSocketServer.webSocketSet;

        System.out.println("webSocketSet.size() = " + webSocketSet.size());

        for (WebSocketServer server : webSocketSet) {
            /*
                如果不进行userId判断,则是进行消息群发(普通消息).
             */
            if (server.getUserId().equals("1001")) {//用户ID:1001肯定也是动态数据。不是所有用户都发送充值之类的消息、
                try {
                    server.getSession().getBasicRemote().sendText("这里的数据可以是用户进行操作之后，反馈的消息:" + server.getUserId());
                } catch (IOException e) {
                    e.printStackTrace();
                    return "False.";
                }
            }
        }
        return "Success.";
    }

    @GetMapping("/messages")
    public List<WebMessage> getMessages(){
        return messageRepository.findAll();
    }

}
