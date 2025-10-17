package bitc.full502.movie.service;

import bitc.full502.movie.dto.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {

    @Value("${movie.service.url}")
    private String mainUrl;

    @Value("${movie.service.userKey}")
    private String userKey;

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate;

    public SearchServiceImpl() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public List<MovieDTO> searchMovies(String keyword, String pageNum) throws Exception {
        String url = mainUrl + "discover/movie" + userKey + "&language=ko-KR" + "&query=" + keyword + "&page=" + pageNum;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("accept", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                System.out.println("API 호출 실패 - 상태 코드: " + response.code());
                String errorBody = response.body() != null ? response.body().string() : "No response body";
                System.out.println("응답 내용: " + errorBody);
                throw new RuntimeException("영화 API 호출 실패");
            }

            String json = response.body().string();
            Map<String, Object> map = objectMapper.readValue(json, Map.class);
            List<Map<String, Object>> results = (List<Map<String, Object>>) map.get("results");

            List<MovieDTO> movies = new ArrayList<>();
            for (Map<String, Object> item : results) {
                MovieDTO dto = new MovieDTO();
                dto.setId(getInteger(item.get("id")));
                dto.setTitle((String) item.get("title"));
                dto.setOriginalTitle((String) item.get("original_title"));
                dto.setOverview((String) item.get("overview"));
                dto.setPosterPath((String) item.get("poster_path"));
                dto.setBackdropPath((String) item.get("backdrop_path"));
                dto.setPopularity(getDouble(item.get("popularity")));
                dto.setVoteAverage(getDouble(item.get("vote_average")));
                dto.setVoteCount(getInteger(item.get("vote_count")));
                dto.setGenreIds(item.get("genre_ids") != null ? item.get("genre_ids").toString() : null);
                dto.setOriginalLanguage((String) item.get("original_language"));
                dto.setOriginCountry(item.get("origin_country") != null ? item.get("origin_country").toString() : null);

                String releaseDate = (String) item.get("release_date");
                if (releaseDate != null && !releaseDate.isBlank()) {
                    dto.setReleaseDate(LocalDate.parse(releaseDate));
                }

                dto.setAdult(item.get("adult") != null && (Boolean) item.get("adult"));
                dto.setVideo(item.get("video") != null && (Boolean) item.get("video"));

                movies.add(dto);
            }

            return movies;
        }
    }

    @Override
    public List<TvDTO> searchTvs(String keyword, String pageNum) throws Exception {
        String url = mainUrl + "discover/tv" + userKey + "&language=ko-KR" + "&query=" + keyword + pageNum;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("accept", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new RuntimeException("TV API 호출 실패");

            String json = response.body().string();
            Map<String, Object> map = objectMapper.readValue(json, Map.class);
            List<Map<String, Object>> results = (List<Map<String, Object>>) map.get("results");

            List<TvDTO> tvs = new ArrayList<>();
            for (Map<String, Object> item : results) {
                TvDTO dto = new TvDTO();
                dto.setId(getInteger(item.get("id")));
                dto.setName((String) item.get("name"));
                dto.setOriginalName((String) item.get("original_name"));
                dto.setOverview((String) item.get("overview"));
                dto.setPosterPath((String) item.get("poster_path"));
                dto.setBackdropPath((String) item.get("backdrop_path"));
                dto.setPopularity(getDouble(item.get("popularity")));
                dto.setVoteAverage(getDouble(item.get("vote_average")));
                dto.setVoteCount(getInteger(item.get("vote_count")));
                dto.setGenreIds(item.get("genre_ids") != null ? item.get("genre_ids").toString() : null);
                dto.setOriginalLanguage((String) item.get("original_language"));

                Object originCountryObj = item.get("origin_country");
                if (originCountryObj instanceof List<?> originList) {
                    dto.setOriginCountry(String.join(",", (List<String>) originList));
                }

                String firstAirDate = (String) item.get("first_air_date");
                if (firstAirDate != null && !firstAirDate.isBlank()) {
                    dto.setFirstAirDate(LocalDate.parse(firstAirDate));
                }

                tvs.add(dto);
            }

            return tvs;
        }
    }

    @Override
    public List<MovieDTO> searchAllMovies(String pageNum) throws Exception {
        String url = mainUrl + "discover/movie" + userKey + "&language=ko-KR" + pageNum;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("accept", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new RuntimeException("영화 API 호출 실패");

            String json = response.body().string();
            Map<String, Object> map = objectMapper.readValue(json, Map.class);
            List<Map<String, Object>> results = (List<Map<String, Object>>) map.get("results");

            List<MovieDTO> movies = new ArrayList<>();
            for (Map<String, Object> item : results) {
                MovieDTO dto = new MovieDTO();
                dto.setId(getInteger(item.get("id")));
                dto.setTitle((String) item.get("title"));
                dto.setOriginalTitle((String) item.get("original_title"));
                dto.setOverview((String) item.get("overview"));
                dto.setPosterPath((String) item.get("poster_path"));
                dto.setBackdropPath((String) item.get("backdrop_path"));
                dto.setPopularity(getDouble(item.get("popularity")));
                dto.setVoteAverage(getDouble(item.get("vote_average")));
                dto.setVoteCount(getInteger(item.get("vote_count")));
                dto.setGenreIds(item.get("genre_ids") != null ? item.get("genre_ids").toString() : null);
                dto.setOriginalLanguage((String) item.get("original_language"));
                dto.setOriginCountry(item.get("origin_country") != null ? item.get("origin_country").toString() : null);

                String releaseDate = (String) item.get("release_date");
                if (releaseDate != null && !releaseDate.isBlank()) {
                    dto.setReleaseDate(LocalDate.parse(releaseDate));
                }

                dto.setAdult(item.get("adult") != null && (Boolean) item.get("adult"));
                dto.setVideo(item.get("video") != null && (Boolean) item.get("video"));

                movies.add(dto);
            }

            return movies;
        }
    }

    @Override
    public List<TvDTO> searchAllTvs(String pageNum) throws Exception {
        String url = mainUrl + "discover/tv" + userKey + "&language=ko-KR" + pageNum;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("accept", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new RuntimeException("TV API 호출 실패");

            String json = response.body().string();
            Map<String, Object> map = objectMapper.readValue(json, Map.class);
            List<Map<String, Object>> results = (List<Map<String, Object>>) map.get("results");

            List<TvDTO> tvs = new ArrayList<>();
            for (Map<String, Object> item : results) {
                TvDTO dto = new TvDTO();
                dto.setId(getInteger(item.get("id")));
                dto.setName((String) item.get("name"));
                dto.setOriginalName((String) item.get("original_name"));
                dto.setOverview((String) item.get("overview"));
                dto.setPosterPath((String) item.get("poster_path"));
                dto.setBackdropPath((String) item.get("backdrop_path"));
                dto.setPopularity(getDouble(item.get("popularity")));
                dto.setVoteAverage(getDouble(item.get("vote_average")));
                dto.setVoteCount(getInteger(item.get("vote_count")));
                dto.setGenreIds(item.get("genre_ids") != null ? item.get("genre_ids").toString() : null);
                dto.setOriginalLanguage((String) item.get("original_language"));

                Object originCountryObj = item.get("origin_country");
                if (originCountryObj instanceof List<?> originList) {
                    dto.setOriginCountry(String.join(",", (List<String>) originList));
                }

                String firstAirDate = (String) item.get("first_air_date");
                if (firstAirDate != null && !firstAirDate.isBlank()) {
                    dto.setFirstAirDate(LocalDate.parse(firstAirDate));
                }

                tvs.add(dto);
            }

            return tvs;
        }
    }

    @Override
    public <T> List<T> searchPageFilters(Class<T> dtoClass, SearchDTO searchDTO) throws Exception {
        String encodedKeyword = URLEncoder.encode(searchDTO.getKeyword(), StandardCharsets.UTF_8)
                .replace("+", "%20");
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(mainUrl)
                .append("search/")
                .append(searchDTO.getType())
                .append(userKey)
                .append("&query=").append(encodedKeyword)
                .append("&language=ko")
                .append("&page=").append(searchDTO.getPageNum());
        if (searchDTO.getCountry() != null && !searchDTO.getCountry().isEmpty()) {
            urlBuilder.append("&region=").append(URLEncoder.encode(searchDTO.getCountry(), StandardCharsets.UTF_8));
        }

        String url = urlBuilder.toString();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI5MzM5M2Y4YjMzZjYzYjdjYTdkODE5ZTY2NTEzZTVkZSIsIm5iZiI6MTc1MTg1NTQ4Ny4xNDksInN1YiI6IjY4NmIzMTdmN2I3NjJhZWY3MjlhM2U4OCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.jd855cGFv2PF0heP-P0nTMk1tTdOYY0xvsTzO25OiUU")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();

                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                Map<String, Object> jsonMap = mapper.readValue(responseBody, new TypeReference<>() {
                });

                List<Map<String, Object>> results = (List<Map<String, Object>>) jsonMap.get("results");

                return results.stream()
                        .map(result -> {
                            T dto = mapper.convertValue(result, dtoClass);

                            try {
                                if (result.containsKey("genre_ids")) {
                                    List<Integer> genreIds = (List<Integer>) result.get("genre_ids");
                                    String genreString = genreIds.stream().map(Object::toString).collect(Collectors.joining(","));

                                    dtoClass.getMethod("setGenreIds", String.class).invoke(dto, genreString);
                                }
                            } catch (Exception ignored) {
                                ignored.printStackTrace();
                            }

                            return dto;
                        })
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            System.out.println("API 호출 중 오류 발생:");
            e.printStackTrace();
        }

        return Collections.emptyList();
    }

    @Override
    public List<MovieDTO> searchFilteredAllMovies(SearchDTO searchDTO) throws Exception {
        List<MovieDTO> allMovies = new ArrayList<>();

        String encodedKeyword = URLEncoder.encode(searchDTO.getKeyword(), StandardCharsets.UTF_8)
                .replace("+", "%20");
        int page = 1;
        int totalPages;

        do {
            StringBuilder urlBuilder = new StringBuilder();
            urlBuilder.append(mainUrl)
                    .append("search/")
                    .append(searchDTO.getType())
                    .append(userKey)
                    .append("&query=").append(encodedKeyword)
                    .append("&language=ko")
                    .append("&page=").append(page);
            String url = urlBuilder.toString();

            SearchMovieResponseDTO response = restTemplate.getForObject(url, SearchMovieResponseDTO.class);

            if (response != null && response.getResults() != null) {
                for (MovieDTO dto : response.getResults()) {
                    // genre_ids 배열을 문자열로 변환해 genreIds 필드에 저장
                    List<Integer> genreIdList = dto.getGenreIdList(); // 배열로 받아오는 필드

                    if (genreIdList != null && !genreIdList.isEmpty()) {
                        String genreIdsStr = genreIdList.stream()
                                .map(String::valueOf)
                                .collect(Collectors.joining(","));
                        dto.setGenreIds(genreIdsStr);
                    } else {
                        dto.setGenreIds(""); // 빈 값도 처리
                    }

                    // origin_country -> originCountry
                    List<String> originCountryList = dto.getOriginCountryList();
                    if (originCountryList != null && !originCountryList.isEmpty()) {
                        dto.setOriginCountry(String.join(",", originCountryList));
                    } else {
                        dto.setOriginCountry("");
                    }

                    allMovies.add(dto);
                }

                totalPages = response.getTotalPages();
            } else {
                totalPages = 0;
            }

            page++;
        } while (page <= totalPages);

        return allMovies;
    }


    @Override
    public List<TvDTO> searchFilteredAllTvs(SearchDTO searchDTO) throws Exception {
        List<TvDTO> allTvs = new ArrayList<>();

        String encodedKeyword = URLEncoder.encode(searchDTO.getKeyword(), StandardCharsets.UTF_8)
                .replace("+", "%20");
        int page = 1;
        int totalPages;

        do {
            StringBuilder urlBuilder = new StringBuilder();
            urlBuilder.append(mainUrl)
                    .append("search/")
                    .append(searchDTO.getType())
                    .append(userKey)
                    .append("&query=").append(encodedKeyword)
                    .append("&language=ko")
                    .append("&page=").append(page);
            String url = urlBuilder.toString();

            SearchTVResponseDTO response = restTemplate.getForObject(url, SearchTVResponseDTO.class);

            if (response != null && response.getResults() != null) {
                for (TvDTO dto : response.getResults()) {
                    // genre_ids -> genreIds
                    List<Integer> genreIdList = dto.getGenreIdList();
                    if (genreIdList != null && !genreIdList.isEmpty()) {
                        String genreIdsStr = genreIdList.stream()
                                .map(String::valueOf)
                                .collect(Collectors.joining(","));
                        dto.setGenreIds(genreIdsStr);
                    } else {
                        dto.setGenreIds("");
                    }

                    // origin_country -> originCountry
                    List<String> originCountryList = dto.getOriginCountryList();
                    if (originCountryList != null && !originCountryList.isEmpty()) {
                        dto.setOriginCountry(String.join(",", originCountryList));
                    } else {
                        dto.setOriginCountry("");
                    }

                    allTvs.add(dto);
                }

                totalPages = response.getTotalPages();
            } else {
                totalPages = 0;
            }

            page++;
        } while (page <= totalPages);

        return allTvs;
    }

    private Double getDouble(Object o) {
        if (o == null) return null;
        if (o instanceof Number) return ((Number) o).doubleValue();
        try {
            return Double.parseDouble(o.toString());
        } catch (Exception e) {
            return null;
        }
    }

    private Integer getInteger(Object o) {
        if (o == null) return null;
        if (o instanceof Number) return ((Number) o).intValue();
        try {
            return Integer.parseInt(o.toString());
        } catch (Exception e) {
            return null;
        }
    }
}
