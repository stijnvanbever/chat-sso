package be.spider.chat.server.chat;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.messaging.SubscribableChannel;

@Configuration
public class ChatConfiguration {
    @Bean
    public SubscribableChannel chatMessageChannel() {
        return MessageChannels.publishSubscribe().get();
    }
}
