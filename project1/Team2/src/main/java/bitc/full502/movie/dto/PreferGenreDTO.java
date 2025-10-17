package bitc.full502.movie.dto;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString

public class PreferGenreDTO {
    private String type; // "movie" 또는 "tv"
    private List<Integer> genreIds;
}
