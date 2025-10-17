package bitc.full502.sceneshare.service;

import bitc.full502.sceneshare.common.ImdbUtils;
import bitc.full502.sceneshare.config.OmdbProperties;
import bitc.full502.sceneshare.domain.entity.dto.MainPageMovieVM;
import bitc.full502.sceneshare.domain.entity.dto.omdb.OmdbMovie;
import bitc.full502.sceneshare.domain.entity.dto.omdb.OmdbSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OmdbService {

  private final RestTemplate restTemplate;
  private final OmdbProperties props;

  public OmdbMovie getByImdbId(String imdbId) {
    var uri = UriComponentsBuilder.fromHttpUrl(props.baseUrl())
        .queryParam("i", imdbId)
        .queryParam("apikey", props.apiKey())
        .build(true).toUri();
    return restTemplate.getForObject(uri, OmdbMovie.class);
  }

  public OmdbMovie getByTitle(String title) {
    var uri = UriComponentsBuilder.fromHttpUrl(props.baseUrl())
        .queryParam("t", title)
        .queryParam("apikey", props.apiKey())
        .build(true).toUri();
    return restTemplate.getForObject(uri, OmdbMovie.class);
  }

  public OmdbSearchResponse search(String keyword, int page) {
    var uri = UriComponentsBuilder.fromHttpUrl(props.baseUrl())
        .queryParam("s", keyword)
        .queryParam("page", page)
        .queryParam("apikey", props.apiKey())
        .build(true).toUri();
    return restTemplate.getForObject(uri, OmdbSearchResponse.class);
  }

  public MainPageMovieVM toMainVM(OmdbMovie m) {
    if (m == null || !"True".equalsIgnoreCase(m.Response())) return null;

    int movieId = ImdbUtils.parseImdbIdToInt(m.imdbID());  // ✅ int

    String poster = (m.Poster() == null || "N/A".equalsIgnoreCase(m.Poster()))
        ? "/img/no-image.svg" : m.Poster();

    return MainPageMovieVM.builder()
        .movieId(movieId)                              // ✅ int
        .movieTitle(m.Title())
        .moviePosterUrl(poster)
        .movieRatingAvg(m.imdbRating())
        .bookmarkCnt(0)
        .build();
  }

  /** 샘플로 2~3편 가져오기 */
  public List<MainPageMovieVM> sampleMainList() {
    var a = toMainVM(getByImdbId("tt3896198")); // Guardians Vol.2
    var b = toMainVM(getByTitle("Inception"));
    return List.of(a, b);
  }

  // ✅ 1) 검색한 결과(최대 10개)를 상세조회까지 해서 뷰모델로 변환
  public List<MainPageMovieVM> getTenForMain(String keyword) {
    var res = search(keyword, 1); // OMDb는 page=1 당 최대 10개
    if (res == null || !"True".equalsIgnoreCase(res.Response()) || res.Search() == null) {
      return List.of();
    }
    return res.Search().stream()
        .limit(10)
        .map(it -> getByImdbId(it.imdbID()))
        .map(this::toMainVM)
        .filter(Objects::nonNull)
        .toList();
  }
}
