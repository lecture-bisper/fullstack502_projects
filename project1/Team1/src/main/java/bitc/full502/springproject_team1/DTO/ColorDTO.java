package bitc.full502.springproject_team1.DTO;

import jakarta.persistence.Column;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class ColorDTO {

    private Integer colorIdx;
    private String colorName;
}
