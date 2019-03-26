package be.spider.chat.server.chat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ChatHandlerTest {
    @InjectMocks
    private ChatHandler chatHandler;

    @Mock
    private ChatService chatService;

    @Test
    public void shouldReturnChatStreamAsSSOResponse() {
        ServerRequest serverRequest = MockServerRequest.builder().build();
        Flux<String> chatMessages = Flux.just("msg1", "msg2");

        when(chatService.getChatStream()).thenReturn(chatMessages);

        Mono<ServerResponse> chatStream = chatHandler.getChatStream(serverRequest);

        ServerResponse serverResponse = chatStream.block();
        assertThat(serverResponse.statusCode()).isEqualTo(HttpStatus.OK);

        verify(chatService).getChatStream();
    }

}