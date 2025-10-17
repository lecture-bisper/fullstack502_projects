package bitc.full502.sceneshare.domain.repository.user;

import bitc.full502.sceneshare.domain.entity.dto.LatestReviewCardView;
import bitc.full502.sceneshare.domain.entity.dto.MovieInfoDTO;
import bitc.full502.sceneshare.domain.entity.user.BoardEntity;
import bitc.full502.sceneshare.domain.entity.user.MovieEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.awt.print.Pageable;
import java.util.List;

public interface MainRepository extends JpaRepository<MovieEntity, Integer> {

  @Query("""
    select new bitc.full502.sceneshare.domain.entity.dto.MovieInfoDTO(
        m.movieId,
        m.movieTitle,
        count(b.bookmarkId),
        CAST(m.ratingAvg AS double),
        m.posterUrl
    )
    from MovieEntity m
    left join BookmarkEntity b on b.movie = m
    group by m.movieId, m.movieTitle, m.ratingAvg, m.posterUrl
    order by count(b.bookmarkId) desc
""")
  List<MovieInfoDTO> findAllByBookmarkCntDesc();

  @Query("""
    select new bitc.full502.sceneshare.domain.entity.dto.MovieInfoDTO(
        m.movieId,
        m.movieTitle,
        count(b.bookmarkId),
        CAST(m.ratingAvg AS double),
        m.posterUrl
    )
    from MovieEntity m
    left join BookmarkEntity b on b.movie = m
    group by m.movieId, m.movieTitle, m.ratingAvg, m.posterUrl, m.releaseDate
    order by m.releaseDate desc
""")
  List<MovieInfoDTO> findAllByReleaseDateDesc();


}
