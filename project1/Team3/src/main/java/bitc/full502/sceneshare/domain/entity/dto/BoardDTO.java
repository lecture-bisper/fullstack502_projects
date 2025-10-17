package bitc.full502.sceneshare.domain.entity.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BoardDTO {

  private Integer boardId;
  private String  contents;
  private String  userId;
  private LocalDateTime createDate;
  private long commentCount;
}
