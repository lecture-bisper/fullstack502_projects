package bitc.full502.sceneshare.service.user;

import bitc.full502.sceneshare.domain.entity.user.MovieEntity;
import org.springframework.web.bind.annotation.PathVariable;

public interface MovieDetailService {

  MovieEntity selectMovieDetail(@PathVariable("movieId") int movieId) throws Exception;

  Object[] ratingAvg() throws Exception;
}
