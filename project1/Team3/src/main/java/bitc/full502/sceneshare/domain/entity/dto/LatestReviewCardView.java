package bitc.full502.sceneshare.domain.entity.dto;

import java.time.LocalDateTime;

public interface LatestReviewCardView {

  Integer getBoardId();
  Integer getMovieId();
  String  getMovieTitle();                  // 영화 제목
  String  getPosterUrl();                   // COALESCE(m.posterUrl, m.moviePosterUrl)
  String  getUserId();
  String  getUserImg();
  Double  getRating();
  String  getContents();
  LocalDateTime getCreateDate();
  long    getCommentCount();
}
