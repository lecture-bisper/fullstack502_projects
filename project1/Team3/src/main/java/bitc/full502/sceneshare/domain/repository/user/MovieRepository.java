package bitc.full502.sceneshare.domain.repository.user;

import bitc.full502.sceneshare.domain.entity.user.MovieEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<MovieEntity, Integer> {

  // 중복 저장 방지용 (제목 + 개봉일이 동일하면 같은 영화로 간주)
  boolean existsByMovieTitleAndReleaseDate(String movieTitle, LocalDateTime releaseDate);

  Optional<MovieEntity> findByMovieTitleAndReleaseDate(String movieTitle, LocalDateTime releaseDate);

  // 필요 시 확장 메서드 예시들:
  // List<MovieEntity> findTop10ByOrderByRatingAvgDesc();
  // List<MovieEntity> findByMovieTitleContainingIgnoreCase(String keyword);
}
