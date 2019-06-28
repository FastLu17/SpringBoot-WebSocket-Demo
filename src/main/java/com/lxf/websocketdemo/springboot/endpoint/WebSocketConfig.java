package com.lxf.websocketdemo.springboot.endpoint;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @author 小66
 * @create 2019-06-26 17:07
 *
 *      注意:如果不是使用SpringBoot的内置容器，就不需要注入ServerEndpointExporter、
 **/
@Configuration
public class WebSocketConfig {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {

        return new ServerEndpointExporter();
    }
}
