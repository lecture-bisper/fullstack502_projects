package bitc.full502.springproject_team1.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@Data
public class ReviewDTO {

    private Integer ReviewIdx;
    private Integer OrderIdx;
    private Integer ReviewStar;
    private LocalDateTime ReviewDate;
    private String ReviewPhoto;
    private Integer ProductId;
    private String reviewContent;
}
