package be.spider.chat.server.chat;

import be.spider.chat.server.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ChatService {
    private final SubscribableChannel messageChannel;
    private final JwtTokenProvider jwtTokenProvider;

    public ChatService(@Qualifier("chatMessageChannel") SubscribableChannel messageChannel,
                       JwtTokenProvider jwtTokenProvider) {
        this.messageChannel = messageChannel;
        this.jwtTokenProvider = jwtTokenProvider;
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

    public Mono<String> joinChat(String userName) {
        return Mono.just(jwtTokenProvider.createToken(userName));
    }
}
