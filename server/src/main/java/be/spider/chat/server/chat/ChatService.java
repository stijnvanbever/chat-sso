package be.spider.chat.server.chat;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ChatService {
    private final SubscribableChannel messageChannel;

    public ChatService(@Qualifier("chatMessageChannel") SubscribableChannel messageChannel) {
        this.messageChannel = messageChannel;
    }

    public Flux<String> getChatStream() {
        return Flux.create(sink -> {
            MessageHandler handler = msg -> sink.next((String) msg.getPayload());
            messageChannel.subscribe(handler);
            sink.onCancel(() -> messageChannel.unsubscribe(handler));
        });
    }

    public Mono<Void> addMessage(String msg) {
        messageChannel.send(new GenericMessage<>(msg));
        return Mono.empty();
    }
}
