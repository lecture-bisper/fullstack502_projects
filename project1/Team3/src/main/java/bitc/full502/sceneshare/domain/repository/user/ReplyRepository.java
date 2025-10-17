package bitc.full502.sceneshare.domain.repository.user;

import bitc.full502.sceneshare.domain.entity.user.ReplyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReplyRepository extends JpaRepository<ReplyEntity, Integer> {

  List<ReplyEntity> findByBoardIdOrderByCreateDateDesc(Integer boardId);
  long countByBoardId(Integer boardId);

  // 권한 검증용
  Optional<ReplyEntity> findByReplyIdAndUserId(int replyId, String userId);

  void deleteByBoardId(int boardId);
}
