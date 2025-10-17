package bitc.full502.sceneshare.service.user.mapper;

import bitc.full502.sceneshare.common.OmdbMapUtils;
import bitc.full502.sceneshare.domain.entity.dto.omdb.OmdbMovie;
import bitc.full502.sceneshare.domain.entity.user.MovieEntity;

public class OmdbToMovieMapper {

  /** OMDb 응답을 DB MovieEntity로 변환 (ID는 자동증가 컬럼이므로 세팅 X) */
  public static MovieEntity toEntity(OmdbMovie m) {
    if (m == null || !"True".equalsIgnoreCase(m.Response())) return null;

    String poster = OmdbMapUtils.posterOrDefault(m.Poster());

    MovieEntity e = new MovieEntity();
    // e.setMovieId(…); // AI PK - 세팅하지 않음

    e.setRatingAvg(OmdbMapUtils.toDoubleOrNull(m.imdbRating()));
    e.setReleaseDate(OmdbMapUtils.toReleasedDateTime(m.Released()));
    e.setMovieActors(OmdbMapUtils.cap255(m.Actors()));
    e.setMovieAge(OmdbMapUtils.cap255(m.Rated()));
    e.setMovieCountry(OmdbMapUtils.cap255(m.Country()));
    e.setMovieDescription(OmdbMapUtils.cap255(m.Plot()));
    e.setMovieDirector(OmdbMapUtils.cap255(m.Director()));

    // ✅ Genre: null 체크 + 소문자 변환 적용
    String genre = OmdbMapUtils.cap255(m.Genre());
    if (genre != null) {
      genre = genre.toLowerCase(); // ✅ "Action, Drama" → "action, drama"
    }
    e.setMovieGenre(genre);

    e.setMovieTime(OmdbMapUtils.toMinutesString(m.Runtime()));
    e.setMovieTitle(OmdbMapUtils.cap255(m.Title()));
    e.setPosterUrl(poster);
    e.setSubTopImgUrl(poster);

    return e;
  }
}
