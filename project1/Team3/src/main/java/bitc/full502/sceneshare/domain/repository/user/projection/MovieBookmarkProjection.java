package bitc.full502.sceneshare.domain.repository.user.projection;

public interface MovieBookmarkProjection {

  Integer getMovieId();
  String  getMovieTitle();
  Long    getBookmarkCnt();
  Double  getRatingAvg();
  String  getPosterUrl();
}
