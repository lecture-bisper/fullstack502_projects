package bitc.full502.springproject_team1.DTO;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class BoardHeartDTO {

    private Integer boardHeartIdx;
    private Integer boardIdx;
    private Integer customerId;
    private String boardHeartyn;
}
