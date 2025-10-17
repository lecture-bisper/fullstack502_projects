package bitc.full502.movie.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;

@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class MediaDTO extends BaseDTO {

    private int id;

    private String type;

    private String overview;

    private String posterPath;

    private String backdropPath;

    private Double popularity;

    private Double voteAverage;

    private Integer voteCount;

    private String genreIds;

    private String originalLanguage;

    private String title;

    private String originalTitle;

    private LocalDate releaseDate;

    private Integer runtime;

    private String originCountry;

    // 영화 전용 변수
    private Boolean adult = false;

    private Boolean video = false;

    // TV 전용 변수
    private String numOfEpisodes;
}
