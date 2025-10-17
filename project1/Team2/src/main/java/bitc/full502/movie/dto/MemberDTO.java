package bitc.full502.movie.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class MemberDTO {

    private String name;

    private String gender;

    private String originalName;

    private String profilePath;

    private String character;

    private Integer order;

}
