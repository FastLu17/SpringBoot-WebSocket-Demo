package com.lxf.websocketdemo.finalVersion.endpoint;

import com.lxf.websocketdemo.finalVersion.util.SocketSessionStorage;
import lombok.extern.slf4j.Slf4j;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.Map;

/**
 * 通过此注解、可以完成服务端主动向客户端发送消息.
 */
@ClientEndpoint
@Slf4j
public class SocketClientEndpoint {

    private Session session;

    @OnOpen
    public void onOpen(Session session) throws IOException {
        this.session = session;
        System.out.println("Socket is connected in SocketClientEndpoint.");
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println("SocketClientEndpoint get message :" + message);
    }

    @OnError
    public void onError(Throwable t) {
        t.printStackTrace();
    }

    /**
     * 连接关闭调用的方法
     *
     * @throws Exception
     */
    @OnClose
    public void onClose() throws Exception {
        System.out.println("socket is closed.");
    }

    /**
     * 关闭链接方法
     *
     * @throws IOException
     */
    public void closeSocket() throws IOException {
        this.session.close();
    }

    /**
     * 发送消息方法。
     *
     * @param message
     * @throws IOException
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    /**
     * 启动客户端并建立新链接  (***会导致消息群发***)
     * <p>
     * 【注意】:使用此方法、最后需要关闭socket连接、client.closeSocket();
     *
     * @param uri webSocket的URI、example："ws:http://localhost:8080/websocket"
     */
    public boolean startByCreateNewConnection(String uri) {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        try {
            this.session = container.connectToServer(SocketClientEndpoint.class, URI.create(uri));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     *      启动客户端并建立新链接 (不会消息群发) -->只会发给userID和token对应的用户。
     * @param userId
     * @param token
     * @return
     */
    public boolean startByUserIdAndToken(String userId, String token) {
        try {
            Map<String, SocketServerEndpoint> container = SocketSessionStorage.get(userId);
            SocketServerEndpoint endPoint = container.get(token);
            //如果找不到连接的容器，说明该客户端已断开连接，返回false
            if (endPoint == null)
                return false;
            this.session = endPoint.getSession();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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
            for (Map.Entry<String, SocketServerEndpoint> websocket : myWebSocket.entrySet()) {
                websocket.getValue().sendMessage(message);
            }
        }
    }
}