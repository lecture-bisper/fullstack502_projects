package bitc.full502.sceneshare.service.user;

import bitc.full502.sceneshare.domain.entity.dto.LatestReviewCardView;
import bitc.full502.sceneshare.domain.entity.user.BoardEntity;
import bitc.full502.sceneshare.domain.repository.user.BoardDetailRepository;
import bitc.full502.sceneshare.domain.repository.user.ReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardServiceImpl implements BoardService {

  private final BoardDetailRepository boardDetailRepository;
  private final ReplyRepository replyRepository;

  @Override
  public BoardEntity selectBoardDetail(int boardId) throws Exception {

    BoardEntity board = boardDetailRepository.findByBoardId(boardId);
    return board;
  }

  @Override
  public BoardEntity boardWrite(BoardEntity board, int movieId) throws Exception {

    // 🎯 movie 정보 조회
    var movie = boardDetailRepository.findByBoardId(movieId); // 👈 movieDetailService로 바꿔도 됨
//
    // ✅ genre 설정
    if (movie != null && movie.getGenre() != null) {
      board.setGenre(movie.getGenre()); // 이미 소문자로 저장된 상태라면 그대로 OK
    }

    board.setUpdateDate(java.time.LocalDateTime.now());

    return boardDetailRepository.save(board);
  }

  @Override
  public Object[] boardCnt() throws Exception{
    Object[] board = boardDetailRepository.countByBoardId();
    return board;
  }

  @Override
  public void write(Integer movieId, String userId,
                    String title, String contents, Double rating, String genre) {

    BoardEntity board = new BoardEntity();
    board.setUserId(userId);
    board.setMovieId(movieId);
    board.setTitle(title);
    board.setContents(contents);
    board.setRating(rating);
    board.setGenre(genre);
    board.setCreateDate(LocalDateTime.now());
    board.setUpdateDate(LocalDateTime.now());

    boardDetailRepository.save(board);
  }

  @Override
  public List<BoardEntity> findByMovieId(int movieId) {
    return boardDetailRepository.findByMovieIdOrderByCreateDateDesc(movieId);
  }

  @Override
  public List<BoardEntity> findTop4ByMovie(int movieId) {
    return boardDetailRepository.findTop4ByMovieIdOrderByCreateDateDesc(movieId);
  }

  @Override
  public List<BoardEntity> findAllWithReplyCount() {
    List<BoardEntity> list = boardDetailRepository.findAllByOrderByCreateDateDesc();
    list.forEach(b ->
        b.setReplyCount(replyRepository.countByBoardId(b.getBoardId()))
    );
    return list;
  }

  @Override
  public long countBoardsByMovie(int movieId) {
    return boardDetailRepository.countByMovieId(movieId);
  }

  @Override
  public List<LatestReviewCardView> getLatestReviewCards(int limit) {
    return boardDetailRepository.findLatestCards(PageRequest.of(0, limit));
    // 정렬을 메서드에서 주고 싶으면:
    // return boardDetailRepository.findLatestCards(PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createDate")));
  }

  @Override
  public void update(int boardId, String loginUserId, String contents, double rating) {
    validateContents(contents);
    double norm = normalizeRating(rating);

    BoardEntity b = boardDetailRepository.findById(boardId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    // 작성자만 수정 가능
    if (!loginUserId.equals(b.getUserId())) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "수정 권한이 없습니다.");
    }

    b.setContents(contents);
    b.setRating(norm);                 // 0.0 ~ 5.0, 0.5 단위로 반올림
    // JPA dirty checking으로 자동 flush (@PreUpdate 있으면 updateDate도 자동)
  }

  private void validateContents(String contents) {
    if (contents == null || contents.isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "내용을 입력하세요.");
    }
    if (contents.length() > 10000) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "10000자 이내로 입력하세요.");
    }
  }

  private double normalizeRating(double r) {
    if (Double.isNaN(r)) return 0.0;
    if (r < 0) r = 0;
    if (r > 5) r = 5;
    return Math.round(r * 2) / 2.0; // 0.5 단위로 반올림
  }

  @Override
  @Transactional
  public void delete(int boardId, String loginUserId) {
    BoardEntity b = boardDetailRepository.findById(boardId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    if (!loginUserId.equals(b.getUserId())) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "삭제 권한이 없습니다.");
    }
    replyRepository.deleteByBoardId(boardId); // FK 상황에 맞게
    boardDetailRepository.delete(b);
  }
}
