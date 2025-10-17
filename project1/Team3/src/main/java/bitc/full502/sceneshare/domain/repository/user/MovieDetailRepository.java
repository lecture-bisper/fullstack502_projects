package bitc.full502.sceneshare.domain.repository.user;

import bitc.full502.sceneshare.domain.entity.user.MovieEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MovieDetailRepository extends JpaRepository<MovieEntity, Integer> {

  MovieEntity findByMovieId(@Param("movieId") int movieId) throws Exception;

  @Query("select be.movieId, avg(be.rating) from BoardEntity as be group by be.movieId")
  Object[] getRatingAvg() throws Exception;
}
