package bitc.full502.sceneshare.controller.user;

import bitc.full502.sceneshare.domain.entity.dto.MovieView;
import bitc.full502.sceneshare.domain.entity.user.BoardEntity;
import bitc.full502.sceneshare.domain.entity.user.MovieEntity;
import bitc.full502.sceneshare.service.OmdbService;
import bitc.full502.sceneshare.service.user.BoardService;
import bitc.full502.sceneshare.service.user.MovieDetailService;
import bitc.full502.sceneshare.domain.repository.user.ReplyRepository; // ✅ 추가
import bitc.full502.sceneshare.service.user.ReplyService;
import bitc.full502.sceneshare.service.user.mapper.MovieViewMapper;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
public class UserBoardController {

  private final BoardService boardService;
  private final MovieDetailService movieDetailService; // ✅ 영화정보 조회
  private final OmdbService omdbService;               // ✅ 폴백
  private final ReplyRepository replyRepository;       // ✅ 댓글 수
  private final ReplyService replyService;

  @GetMapping("/user/boardDetail/{boardId}")
  public ModelAndView boardDetail(@PathVariable("boardId") int boardId, HttpSession session) throws Exception {
    ModelAndView mv = new ModelAndView("user/board/boardDetail");

    // 1) 게시글
    BoardEntity board = boardService.selectBoardDetail(boardId);
    if (board == null) {
      mv.setViewName("error/404");
      mv.addObject("message", "게시글을 찾을 수 없습니다.");
      return mv;
    }

    // 2) 영화 정보: DB 우선, 없으면 OMDb 폴백 → MovieView로 통일
    MovieView movieView;
    MovieEntity entity = movieDetailService.selectMovieDetail(board.getMovieId());
    if (entity != null) {
      if (entity.getMovieActors() == null || entity.getMovieActors().isBlank()) {
        entity.setMovieActors("조연:정보 없음");
      }
      movieView = MovieViewMapper.fromEntity(entity);
    } else {
      var omdb = omdbService.getByImdbId("tt" + board.getMovieId());
      if (omdb == null || !"True".equalsIgnoreCase(omdb.Response())) {
        String padded = String.format("%07d", board.getMovieId());
        omdb = omdbService.getByImdbId("tt" + padded);
      }
      if (omdb != null && "True".equalsIgnoreCase(omdb.Response())) {
        movieView = MovieViewMapper.fromOmdb(omdb, board.getMovieId());
        if (movieView.getMovieActors() == null || movieView.getMovieActors().isBlank()) {
          movieView.setMovieActors("조연:정보 없음");
        }
      } else {
        // 최후 안전값
        movieView = new MovieView();
        movieView.setMovieId(board.getMovieId());
        movieView.setMovieTitle("(제목 없음)");
      }
    }

    long replyCount = replyRepository.countByBoardId(boardId);

    mv.addObject("board", board);
    mv.addObject("movie", movieView);   // ✅ 템플릿에서 사용
    mv.addObject("replyCount", replyCount);

    mv.addObject("replyCount", replyCount);
    mv.addObject("replies", replyService.list(boardId, (String) session.getAttribute("userId")));

    // 로그인 사용자 id (버튼 노출 제어용)
    mv.addObject("loginUserId", (String) session.getAttribute("userId"));


    return mv;
  }

  // jin 추가
  @GetMapping("/user/noticeDetail.do")
  public String myUpdate() throws Exception {
    return "/user/board/noticeDetail";
  }
}
