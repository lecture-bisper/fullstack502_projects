package bitc.full502.sceneshare.service.user;

import bitc.full502.sceneshare.domain.entity.dto.ReplyDTO;

import java.util.List;

public interface ReplyService {

  /** 목록 조회 (mine 계산을 위해 로그인 사용자 아이디를 함께 전달) */
  List<ReplyDTO> list(int boardId, String loginUserId);

  /** 댓글 개수 */
  long count(int boardId);

  /** 생성 */
  void create(int boardId, String loginUserId, String contents);

  /** 수정 (권한 체크 포함) */
  ReplyDTO update(int replyId, String loginUserId, String contents);

  /** 삭제 (권한 체크 포함) */
  void delete(int replyId, String loginUserId);
}
