package bitc.full502.sceneshare.controller.user;

import bitc.full502.sceneshare.domain.entity.dto.MovieInfoDTO;
import bitc.full502.sceneshare.domain.entity.user.BoardEntity;
import bitc.full502.sceneshare.service.OmdbService;
import bitc.full502.sceneshare.service.user.BoardService;
import bitc.full502.sceneshare.service.user.MainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;
import bitc.full502.sceneshare.domain.entity.user.MovieEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.ui.Model;

@Controller
@RequiredArgsConstructor
public class UserMainController {

  private final MainService mainService;
  private final OmdbService omdbService;
  private final BoardService boardService;

  /**
   * ✅ 메서드명 변경: mainPage → showMainPage (main 이름 혼동 방지)
   * ✅ OMDb 데이터를 폴백/보강 용도로 주입
   * ✅ /main 매핑은 이 메서드 '하나만' 존재해야 함
   */
  @GetMapping("/main")
  public ModelAndView showMainPage() throws Exception {  // ✅ 메서드명도 명확하게
    ModelAndView mv = new ModelAndView("user/main");

    // 1) 기존 DB 데이터
    List<MovieInfoDTO> movieListBookmarkCnt = mainService.selectBoardListByBookmarkCnt();
    if (movieListBookmarkCnt.size() > 15) {
      movieListBookmarkCnt = movieListBookmarkCnt.subList(0, 15);
    }

    List<MovieInfoDTO> movieListReleaseDate = mainService.selectBoardListByReleaseDate();
    if (movieListReleaseDate.size() > 15) {
      movieListReleaseDate = movieListReleaseDate.subList(0, 15);
    }

    // 2) OMDb 10개 (섹션별 키워드를 다르게 줄 수도 있음)
    var omdbTop10Popular  = omdbService.getTenForMain("marvel"); // ✅ 가장 많이 찾는 영화(예시 키워드)
    var omdbTop10Latest   = omdbService.getTenForMain("2023");   // ✅ 최신 영화(예시 키워드)

    // 3) 주입 로직: DB가 비었으면 OMDb 10개로 대체 (원하면 항상 OMDb로 덮어써도 됨)
    if (movieListBookmarkCnt == null || movieListBookmarkCnt.isEmpty()) {
      mv.addObject("movieListBookmarkCnt", omdbTop10Popular);     // ✅
    } else {
      mv.addObject("movieListBookmarkCnt", movieListBookmarkCnt);
    }

    if (movieListReleaseDate == null || movieListReleaseDate.isEmpty()) {
      mv.addObject("movieListReleaseDate", omdbTop10Latest);      // ✅
    } else {
      mv.addObject("movieListReleaseDate", movieListReleaseDate);
    }

    // 4) 리뷰 목록
    List<BoardEntity> boardList = mainService.selectBoardList();
//    mv.addObject("boardList", boardList);

    mv.addObject("boardList", boardService.getLatestReviewCards(3)); // 최신 3개

    return mv;
  }

  @GetMapping("/main/movieListBookmarkCnt")
  public ModelAndView movieListBookmarkCnt() throws Exception {
    ModelAndView mv = new ModelAndView("/user/sub/movieListBookmarkCnt");
    List<MovieInfoDTO> movieListBookmarkCnt = mainService.selectBoardListByBookmarkCnt();
    mv.addObject("movieListBookmarkCnt", movieListBookmarkCnt);
    return mv;
  }

  @GetMapping("/main/movieListReleaseDate")
  public ModelAndView movieListReleaseDate() throws Exception {
    ModelAndView mv = new ModelAndView("/user/sub/movieListReleaseDate");
    List<MovieInfoDTO> movieListReleaseDate = mainService.selectBoardListByReleaseDate();
    mv.addObject("movieListReleaseDate", movieListReleaseDate);
    return mv;
  }

  @GetMapping("/main/boardList")
  public ModelAndView boardList() {
    ModelAndView mv = new ModelAndView("/user/board/boardList");
    mv.addObject("boardList", boardService.findAllWithReplyCount());
    return mv;
  }

  @GetMapping("/main/search")
  public ModelAndView movieSearchResult(@RequestParam("searchMovie") String searchMovie) throws Exception {
    ModelAndView mv = new ModelAndView("/user/sub/movieSearchResult");
    Map<String, List<MovieEntity>> movie = mainService.movieSearchList(searchMovie);
    mv.addObject("movie", movie);
    mv.addObject("searchMovie", searchMovie);
    return mv;
  }

  @ResponseBody
  @GetMapping("/main/searchResult")
  public ModelAndView searchMovie(@RequestParam("searchMovie") String searchMovie) throws Exception {
    ModelAndView mv = new ModelAndView("/user/sub/movieSearchResult");
    Map<String, List<MovieEntity>> movieResultList = mainService.movieSearchList(searchMovie);
    mv.addObject("movieResultList", movieResultList);
    return mv;
  }

}
