package bitc.full502.movie.controller;

import bitc.full502.movie.dto.MediaDTO;
import bitc.full502.movie.dto.MovieDTO;
import bitc.full502.movie.dto.SearchDTO;
import bitc.full502.movie.dto.TvDTO;
import bitc.full502.movie.service.APIService;
import bitc.full502.movie.service.JPAService;
import bitc.full502.movie.service.SearchService;
import bitc.full502.movie.util.MediaFilter;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.awt.*;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;
    private final JPAService jpaService;
    private final APIService apiService;

    // 헤더 검색창에서 타입 + 키워드만 받아서 페이지 이동용 (뷰 반환)
    @GetMapping("/search/{type}")
    public ModelAndView searchContents(@PathVariable String type) throws Exception {
        ModelAndView mv = new ModelAndView("search");
        List<MediaDTO> contentsList;

        if (type.equals("movie")) {
            contentsList = jpaService.convertMovieToMedia
                    (apiService.getContentList(MovieDTO.class, apiService.createMovieListUrl("popularity", "1")));
        } else {
            contentsList = jpaService.convertTVToMedia
                    (apiService.getContentList(TvDTO.class, apiService.createTVListUrl("popularity", "1")));
        }

        mv.addObject("contentsList", contentsList);
        return mv;
    }

    @GetMapping("/searchDefault/{type}/{pageNum}")
    @ResponseBody
    public List<MediaDTO> searchDefaultContents(@PathVariable String type, @PathVariable String pageNum) throws Exception {

        List<MediaDTO> contentsList;
        if (type.equals("movie")) {
            contentsList = jpaService.convertMovieToMedia
                    (apiService.getContentList(MovieDTO.class, apiService.createMovieListUrl("releaseDate", pageNum)));
        } else {
            contentsList = jpaService.convertTVToMedia
                    (apiService.getContentList(TvDTO.class, apiService.createTVListUrl("firstAirDate", pageNum)));
        }

        return contentsList;
    }


    // AJAX로 필터 + 페이징 등 상세 조건 받아서 JSON 반환
    @GetMapping("/detailSearch/{type}/{pageNum}")
    @ResponseBody
    public List<MediaDTO> searchPage(@ModelAttribute SearchDTO searchDTO,
                                     @PathVariable String type, @PathVariable String pageNum) throws Exception {
        List<MediaDTO> contentsList;

        if (searchDTO.getKeyword() == null) searchDTO.setKeyword("");
        searchDTO.setType(type);
        searchDTO.setPageNum(pageNum);

        if (type.equals("movie")) {
            searchDTO.setCountry("");
            contentsList = jpaService.convertMovieToMedia(searchService.searchFilteredAllMovies(searchDTO));
        } else {
            contentsList = jpaService.convertTVToMedia(searchService.searchFilteredAllTvs(searchDTO));
        }

        return MediaFilter.filterMedia(contentsList, searchDTO);
    }

    @GetMapping("/mainSearch")
    @ResponseBody
    public String mainSearch(HttpSession session, @RequestParam String keyword) throws Exception {
        session.setAttribute("keyword", keyword);
        return "";
    }

    @GetMapping("/deleteSearchKeyword")
    @ResponseBody
    public String deleteSearchKeyword(HttpSession session) throws Exception {
        session.removeAttribute("keyword");
        return "";
    }
}
