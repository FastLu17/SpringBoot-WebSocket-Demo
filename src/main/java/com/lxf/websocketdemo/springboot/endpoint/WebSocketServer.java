package com.lxf.websocketdemo.springboot.endpoint;

import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * SpringBoot 使用此方式处理、  Spring不是用此方法、
 *
 * @ServerEndPoint 注解是一个类层次的注解，它的功能主要是将目前的类定义成一个websocket服务器端，
 * 注解的值将被用于监听用户连接的终端访问URL地址，客户端可以通过这个URL连接到websocket服务器端
 */
//@ServerEndpoint("/websocket")
@ServerEndpoint("/websocket/{userId}") //此处可以使用动态参数、
@Component
public class WebSocketServer {
    private static int onlineCount = 0;
    public static CopyOnWriteArrayList<WebSocketServer> webSocketSet = new CopyOnWriteArrayList<>();
    private Session session;
    private String userId;

    public Session getSession() {
        return session;
    }

    public String getUserId() {
        return userId;
    }

    /**
     * 单独服务器端给前端发送即时消息，不需要进行对话，则使用此办法进行消息发送。
     *
     * @return
     */
    public String sendText() {
//        try {
//
//            Session session = WebSocketServer.this.session;
//            System.out.println("session = " + session);//这种方式获取到的Session = null.
//
//            WebSocketServer.this.session.getBasicRemote().sendText("AAA");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        for (WebSocketServer server : webSocketSet) {
            try {
                if (server != null) {//这种方式得到的Session不为null. 已在onOpen()提前存入CopyOnWriteArrayList<WebSocketServer>中.
                    server.sendMessage("服务器收到客户端消息后，进行消息群发. message is :" + "AAAAAA");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return "TRUE";
    }


    //通过@PathParam(value = "userId")获取动态参数、
    @OnOpen
    public void onOpen(Session session, @PathParam(value = "userId") String userId) {
        this.session = session;
        webSocketSet.add(this);//加入CopyOnWriteArrayList<WebSockServer>对象中
        addOnlineCount();
        System.out.println("有新连接加入！当前在线人数为" + getOnlineCount());

        this.userId = userId;
        System.out.println("userId = " + this.userId);
    }

    @OnClose
    public void onClose() {
        webSocketSet.remove(this);
        subOnlineCount();
        System.out.println("有一连接关闭！当前在线人数为" + getOnlineCount());
    }

    @OnMessage
    public void onMessage(String message, Session session) {

        System.out.println("来自客户端的消息：" + message);
        //群发消息：类似聊天室功能就完成.(每个在线的用户都收的到回复的消息).如何完成未在线的用户也收到消息？
        for (WebSocketServer server : webSocketSet) {
            System.out.println("webSocketSet = " + webSocketSet.size());
            try {
                server.sendMessage("服务器收到客户端消息后，进行消息群发. message is :" + message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("发生错误！");
        throwable.printStackTrace();
    }

    /**
     * 发送消息给指定session的客户端、
     *
     * @param message
     * @throws IOException
     */
    private void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }


    /*
         这三个方法是统计在线人数的.
     */
    private static synchronized int getOnlineCount() {
        return onlineCount;
    }

    private static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    private static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }

}