package bitc.full502.lostandfound.config;

import bitc.full502.lostandfound.websocket.AuthHandshakeInterceptor;
import bitc.full502.lostandfound.websocket.ChatHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChatHandler chatHandler;
    private final AuthHandshakeInterceptor authInterceptor;

    public WebSocketConfig(ChatHandler chatHandler, AuthHandshakeInterceptor authInterceptor) {
        this.chatHandler = chatHandler;
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // /chat 경로로 WebSocket 연결 허용
        registry.addHandler(chatHandler, "/chat")
                .addInterceptors(authInterceptor)
                .setAllowedOrigins("*");
    }
}
