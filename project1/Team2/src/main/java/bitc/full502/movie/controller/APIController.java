package bitc.full502.movie.controller;

import bitc.full502.movie.dto.MovieDTO;
import bitc.full502.movie.dto.TvDTO;
import bitc.full502.movie.service.APIService;
import bitc.full502.movie.service.JPAService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class APIController {

    private final JPAService jpaService;
    private final APIService apiService;

    @GetMapping("/movie")
    public List<MovieDTO> getMovieSlider(@RequestParam("type") String type) throws Exception {

        switch (type) {
            case "popMovie":
                return apiService.getContentList(MovieDTO.class, apiService.createMovieListUrl("popularity", "1"));
        }

        return null;
    }

    @GetMapping("/tv")
    public List<TvDTO> getTVSlider(@RequestParam("type") String type) throws Exception {

        switch (type) {
            case "popTV":
                return apiService.getContentList(TvDTO.class, apiService.createTVListUrl("popularity", "1"));
        }

        return null;
    }
}
