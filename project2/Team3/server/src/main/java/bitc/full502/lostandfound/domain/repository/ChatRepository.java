package bitc.full502.lostandfound.domain.repository;

import bitc.full502.lostandfound.domain.entity.ChatEntity;
import bitc.full502.lostandfound.domain.entity.ChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<ChatEntity, Long> {
    List<ChatEntity> findAllByChatRoomOrderBySendDateAsc(ChatRoomEntity chatRoom);

    int countByChatRoomIdxAndStatus(Long chatRoomIdx, String status);

    int countByChatRoomIdxAndStatusAndSenderNot(Long chatRoomIdx, String status, String sender);
}
