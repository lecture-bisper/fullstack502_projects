package bitc.full502.lostandfound.service;

import bitc.full502.lostandfound.domain.entity.ChatRoomEntity;
import bitc.full502.lostandfound.domain.entity.FcmTokenEntity;
import bitc.full502.lostandfound.domain.repository.ChatRoomRepository;
import bitc.full502.lostandfound.domain.repository.FcmTokenRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FcmServiceImpl implements FcmService {

    private final FcmTokenRepository fcmTokenRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final JpaService jpaService;

    @Override
    public void sendNotification(String userToken, String receiverId, String body, Long roomIdx) throws Exception {
        String userId = jpaService.getUserIdByToken(userToken);

        // receiverId의 모든 FCM 토큰 조회
        List<FcmTokenEntity> fcmTokens = fcmTokenRepository.findAllByUserId(receiverId);
        if (fcmTokens.isEmpty()) return;

        Optional<ChatRoomEntity> chatroomOpt = chatRoomRepository.findById(roomIdx);
        if (chatroomOpt.isEmpty()) return;

        ChatRoomEntity chatroom = chatroomOpt.get();
        String boardTitle = chatroom.getBoard().getTitle();
        String boardIdx = String.valueOf(chatroom.getBoard().getIdx());

        for (FcmTokenEntity fcmToken : fcmTokens) {
            Message message = Message.builder()
                    .setToken(fcmToken.getToken())
                    .putData("boardIdx", boardIdx)
                    .putData("title", boardTitle)
                    .putData("body", body)
                    .putData("senderId", userId)
                    .putData("receiverId", receiverId)
                    .putData("type", "chat")
                    .build();

            try {
                String response = FirebaseMessaging.getInstance().send(message);
                System.out.println("Successfully sent notification to token: " + fcmToken.getToken() + " | Response: " + response);
            } catch (FirebaseMessagingException e) {
                System.err.println("FCM 전송 실패 (토큰: " + fcmToken.getToken() + "): " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
