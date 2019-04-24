package be.spider.chat.server.jwt;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Component
public class JwtHandler {
    private final JwtTokenProvider jwtTokenProvider;
    private final static String AUTH_HEADER = "Authorization";
    private static final String AUTH_HEADER_PREFIX = "Bearer ";

    public JwtHandler(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public Mono<ServerResponse> createToken(ServerRequest request) {
        Mono<String> userName = request.bodyToMono(String.class);
        return userName.map(jwtTokenProvider::createToken)
                .flatMap(token -> ServerResponse.ok()
                        .header(AUTH_HEADER, AUTH_HEADER_PREFIX + token)
                        .body(BodyInserters.empty()));
    }

    public Mono<ServerResponse> filterToken(ServerRequest request, HandlerFunction<ServerResponse> next) {
        return getUserName(request)
                .map(userName -> next.handle(request))
                .orElse(ServerResponse.status(HttpStatus.UNAUTHORIZED).build());
    }

    public Optional<String> getUserName(ServerRequest request) {
        List<String> authHeaders = request.headers().header(AUTH_HEADER);

        if (authHeaders == null || authHeaders.size() == 0) {
            return Optional.empty();
        }

        String token = authHeaders.get(0).replace(AUTH_HEADER_PREFIX, "");

        return Optional.ofNullable(jwtTokenProvider.getSubject(token));
    }
}
