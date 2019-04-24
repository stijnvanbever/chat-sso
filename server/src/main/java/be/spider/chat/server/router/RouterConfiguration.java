package be.spider.chat.server.router;

import be.spider.chat.server.chat.ChatHandler;
import be.spider.chat.server.jwt.JwtHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@EnableWebFlux
@Configuration
public class RouterConfiguration {
    private final ChatHandler chatHandler;
    private final JwtHandler jwtHandler;

    public RouterConfiguration(ChatHandler chatHandler, JwtHandler jwtHandler) {
        this.chatHandler = chatHandler;
        this.jwtHandler = jwtHandler;
    }

    @Bean
    public RouterFunction<ServerResponse> routersWithAuthorization() {
        return RouterFunctions.route()
                .GET("/api/chat", chatHandler::getChatStream)
                .POST("/api/chat", chatHandler::addChat)
                .filter(jwtHandler::filterToken)
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> joinRouter() {
        return RouterFunctions.route()
                .POST("/api/chat/join", jwtHandler::createToken)
                .build();
    }
}
