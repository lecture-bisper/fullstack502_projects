package bitc.full502.sceneshare.service.user;

import bitc.full502.sceneshare.domain.entity.user.MovieEntity;
import bitc.full502.sceneshare.domain.repository.user.MovieDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

@Service
@RequiredArgsConstructor
public class MovieDetailServiceImpl implements MovieDetailService {

  private final MovieDetailRepository movieDetailRepository;

  @Override
  public MovieEntity selectMovieDetail(@PathVariable("movieId") int movieId) throws Exception {
    return movieDetailRepository.findByMovieId(movieId);
  }

  @Override
  public Object[] ratingAvg() throws Exception {
    Object[] ratingAvg = movieDetailRepository.getRatingAvg();
    return ratingAvg;
  }
}
