package bitc.full502.movie.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;

@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class MovieDTO extends BaseDTO {

    @JsonProperty("title")
    private String title;

    @JsonProperty("original_title")
    private String originalTitle;

    @JsonProperty("release_date")
    private LocalDate releaseDate;

    @JsonProperty("video")
    private Boolean video;

    @JsonProperty("adult")
    private Boolean adult;

    @JsonProperty("runtime")
    private Integer runtime;
}
