package bitc.full502.sceneshare.service.user.mapper;

import bitc.full502.sceneshare.common.OmdbMapUtils;
import bitc.full502.sceneshare.domain.entity.dto.MovieView;
import bitc.full502.sceneshare.domain.entity.dto.omdb.OmdbMovie;
import bitc.full502.sceneshare.domain.entity.user.MovieEntity;

public class MovieViewMapper {

  // DB Entity -> View
  public static MovieView fromEntity(MovieEntity e) {
    if (e == null) return null;

    String poster = (e.getPosterUrl() == null || e.getPosterUrl().isBlank())
        ? "/img/no-image.svg" : e.getPosterUrl();

    MovieView v = new MovieView();
    v.setMovieId(e.getMovieId());
    v.setMovieTitle(e.getMovieTitle());
    v.setMovieActors(e.getMovieActors());
    v.setMovieDirector(e.getMovieDirector());
    v.setMovieGenre(e.getMovieGenre());
    v.setMovieAge(e.getMovieAge());
    v.setMovieCountry(e.getMovieCountry());
    v.setMovieTime(e.getMovieTime());
    v.setReleaseDate(e.getReleaseDate());

    v.setMovieDescription(e.getMovieDescription());
    v.setMoviePlot(e.getMovieDescription()); // 템플릿 호환

    v.setPosterUrl(poster);
    v.setMoviePosterUrl(poster);
    v.setSubTopImgUrl(e.getSubTopImgUrl() == null ? poster : e.getSubTopImgUrl());

    v.setRatingAvg(e.getRatingAvg());
    v.setMovieRatingAvg(e.getRatingAvg());
    v.setBookmarkCnt(null); // 필요하면 채우기

    return v;
  }

  // OMDb -> View (movieId는 숫자만 넘어온 값)
  public static MovieView fromOmdb(OmdbMovie m, int movieId) {
    if (m == null || !"True".equalsIgnoreCase(m.Response())) return null;

    String poster = OmdbMapUtils.posterOrDefault(m.Poster());

    MovieView v = new MovieView();
    v.setMovieId(movieId);
    v.setMovieTitle(m.Title());
    v.setMovieActors(OmdbMapUtils.cap255(m.Actors()));
    v.setMovieDirector(OmdbMapUtils.cap255(m.Director()));
    v.setMovieGenre(OmdbMapUtils.cap255(m.Genre()));
    v.setMovieAge(OmdbMapUtils.cap255(m.Rated()));
    v.setMovieCountry(OmdbMapUtils.cap255(m.Country()));
    v.setMovieTime(OmdbMapUtils.toMinutesString(m.Runtime()));
    v.setReleaseDate(OmdbMapUtils.toReleasedDateTime(m.Released()));

    v.setMovieDescription(OmdbMapUtils.cap255(m.Plot()));
    v.setMoviePlot(OmdbMapUtils.cap255(m.Plot()));

    v.setPosterUrl(poster);
    v.setMoviePosterUrl(poster);
    v.setSubTopImgUrl(poster);

    Double rating = OmdbMapUtils.toDoubleOrNull(m.imdbRating());
    v.setRatingAvg(rating);
    v.setMovieRatingAvg(rating);
    v.setBookmarkCnt(0L);

    return v;
  }
}
