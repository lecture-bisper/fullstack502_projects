package bitc.full502.sceneshare.controller.user;

import bitc.full502.sceneshare.domain.entity.user.BoardEntity;
import bitc.full502.sceneshare.domain.entity.user.MovieEntity;
import bitc.full502.sceneshare.service.OmdbService;
import bitc.full502.sceneshare.service.user.BoardService;
import bitc.full502.sceneshare.service.user.MovieDetailService;
import bitc.full502.sceneshare.service.user.mapper.MovieViewMapper;
import bitc.full502.sceneshare.service.user.mapper.OmdbToMovieMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import bitc.full502.sceneshare.domain.entity.dto.MovieView;

@Controller
@RequiredArgsConstructor
public class UserMovieDetailController {

  private final MovieDetailService movieDetailService;
  private final BoardService boardService;
  private final OmdbService omdbService;

  /**
   * 영화 상세보기
   * - DB에 영화가 있으면 DB 값을 사용
   * - 없으면 OMDb API로 보충하여 View DTO(MovieView)로 매핑
   * - 항상 boardList(영화별 추천글 목록)를 함께 내려서 화면 하단에 보여준다
   */
  @GetMapping("/movieDetail/{movieId}")
  public ModelAndView movieDetail(@PathVariable("movieId") int movieId) throws Exception {
    ModelAndView mv = new ModelAndView("user/sub/movieDetail");

    Object[] boardCnt  = boardService.boardCnt();
    Object[] ratingCnt = movieDetailService.ratingAvg();

    var entity = movieDetailService.selectMovieDetail(movieId);
    MovieView view;
    if (entity != null) {
      if (entity.getMovieActors() == null || entity.getMovieActors().isBlank()) {
        entity.setMovieActors("조연:정보 없음");
      }
      view = MovieViewMapper.fromEntity(entity);
    } else {
      var omdb = omdbService.getByImdbId("tt" + movieId);
      if (omdb == null || !"True".equalsIgnoreCase(omdb.Response())) {
        String padded = String.format("%07d", movieId);
        omdb = omdbService.getByImdbId("tt" + padded);
      }
      if (omdb == null || !"True".equalsIgnoreCase(omdb.Response())) {
        mv.setViewName("error/404");
        mv.addObject("message", "영화 정보를 찾을 수 없습니다.");
        return mv;
      }
      view = MovieViewMapper.fromOmdb(omdb, movieId);
      if (view.getMovieActors() == null || view.getMovieActors().isBlank()) {
        view.setMovieActors("조연:정보 없음");
      }
    }

    mv.addObject("movie", view);
    mv.addObject("board", boardCnt);
    mv.addObject("rating", ratingCnt);

    // ✅ 최신글 4개만
    mv.addObject("boardList", boardService.findTop4ByMovie(movieId));

    // ✅ 전체 개수
    mv.addObject("boardTotal", boardService.countBoardsByMovie(movieId));

    return mv;
  }

  @PostMapping("/movieDetail/{movieId}")
  public String boardWrite(
      @PathVariable("movieId") int movieId,
      @RequestParam(value = "title", required = false) String title,
      @RequestParam("rating") double rating,
      @RequestParam("contents") String contents,
      HttpServletRequest req
  ) throws Exception {

    HttpSession session = req.getSession(false);
    String userId = (session != null) ? (String) session.getAttribute("userId") : null;

    var movie = movieDetailService.selectMovieDetail(movieId);

    if (title == null || title.isBlank()) {
      String movieTitle = (movie != null && movie.getMovieTitle() != null) ? movie.getMovieTitle() : ("#" + movieId);
      title = "[추천] " + movieTitle;
    }

    BoardEntity board = new BoardEntity();
    board.setTitle(title);
    board.setContents(contents);
    board.setRating(rating);
    board.setMovieId(movieId);
    if (userId != null) board.setUserId(userId);
    if (movie != null && movie.getMovieGenre() != null) {
      board.setGenre(movie.getMovieGenre());
    }

    // ✅ 저장 (반환값은 필요 시 사용)
    boardService.boardWrite(board, movieId);

    // ✅ 같은 페이지로 돌아가기 (리뷰 영역으로 스크롤하고 싶으면 #reviews)
    return "redirect:/movieDetail/" + movieId + "#reviews";
  }

}
