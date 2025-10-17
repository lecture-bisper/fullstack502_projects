package bitc.full502.springproject_team1.DTO;

import jakarta.persistence.Column;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class SizeDTO {

    private Integer sizeIdx;
    private String sizeName;
}
