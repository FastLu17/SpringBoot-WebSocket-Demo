package com.lxf.websocketdemo.oldVersion.service;

import com.lxf.websocketdemo.finalVersion.endpoint.SocketClientEndpoint;
import com.lxf.websocketdemo.oldVersion.endpoint.WebSocketServerEndpoint;
import com.lxf.websocketdemo.oldVersion.entity.WebMessage;
import com.lxf.websocketdemo.oldVersion.entity.WebUser;
import com.lxf.websocketdemo.oldVersion.repository.MessageRepository;
import com.lxf.websocketdemo.oldVersion.repository.UserRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
    private static final Log LOG = LogFactory.getLog(UserService.class);
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;


    public WebUser addUser(WebUser webUser) {

        WebUser user = userRepository.saveAndFlush(webUser);

        sendMessage(user.getId(), "恭喜你:" + user.getUserName() + ",已于" + LocalDateTime.now() + "成功完成用户注册。");

        return user;
    }

    public WebUser verifyUser(WebUser webUser) {
        LOG.info("webUser = " + webUser);
        WebUser user = userRepository.findByUserName(webUser.getUserName());

        if (user != null) {
            LOG.info("user = " + user);
            if (user.getPassword().equals(webUser.getPassword())) {

                LOG.info("通过密码校验、准备发送消息。");
                sendMessage(user.getId(), "欢迎" + user.getUserName() + ",于" + LocalDateTime.now() + "登录,请注意核实。");
                return user;
            }
        } else {
            LOG.info("user is null");
        }

        return user;
    }

    private void sendMessage(Long userId, String messageContent) {
        CopyOnWriteArrayList<WebSocketServerEndpoint> webSocketSet = WebSocketServerEndpoint.webSocketSet;

        LOG.info("webSocketSet.size() = " + webSocketSet.size());

        SocketClientEndpoint clientEndpoint = new SocketClientEndpoint();
        /**
         *  token和userId都需要通过参数传递进来.
         */
        boolean token = clientEndpoint.startByUserIdAndToken(String.valueOf(userId), "token");
        if (token) {
            try {
                clientEndpoint.sendMessage("通过startByUserIdAndToken发送消息给客户端.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        for (WebSocketServerEndpoint server : webSocketSet) {
        /*
            如果不进行userId判断,则是进行消息群发(普通消息).
         */
            if (server.getUserId().equals(String.valueOf(userId))) {//用户ID:1001肯定也是动态数据。不是所有用户都发送充值之类的消息、
                try {
                    WebMessage webMessage = new WebMessage();
                    webMessage.setContent(messageContent);
                    webMessage.setUserId(userId);
                    WebMessage saveAndFlush = messageRepository.saveAndFlush(webMessage);
                   LOG.info("存入数据:webMessage = " + saveAndFlush);

                    /*
                     *   这种方式只发给当前用户. 为什么？？
                     * */
                    server.getSession().getBasicRemote().sendText(messageContent);

                    /*
                     *   这种方式进行的是群发. 为什么？？
                     * */
                    SocketClientEndpoint socketClientEndpoint = new SocketClientEndpoint();
                    socketClientEndpoint.startByCreateNewConnection("ws://localhost:8080/websocket/1003");//主要是为了赋值Session对象、

                    try {
                        socketClientEndpoint.sendMessage("服务器主动向客户端发送消息：第" + 1 + "次。存入数据库内容：" + saveAndFlush);
                        Thread.sleep(3000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    socketClientEndpoint.closeSocket();

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

        LOG.info("更改后的信息：save = " + save);

        sendMessage(oldUser.getId(), "用户:" + oldUser.getUserName() + "于" + LocalDateTime.now() + "更改用户信息成功,用户名改为:" + save.getUserName() + "用户密码改完:" + save.getPassword());

        return save;
    }
}
