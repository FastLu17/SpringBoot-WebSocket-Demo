package com.lxf.websocketdemo.finalVersion.util;

import com.lxf.websocketdemo.finalVersion.endpoint.SocketServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

import javax.websocket.Session;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * websocket服务端事件监听器的封装处理.
 *
 * @author: 小66
 */
@Slf4j
public class SocketEventListener {
    /**
     * onOpen 连接到websocket时触发(开发中需要userId和token保证安全)
     * ***** userId相同、token不同,即为多端登录. *****
     *
     * @param userId         用户ID信息
     * @param token          token认证
     * @param serverEndpoint 可以获取Session对象
     */
    public static void onOpen(@Nullable String userId, @Nullable String token, SocketServerEndpoint serverEndpoint) {
        Map<String, SocketServerEndpoint> sessionMap = SocketSessionStorage.get(userId);
        if (sessionMap == null) {
            sessionMap = new HashMap<>();
        }
        boolean containUserId = SocketSessionStorage.webSocketMap.containsKey(userId);
        boolean containToken = sessionMap.containsKey(token);
        if (containUserId && containToken) {
            try {
                sessionMap.get(token).closeSocket();
            } catch (IOException e) {
                e.printStackTrace();
                log.error("执行closeSocket失败,当前SocketServerEndpoint is ：{}" + sessionMap.get(token));
            }
        }
        int oldSize = sessionMap.size();
        sessionMap.put(token, serverEndpoint);
        if (oldSize != sessionMap.size() || !containUserId)
            SocketSessionStorage.addOnlineCount();
        SocketSessionStorage.put(userId, sessionMap);

        log.info("当前总人数: {}", SocketSessionStorage.webSocketMap.size());
        log.info("[[websocket onOpen]] 新连接：{},当前总连接数：{}", userId, SocketSessionStorage.getOnlineCount());
        for (Map.Entry<String, Map<String, SocketServerEndpoint>> stringMapEntry : SocketSessionStorage.webSocketMap.entrySet()) {
            log.info("当前用户：{}下共有{}个客户端登录.", stringMapEntry.getKey(), stringMapEntry.getValue().size());
        }
    }

    /**
     * onClose 断开websocket时触发
     *
     * @param userId         用户ID
     * @param token          token认证
     * @param serverEndPoint 可以获取Session对象、
     */
    public static void onClose(@Nullable String userId, @Nullable String token, SocketServerEndpoint serverEndPoint) {
        /*
         * 	该用户的所有多端session是否都被注销、
         * */
        boolean isUserSessionAllOver = false; //需要根据业务逻辑去处理onClose()方法.

        Map<String, SocketServerEndpoint> sessionMap = SocketSessionStorage.get(userId);
        if (sessionMap == null || sessionMap.isEmpty()) {
            SocketSessionStorage.remove(userId);
            SocketSessionStorage.subOnlineCount();
            log.info("[[websocket onClose --> sessionMap is Empty.]] 用户：{}断开连接,当前总连接数：{}", userId, SocketSessionStorage.getOnlineCount());
        } else {
            sessionMap.remove(token);
            if (sessionMap.isEmpty()) {
                SocketSessionStorage.remove(userId);
                SocketSessionStorage.subOnlineCount();
                log.info("[[websocket onClose]] 用户：{}断开连接,当前总连接数：{}", userId, SocketSessionStorage.getOnlineCount());
            } else {
                //一个用户有多个token(多端登录),则移除退出的一端的token,其他端的token依然存在。
                SocketSessionStorage.subOnlineCount();
                log.info("[[websocket onClose]] 用户：{}断开连接,当前总连接数：{}", userId, SocketSessionStorage.getOnlineCount());
                log.info("[[websocket onClose]] 用户：{}多端登录数量减少1个,还剩：{}个.", userId, sessionMap.size());
                SocketSessionStorage.put(userId, sessionMap);
            }
        }
    }

    /**
     * onMessage 客户端向websocket通信时触发
     *
     * @param message           客户端发送到服务器的消息
     * @param userId            根据业务需求,确认是否需要传递此参数、
     * @param websocketEndPoint 可以获取Session对象、
     */
    public static void onMessage(String message, @Nullable String userId, SocketServerEndpoint websocketEndPoint) {
        if (userId == null)
            log.info("[[websocket onMessage]] 客户端发送内容为：{}", message);
        log.info("[[websocket onMessage]] id为：{}的用户,发送内容为：{}", userId, message);
        try {
            websocketEndPoint.getSession().getBasicRemote().sendText("Server接收到Client发送的消息后,完成业务逻辑并返回数据给Client.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * onError 连接websocket错误时触发
     *
     * @param userId
     * @param session
     */
    public static void onError(String userId, Session session, Throwable error) {
        log.debug("[[websocket onError]] 用户id为：{}的连接发送错误", userId);
        error.printStackTrace();
    }
}
