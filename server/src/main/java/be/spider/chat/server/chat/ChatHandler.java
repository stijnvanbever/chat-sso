package be.spider.chat.server.chat;

import be.spider.chat.server.jwt.JwtHandler;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class ChatHandler {
    private final ChatService chatService;
    private final JwtHandler jwtHandler;

    public ChatHandler(ChatService chatService, JwtHandler jwtHandler) {
        this.chatService = chatService;
        this.jwtHandler = jwtHandler;
    }


    public Mono<ServerResponse> getChatStream(ServerRequest request) {
        Flux<String> chatStream = chatService.getChatStream();
        Flux<ServerSentEvent<String>> sseChat = chatStream
                .map(message -> ServerSentEvent.builder(message).build());
        return ServerResponse.ok().body(BodyInserters.fromServerSentEvents(sseChat));
    }

    public Mono<ServerResponse> addChat(ServerRequest request) {
        String userName = jwtHandler.getUserName(request).get();
        Mono<String> msg = request.bodyToMono(String.class);
        return msg.map(message -> chatService.addMessage(userName, message))
                .flatMap(str -> ServerResponse.ok().body(BodyInserters.fromObject("added")));
    }


}
