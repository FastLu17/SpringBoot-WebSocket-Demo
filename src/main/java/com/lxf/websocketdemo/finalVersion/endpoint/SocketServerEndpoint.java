package com.lxf.websocketdemo.finalVersion.endpoint;

import com.lxf.websocketdemo.finalVersion.util.SocketEventListener;
import com.lxf.websocketdemo.finalVersion.util.SocketSessionStorage;
import lombok.Data;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;


@ServerEndpoint(value = "/websocket_token/{userId}/{token}")
@Component
@Scope("prototype")
@Data
public class SocketServerEndpoint {
    private String userId;

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    //连接时执行
    @OnOpen
    public void onOpen(@PathParam("userId") String userId, @PathParam("token") String token, Session session) throws IOException {
        this.userId = userId;
        this.session = session;
        SocketEventListener.onOpen(userId, token, this);
    }

    //关闭时执行
    @OnClose
    public void onClose(@PathParam("userId") String userId, @PathParam("token") String token) {
        SocketEventListener.onClose(userId, token, this);
    }

    //收到消息时执行
    @OnMessage
    public void onMessage(String message, @PathParam("userId") String userId) throws IOException, EncodeException {
        SocketEventListener.onMessage(message, userId, this);
    }

    //连接错误时执行
    @OnError
    public void onError(Session session, @PathParam("userId") String userId, Throwable error) {
        SocketEventListener.onError(userId, session, error);
    }

    /**
     * 发送消息方法。
     *
     * @param message
     * @throws IOException
     * @throws EncodeException
     */
    public void sendMessage(Object message) throws IOException, EncodeException {
        System.out.println("接收消息的客户端 userId = " + userId);
        this.session.getBasicRemote().sendObject(message);
    }

    /**
     * 群发消息方法。
     *
     * @param message
     * @throws IOException
     * @throws EncodeException
     */
    public void sendMessageAll(String message) throws IOException, EncodeException {
        for (Map<String, SocketServerEndpoint> myWebSocket : SocketSessionStorage.getValues()) {
            for (Entry<String, SocketServerEndpoint> websocket : myWebSocket.entrySet()) {
                websocket.getValue().sendMessage(message);
            }
        }
    }

    /**
     * 关闭链接方法
     *
     * @throws IOException
     */
    public void closeSocket() throws IOException {
        this.session.close();
    }

}