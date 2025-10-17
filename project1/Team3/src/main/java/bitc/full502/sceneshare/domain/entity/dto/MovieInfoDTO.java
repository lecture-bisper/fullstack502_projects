package bitc.full502.sceneshare.domain.entity.dto;

public class MovieInfoDTO {

  public Integer movieId;
  public String  movieTitle;
  public Long    bookmarkCnt;
  public Double  movieRatingAvg;
  public String  moviePosterUrl;

  public MovieInfoDTO(Integer movieId,
                      String  movieTitle,
                      Long    bookmarkCnt,
                      Double  movieRatingAvg,
                      String  moviePosterUrl) {
    this.movieId        = movieId;
    this.movieTitle     = movieTitle;
    this.bookmarkCnt    = bookmarkCnt;
    this.movieRatingAvg = movieRatingAvg;
    this.moviePosterUrl = moviePosterUrl;
  }

  public Integer getMovieId()                 { return movieId; }
  public void    setMovieId(Integer movieId)  { this.movieId = movieId; }

  public String  getMovieTitle()              { return movieTitle; }
  public void    setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

  public Long    getBookmarkCnt()             { return bookmarkCnt; }
  public void    setBookmarkCnt(Long bookmarkCnt) { this.bookmarkCnt = bookmarkCnt; }

  public Double  getMovieRatingAvg()          { return movieRatingAvg; }
  public void    setMovieRatingAvg(Double movieRatingAvg) { this.movieRatingAvg = movieRatingAvg; }

  public String  getMoviePosterUrl()          { return moviePosterUrl; }
  public void    setMoviePosterUrl(String moviePosterUrl) { this.moviePosterUrl = moviePosterUrl; }
}
