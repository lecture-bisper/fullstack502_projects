package bitc.full502.movie.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;


@Data
@ToString
public class BaseDTO {

    @JsonProperty("id")
    private int id;

    @JsonProperty("overview")
    private String overview;

    @JsonProperty("poster_path")
    private String posterPath;

    @JsonProperty("backdrop_path")
    private String backdropPath;

    @JsonProperty("popularity")
    private Double popularity;

    @JsonProperty("vote_average")
    private Double voteAverage;

    @JsonProperty("vote_count")
    private Integer voteCount;

    @JsonIgnore
    private String genreIds;

    @JsonProperty("genre_ids")
    private List<Integer> genreIdList;

    @JsonProperty("original_language")
    private String originalLanguage;

    @JsonIgnore
    private String originCountry;

    @JsonProperty("origin_country")
    private List<String> originCountryList;
}
