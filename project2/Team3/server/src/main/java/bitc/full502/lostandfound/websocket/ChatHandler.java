package bitc.full502.lostandfound.websocket;

import bitc.full502.lostandfound.dto.ChatDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatHandler extends TextWebSocketHandler {

    // 사용자ID ↔ 세션 매핑
    private final Map<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        System.out.println("클라이언트 접속: " + session.getId());
        // 사용자ID는 쿼리 파라미터로 전달한다고 가정
        String username = getUsernameFromSession(session);
        if (username != null) {
            userSessions.put(username, session);
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        ChatDTO chatDTO = mapper.readValue(message.getPayload(), ChatDTO.class);

        // 발신자는 항상 세션에서 가져온 username
        chatDTO.setSender(getUsernameFromSession(session));
        System.out.println("채팅 정보: " + chatDTO);

        // target에게만 전송
        WebSocketSession targetSession = userSessions.get(chatDTO.getTarget());
        if (targetSession != null && targetSession.isOpen()) {
            targetSession.sendMessage(new TextMessage(mapper.writeValueAsString(chatDTO)));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        userSessions.values().removeIf(s -> s.getId().equals(session.getId()));
        System.out.println("클라이언트 종료: " + session.getId());
    }

    // 안전하게 세션 속성에서 username 가져오기
    private String getUsernameFromSession(WebSocketSession session) {
        return (String) session.getAttributes().get("username");
    }

    public boolean isUserOnline(String username) {
        WebSocketSession session = userSessions.get(username);
        System.out.println("username = " +  username);
        System.out.println("session = " + session);
        return session != null && session.isOpen();
    }
}


