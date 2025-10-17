package bitc.full502.sceneshare.domain.entity.dto;

import bitc.full502.sceneshare.domain.entity.user.ReplyEntity;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReplyDTO {

  private Integer replyId;
  private String userId;      // 문자열
  private String contents;
  private LocalDateTime createDate;  // yyyy-MM-dd HH:mm 포맷 문자열
  private boolean mine;
  private String userImg;

  private ReplyDTO toDto(ReplyEntity e, String loginUserId, String userImg) {
    boolean mine = loginUserId != null && loginUserId.equals(e.getUserId());
    return ReplyDTO.builder()
        .replyId(e.getReplyId())
        .userId(e.getUserId())
        .contents(e.getContents())
        .createDate(e.getCreateDate())   // ✅ 여기! format 하지 말고 그대로
        .mine(mine)
        .userImg(userImg)
        .build();
  }
}
