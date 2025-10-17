package bitc.full502.sceneshare.domain.entity.dto.omdb;

import java.util.List;

public record OmdbMovie(
    String Title,
    String Year,
    String Rated,
    String Released,
    String Runtime,
    String Genre,
    String Director,
    String Writer,
    String Actors,
    String Plot,
    String Language,
    String Country,
    String Awards,
    String Poster,
    List<OmdbRatings> Ratings,
    String Metascore,
    String imdbRating,
    String imdbVotes,
    String imdbID,
    String Type,
    String DVD,
    String BoxOffice,
    String Production,
    String Website,
    String Response,
    String Error
) {
}
