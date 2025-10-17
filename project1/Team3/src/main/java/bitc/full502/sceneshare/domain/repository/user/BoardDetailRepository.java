package bitc.full502.sceneshare.domain.repository.user;

import bitc.full502.sceneshare.domain.entity.dto.LatestReviewCardView;
import bitc.full502.sceneshare.domain.entity.user.BoardEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BoardDetailRepository extends JpaRepository<BoardEntity, Integer> {

  BoardEntity findByBoardId(int boardId) throws Exception;

  @Query("select be.movieId, count(be.boardId) from BoardEntity as be group by be.movieId order by count(be.boardId) desc")
  Object[] countByBoardId() throws Exception;

  List<BoardEntity> findByMovieIdOrderByCreateDateDesc(Integer movieId);

  // 최신 4개
  List<BoardEntity> findTop4ByMovieIdOrderByCreateDateDesc(int movieId);

  // ✅ 전체 개수
  long countByMovieId(int movieId);

  @Query("""
      select
        b.boardId                               as boardId,
        b.movieId                               as movieId,
        coalesce(m.movieTitle, '(제목 없음)')    as movieTitle,
        coalesce(m.posterUrl, m.posterUrl) as posterUrl,
        b.userId                                as userId,
        b.userImg                               as userImg,
        b.rating                                as rating,
        b.contents                              as contents,
        b.createDate                            as createDate,
        (select count(r.replyId) from ReplyEntity r where r.boardId = b.boardId) as commentCount
      from BoardEntity b
        left join MovieEntity m on m.movieId = b.movieId
      order by b.createDate desc
    """)
  List<LatestReviewCardView> findLatestCards(Pageable pageable);

  List<BoardEntity> findAllByOrderByCreateDateDesc();
}
