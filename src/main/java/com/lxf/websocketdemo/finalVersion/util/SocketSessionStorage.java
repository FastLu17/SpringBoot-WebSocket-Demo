package com.lxf.websocketdemo.finalVersion.util;

import com.lxf.websocketdemo.finalVersion.endpoint.SocketServerEndpoint;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 小66
 * @create 2019-07-19 16:07
 * <p>
 * 存储ServerEndpoint对象(复杂点),
 * 简单点也可以直接存入Session(javax.websocket.Session)对象、
 * <p>
 * 可以使用userId和token作为Key来取出Session对象、进行后续操作
 **/
public class SocketSessionStorage {
    //计数器、
    private static int onlineCount = 0;

    public static Map<String, Map<String, SocketServerEndpoint>> webSocketMap = new ConcurrentHashMap<>();

    /**
     * 当打开webSocket连接时、需要将session保存.
     *
     * @param key
     * @param myWebSocket
     */
    public static void put(String key, Map<String, SocketServerEndpoint> myWebSocket) {
        webSocketMap.put(key, myWebSocket);
    }

    public static Map<String, SocketServerEndpoint> get(String key) {
        return webSocketMap.get(key);
    }

    public static void remove(String key) {
        webSocketMap.remove(key);
    }

    public static Collection<Map<String, SocketServerEndpoint>> getValues() {
        return webSocketMap.values();
    }

    /*
         这三个方法是统计在线人数的.
     */
    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        SocketSessionStorage.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        SocketSessionStorage.onlineCount--;
    }
}