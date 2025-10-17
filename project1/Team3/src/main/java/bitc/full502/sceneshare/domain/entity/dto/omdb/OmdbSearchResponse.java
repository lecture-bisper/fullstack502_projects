package bitc.full502.sceneshare.domain.entity.dto.omdb;

import java.util.List;

public record OmdbSearchResponse(List<OmdbSearchItem> Search, String totalResults, String Response, String Error) {
}
