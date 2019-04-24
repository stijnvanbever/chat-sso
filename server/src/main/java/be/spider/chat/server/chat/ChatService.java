package be.spider.chat.server.chat;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class ChatService {
    private final SubscribableChannel messageChannel;
    private static final String USERNAME_HEADER = "USERNAME";

    public ChatService(@Qualifier("chatMessageChannel") SubscribableChannel messageChannel) {
        this.messageChannel = messageChannel;
    }

    public Flux<String> getChatStream() {
        return Flux.create(sink -> {
            MessageHandler handler = msg -> sink.next((String) buildChatMessage(msg));
            messageChannel.subscribe(handler);
            sink.onCancel(() -> messageChannel.unsubscribe(handler));
        });
    }

    private String buildChatMessage(Message<?> msg) {
        return msg.getHeaders().get(USERNAME_HEADER) + ": " + msg.getPayload();
    }

    public Mono<Void> addMessage(String userName, String msg) {
        messageChannel.send(new GenericMessage<>(msg, Map.of(USERNAME_HEADER, userName)));
        return Mono.empty();
    }

}
