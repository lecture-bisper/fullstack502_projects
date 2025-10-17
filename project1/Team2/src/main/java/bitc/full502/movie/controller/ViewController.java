package bitc.full502.movie.controller;

import bitc.full502.movie.domain.entity.UserEntity;
import bitc.full502.movie.dto.MediaDTO;
import bitc.full502.movie.dto.MemberDTO;
import bitc.full502.movie.dto.MovieDTO;
import bitc.full502.movie.dto.TvDTO;
import bitc.full502.movie.service.APIService;
import bitc.full502.movie.service.JPAService;
import bitc.full502.movie.service.PreferGenreService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ViewController {

    private final APIService apiService;
    private final JPAService jpaService;
    private final PreferGenreService preferGenreService;

    @GetMapping({"/", "/main"})
    public ModelAndView index(HttpSession session) throws Exception {
        ModelAndView mv = new ModelAndView("index");
        UserEntity user = (UserEntity) session.getAttribute("loginUser");
        String userId = String.valueOf(session.getAttribute("userId"));

        // 메인슬라이더 (movie)
        List<MovieDTO> mainSlider = apiService.getContentList(MovieDTO.class, apiService.createMovieListUrl("popularity", "1"));

        // 인기 콘텐츠
        List<MovieDTO> popularityMovieSlider = apiService.getContentList(MovieDTO.class, apiService.createMovieListUrl("popularity", "1"));
        List<TvDTO> popularityTVSlider = apiService.getContentList(TvDTO.class, apiService.createTVListUrl("popularity", "1"));

        // 최신 콘텐츠
        List<MovieDTO> releaseMovieSlider = apiService.getContentList(MovieDTO.class, apiService.createMovieListUrl("releaseDate", "1"));
        List<TvDTO> releaseTVSlider = apiService.getContentList(TvDTO.class, apiService.createTVListUrl("firstAirDate", "1"));

        // 추천 콘텐츠
        List<MovieDTO> recommendMovieSlider;
        List<TvDTO> recommendTVSlider;

        // 북마크
        List<MediaDTO> favoriteSlider;

        // 로그인 시 반환
        if (user != null) {
            // 추천 콘텐츠 - 로그인 된 유저의 선호장르로 필터링 후 반한
            recommendMovieSlider = apiService.getContentList(MovieDTO.class, apiService.createMovieListUrl
                    (jpaService.getPreferredGenres(user.getId(), "movie"), "1"));
            recommendTVSlider = apiService.getContentList(TvDTO.class, apiService.createTVListUrl
                    (jpaService.getPreferredGenres(user.getId(), "tv"), "1"));

            // 북마크 - 로그인 된 유저의 북마크 반환
            favoriteSlider = apiService.getFavoritesList(user.getId());

        } else {
            // 추천 콘텐츠
            recommendMovieSlider = apiService.getContentList(MovieDTO.class, apiService.createMovieListUrl("recommend", "1"));
            recommendTVSlider = apiService.getContentList(TvDTO.class, apiService.createTVListUrl("recommend", "1"));

            // favoriteSlider = null;
            favoriteSlider = null;
        }

        mv.addObject("mainSlider", mainSlider.subList(0, 9));
        mv.addObject("popularityMovieSlider", popularityMovieSlider);
        mv.addObject("popularityTVSlider", popularityTVSlider);
        mv.addObject("releaseMovieSlider", releaseMovieSlider);
        mv.addObject("releaseTVSlider", releaseTVSlider);
        mv.addObject("recommendMovieSlider", recommendMovieSlider);
        mv.addObject("recommendTVSlider", recommendTVSlider);
        mv.addObject("favoriteSlider", favoriteSlider);

        return mv;
    }

    @GetMapping("/movie/{contentId}")
    public ModelAndView movieDetail(@PathVariable String contentId, HttpSession session) throws Exception {
        ModelAndView mv = new ModelAndView("detail");
        UserEntity user = new UserEntity();
        String convertGenre = "";

        if (session.getAttribute("loginUser") != null) {
            user = (UserEntity) session.getAttribute("loginUser");
        }

        int[] favoritesIds = jpaService.getFavoriteContentsIds(user.getId(), "movie");

        // 반환할 콘텐츠 & 출연진 정보 생성
        MediaDTO content = jpaService.convertMovieToMedia(apiService.getContent(MovieDTO.class, apiService.createUrl("movie", contentId)));
        System.out.println(apiService.createUrl("movie", contentId));
        List<MemberDTO> memberDTOList = apiService.getCastInfo("movie", contentId);
        // 장르 ID 값으로 반환할 문자열 생성
        if (content.getGenreIds() != null) {
            convertGenre = jpaService.convertGenre(content.getGenreIds());
        }

        mv.addObject("type", "movie");
        mv.addObject("content", content);
        mv.addObject("memberList", memberDTOList);
        mv.addObject("convertGenre", convertGenre);
        mv.addObject("user", user);
        mv.addObject("favoritesIds", favoritesIds);
        return mv;
    }

    @GetMapping("/tv/{contentId}")
    public ModelAndView tvDetail(@PathVariable String contentId, HttpSession session) throws Exception {
        ModelAndView mv = new ModelAndView("detail");
        UserEntity user = (UserEntity) session.getAttribute("loginUser");
        String convertGenre = "";

        if (session.getAttribute("loginUser") != null) {
            user = (UserEntity) session.getAttribute("loginUser");
        }

        int[] favoritesIds = jpaService.getFavoriteContentsIds(user.getId(), "tv");

        // 반환할 콘텐츠 & 출연진 정보 생성
        MediaDTO content = jpaService.convertTVToMedia(apiService.getContent(TvDTO.class, apiService.createUrl("tv", contentId)));
        List<MemberDTO> memberDTOList = apiService.getCastInfo("tv", contentId);
        // 장르 ID 값으로 반환할 문자열 생성
        if (content.getGenreIds() != null) {
            convertGenre = jpaService.convertGenre(content.getGenreIds());
        }

        mv.addObject("type", "tv");
        mv.addObject("content", content);
        mv.addObject("memberList", memberDTOList);
        mv.addObject("convertGenre", convertGenre);
        mv.addObject("user", user);
        mv.addObject("favoritesIds", favoritesIds);

        return mv;
    }

    @GetMapping("/profile")
    public ModelAndView profile(HttpSession session) throws Exception {
        ModelAndView mv = new ModelAndView("profile");
        UserEntity user = (UserEntity) session.getAttribute("loginUser");

        List<String> preferGenreMovieList = preferGenreService.getPreferGenre(user.getId(), "movie");
        List<String> preferGenreTvList = preferGenreService.getPreferGenre(user.getId(), "tv");

        // 북마크 리스트 실제로 할당
        List<MediaDTO> favoriteSlider = apiService.getFavoritesList(user.getId());

        mv.addObject("preferGenreMovieList", preferGenreMovieList);
        mv.addObject("preferGenreTvList", preferGenreTvList);
        mv.addObject("user", user);
        mv.addObject("favoriteSlider", favoriteSlider);

        return mv;
    }
}

