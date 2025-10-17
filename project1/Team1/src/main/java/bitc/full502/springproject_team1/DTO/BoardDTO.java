package bitc.full502.springproject_team1.DTO;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class BoardDTO {

    private Integer boardIdx;
    private Integer customerId;
    private String boardPost;
    private int boardHeartCount;
    private int boardReplyCount;
    private String boardUploadPhoto;
}
