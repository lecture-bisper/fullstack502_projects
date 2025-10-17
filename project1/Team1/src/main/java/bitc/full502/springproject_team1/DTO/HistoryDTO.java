package bitc.full502.springproject_team1.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class HistoryDTO {

    private Integer historyIdx;
    private Integer productIdx;
    private Integer customerId;
    private String historyDate;
}
