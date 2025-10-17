package bitc.full502.springproject_team1.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class BoardCommentDTO {

    private Integer boardCommIdx;
    private Integer customerId;
    private String boardComment;
    private Integer boardIdx;
}
