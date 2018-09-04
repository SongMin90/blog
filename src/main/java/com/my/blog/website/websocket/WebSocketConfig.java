package com.my.blog.website.websocket;

import cn.binarywang.tools.generator.ChineseNameGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

@Configuration
public class WebSocketConfig {

	@Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
	
}
