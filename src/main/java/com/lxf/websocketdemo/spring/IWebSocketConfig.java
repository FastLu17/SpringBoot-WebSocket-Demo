package com.lxf.websocketdemo.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

// 只适合Spring项目
/**
 * 实现 WebSocketConfigurer 接口，重写 registerWebSocketHandlers 方法，核心实现方法，
 *      配置 websocket 入口，允许访问的域、注册 Handler、SockJs 支持和拦截器。
 *      registry.addHandler()注册和路由的功能，当客户端发起 websocket 连接，把 /path 交给对应的 handler 处理，而不实现具体的业务逻辑，可以理解为收集和任务分发中心。
 *      addInterceptors，顾名思义就是为 handler 添加拦截器，可以在调用 handler 前后加入我们自己的逻辑代码。
 *
 *      ServletServerContainerFactoryBean可以添加对WebSocket的一些配置.
 *
 *
 * @author 小66
 * @create 2019-06-26 22:56
 **/

@Configuration
@EnableWebSocket
public class IWebSocketConfig implements WebSocketConfigurer {

    /**
     *      WebSocket的核心方法、
     * @param webSocketHandlerRegistry
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        //访问路径为：http:localhost:8080/webSocket、就会开启HandshakeInterceptor.
        webSocketHandlerRegistry.addHandler(webSocketHandler(), "/webSocket/*").addInterceptors(new IWebSocketHandshakeInterceptor()).setAllowedOrigins("*");

        //withSockJS() 方法声明我们想要使用 SockJS 功能,如果WebSocket不可用的话，会使用 SockJS;
        /*
            SockJS 所处理的 URL 是 http:// 或 https:// 模式，而不是 ws:// or wss://
         */
        webSocketHandlerRegistry.addHandler(webSocketHandler(), "/sockjs/webSocketServer")
                .addInterceptors(new IWebSocketHandshakeInterceptor()).withSockJS();
    }

    @Bean
    public WebSocketHandler webSocketHandler() {
        return new IWebSocketHandler();
    }


    /**
     *  对WebSocket的容器配置.
     * @return
     */
    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(8192*4);
        container.setMaxBinaryMessageBufferSize(8192*4);
        return container;
    }
}
