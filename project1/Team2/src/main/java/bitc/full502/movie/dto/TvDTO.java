package bitc.full502.movie.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;

@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class TvDTO extends BaseDTO {

    @JsonProperty("name")
    private String name;

    @JsonProperty("original_name")
    private String originalName;

    @JsonProperty("first_air_date")
    private LocalDate firstAirDate;

    @JsonProperty("number_of_episodes")
    private String numOfEpisodes;
}
