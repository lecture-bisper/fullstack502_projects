package bitc.full502.lostandfound.domain.repository;


import bitc.full502.lostandfound.domain.entity.BoardEntity;
import bitc.full502.lostandfound.domain.entity.ChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long> {

    @Query("SELECT c FROM ChatRoomEntity c WHERE c.userId1 = :userId OR c.userId2 = :userId")
    List<ChatRoomEntity> findAllByUserId(String userId);


    @Query("SELECT c FROM ChatRoomEntity c " +
            "WHERE ((c.userId1 = :userId1 AND c.userId2 = :userId2) " +
            "    OR (c.userId1 = :userId2 AND c.userId2 = :userId1)) " +
            "  AND c.board.idx = :boardIdx")
    Optional<ChatRoomEntity> findChatRoomBetweenUsers(
            @Param("userId1") String userId1,
            @Param("userId2") String userId2,
            @Param("boardIdx") Long boardIdx);

    @Query("SELECT c FROM ChatRoomEntity c " +
            "WHERE (c.userId1 = :userId OR c.userId2 = :userId) " +
            "AND c.board.idx = :boardIdx")
    ChatRoomEntity getRoomByUserIdAndBoardIdx(@Param("userId") String userId, @Param("boardIdx") Long boardIdx);

}
