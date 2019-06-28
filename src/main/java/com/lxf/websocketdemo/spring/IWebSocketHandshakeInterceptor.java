package com.lxf.websocketdemo.spring;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

// 只适合Spring项目
/**
 * 握手拦截器、
 *
 * @author 小66
 * @create 2019-06-26 22:54
 **/
public class IWebSocketHandshakeInterceptor implements HandshakeInterceptor {

    /**
     * 握手之前调用
     *
     * @param serverHttpRequest  当前请求
     * @param serverHttpResponse 当前响应
     * @param webSocketHandler   目标处理器
     * @param map                请求属性
     * @return 是否通过
     * @throws Exception 异常信息
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Map<String, Object> map) throws Exception {
        System.out.println("连接前进行处理");
//        return false;
        return true;
    }

    /**
     * 握手之后调用
     *
     * @param serverHttpRequest  当前请求
     * @param serverHttpResponse 当前响应
     * @param webSocketHandler   目标处理器
     * @param e                  握手期间引发的异常，如果没有，则为null
     */
    @Override
    public void afterHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Exception e) {
        System.out.println("连接后进行处理");
    }
}
