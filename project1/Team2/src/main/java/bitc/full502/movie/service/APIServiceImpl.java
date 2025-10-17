package bitc.full502.movie.service;

import bitc.full502.movie.dto.MediaDTO;
import bitc.full502.movie.dto.MovieDTO;
import bitc.full502.movie.dto.MemberDTO;
import bitc.full502.movie.dto.TvDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class APIServiceImpl implements APIService {

    private final JPAService jpaService;

    @Value("${movie.service.url}")
    private String mainUrl;

    @Value("${movie.service.userKey}")
    private String userKey;

    @Override
    public String createMovieListUrl(String keyword, String pageNum) throws Exception {
        String url = "";

        switch (keyword) {
            case "popularity":
                url = mainUrl + "discover/movie" + userKey
                        + "&language=ko"
                        + "&primary_release_date.gte=2025-01-01"
                        + "&vote_count.gte=500"
                        + "&sort_by=popularity.desc"
                        + "&page=" + pageNum;
                break;
            case "releaseDate":
                url = mainUrl + "discover/movie" + userKey
                        + "&language=ko"
                        + "&primary_release_date.lte=" + LocalDate.now()
                        + "&vote_count.gte=300"
                        + "&sort_by=primary_release_date.desc"
                        + "&page=" + pageNum;
                break;
            case "recommend":
                url = mainUrl + "discover/movie" + userKey
                        + "&language=ko"
                        + "&region=KR"
                        + "&primary_release_date.gte=2010-01-01"
                        + "&sort_by=popularity.desc"
                        + "&page=" + pageNum;
                break;
            default:
                String[] splitGenre = keyword.split(",");

                String[] sortByTypes = {
                        "popularity.desc",
                        "vote_average.desc",
                        "vote_count.desc",
                        "revenue.desc"
                };

                url = mainUrl + "discover/movie" + userKey
                        + "&language=ko"
                        + "&vote_count.gte=300"
                        + "&primary_release_date.gte=2010-01-01"
                        + "&with_genres=" + splitGenre[(int) (Math.random() * splitGenre.length)].trim()
                        + "&sort_by=" + sortByTypes[(int) (Math.random() * sortByTypes.length)].trim()
                        + "&page=" + pageNum;
                break;
        }

        return url;
    }

    @Override
    public String createTVListUrl(String keyword, String pageNum) throws Exception {
        String url = "";

        switch (keyword) {
            case "popularity":
                url = mainUrl + "discover/tv" + userKey
                        + "&language=ko"
                        + "&first_air_date.gte=2025-01-01"
                        + "&vote_count.gte=150"
                        + "&sort_by=popularity.desc"
                        + "&page=" + pageNum;
                break;
            case "firstAirDate":
                url = mainUrl + "discover/tv" + userKey
                        + "&language=ko"
                        + "&first_air_date.lte=" + LocalDate.now()
                        + "&vote_count.gte=300"
                        + "&sort_by=first_air_date.desc"
                        + "&page=" + pageNum;
                break;
            case "recommend":
                url = mainUrl + "discover/tv" + userKey
                        + "&language=ko"
                        + "&first_air_date.gte=2010-01-01"
                        + "&vote_count.gte=150"
                        + "&sort_by=popularity.desc"
                        + "&page=" + pageNum;
                break;
            default:
                String[] splitGenre = keyword.split(",");

                String[] sortByTypes = {
                        "popularity.desc",
                        "vote_average.desc",
                        "vote_count.desc",
                        "revenue.desc"
                };

                url = mainUrl + "discover/tv" + userKey
                        + "&language=ko"
                        + "&vote_count.gte=300"
                        + "&first_air_date.gte=2010-01-01"
                        + "&with_genres=" + splitGenre[(int) (Math.random() * splitGenre.length)].trim()
                        + "&sort_by=" + sortByTypes[(int) (Math.random() * sortByTypes.length)].trim()
                        + "&page=" + pageNum;
                break;
        }

        return url;
    }

    @Override
    public String createFavoriteUrl(String keyword, String type) throws Exception {
        String url = "";

        if (type.equals("movie")) {
            url = mainUrl + "movie/" + keyword + userKey
                    + "&language=ko";
        } else if (type.equals("tv")) {
            url = mainUrl + "tv/" + keyword + userKey
                    + "&language=ko";
        }

        return url;
    }

    @Override
    public <T> List<T> getContentList(Class<T> dtoClass, String url) throws Exception {
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
    public <T> T getContent(Class<T> dtoClass, String url) throws Exception {
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

                // case 1: results 배열이 있는 경우
                if (jsonMap.containsKey("results")) {
                    List<Map<String, Object>> results = (List<Map<String, Object>>) jsonMap.get("results");
                    if (results == null || results.isEmpty()) return null;

                    Map<String, Object> result = results.get(0);
                    T dto = mapper.convertValue(result, dtoClass);

                    try {
                        if (result.containsKey("genre_ids")) {
                            List<Integer> genreIds = (List<Integer>) result.get("genre_ids");
                            String genreString = genreIds.stream().map(Object::toString).collect(Collectors.joining(","));
                            dtoClass.getMethod("setGenreIds", String.class).invoke(dto, genreString);
                        }
                        if (result.containsKey("origin_country")) {
                            List<String> originCountries = (List<String>) result.get("origin_country");
                            String originCountryString = String.join(",", originCountries);
                            dtoClass.getMethod("setOriginCountry", String.class).invoke(dto, originCountryString);
                        }
                    } catch (Exception ignored) {
                        ignored.printStackTrace();
                    }

                    return dto;
                }
                // case 2: 단일 객체인 경우
                else {
                    T dto = mapper.convertValue(jsonMap, dtoClass);

                    try {
                        if (jsonMap.containsKey("genres")) {
                            List<Map<String, Object>> genres = (List<Map<String, Object>>) jsonMap.get("genres");
                            String genreString = genres.stream()
                                    .map(g -> g.get("id").toString())
                                    .collect(Collectors.joining(","));
                            dtoClass.getMethod("setGenreIds", String.class).invoke(dto, genreString);
                        }
                        if (jsonMap.containsKey("origin_country")) {
                            List<String> originCountries = (List<String>) jsonMap.get("origin_country");
                            String countryString = String.join(",", originCountries);
                            dtoClass.getMethod("setOriginCountry", String.class).invoke(dto, countryString);
                        }

                    } catch (Exception ignored) {
                        ignored.printStackTrace();
                    }

                    return dto;
                }
            }
        } catch (Exception e) {
            System.out.println("API 호출 중 오류 발생:");
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<MediaDTO> getFavoritesList(String userId) throws Exception {
        List<MediaDTO> favoritesList = new ArrayList<>();

        int[] movieIds = jpaService.getFavoriteContentsIds(userId, "movie");
        int[] tvIds = jpaService.getFavoriteContentsIds(userId, "tv");

        if (movieIds != null) {
            for (int ids : movieIds) {
                String favoritesUrl = createFavoriteUrl(String.valueOf(ids), "movie");
                MovieDTO favoritesMovieDto = getContent(MovieDTO.class, favoritesUrl);
                favoritesList.add(jpaService.convertMovieToMedia(favoritesMovieDto));
            }
        }
        if (tvIds != null) {
            for (int ids : tvIds) {
                String favoritesUrl = createFavoriteUrl(String.valueOf(ids), "tv");
                TvDTO favoritesTVDto = getContent(TvDTO.class, favoritesUrl);
                favoritesList.add(jpaService.convertTVToMedia(favoritesTVDto));
            }
        }

        return favoritesList;
    }

    @Override
    public String createUrl(String type, String contentId) throws Exception {
        return mainUrl + type + "/" + contentId + userKey + "&language=ko";
    }

    @Override
    public List<MemberDTO> getCastInfo(String type, String contentId) throws Exception {
        String url = "https://api.themoviedb.org/3/" + type + "/" + contentId + "/credits?language=ko";

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
                Map<String, Object> jsonMap = mapper.readValue(responseBody, new TypeReference<>() {
                });

                List<MemberDTO> castList = new ArrayList<>();
                if (jsonMap.containsKey("cast")) {
                    List<Map<String, Object>> rawCast = (List<Map<String, Object>>) jsonMap.get("cast");

                    for (Map<String, Object> castMap : rawCast) {
                        MemberDTO cast = new MemberDTO();
                        cast.setName((String) castMap.get("name"));
                        cast.setGender(String.valueOf(castMap.get("gender")));
                        cast.setOriginalName((String) castMap.get("original_name"));
                        cast.setProfilePath((String) castMap.get("profile_path"));
                        cast.setCharacter((String) castMap.get("character"));
                        cast.setOrder((Integer) castMap.get("order")); // 주의: null이면 ClassCastException 발생 가능

                        castList.add(cast);
                    }

                    // 주연만 필터링하고 반환 (order 기준)
                    return castList;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Collections.emptyList();
    }
}
