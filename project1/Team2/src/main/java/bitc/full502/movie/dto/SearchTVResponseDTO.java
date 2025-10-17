package bitc.full502.movie.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class SearchTVResponseDTO {

    private int page;

    @JsonProperty("results")
    private List<TvDTO> results;

    @JsonProperty("total_pages")
    private int totalPages;

    @JsonProperty("total_results")
    private int totalResults;
}
