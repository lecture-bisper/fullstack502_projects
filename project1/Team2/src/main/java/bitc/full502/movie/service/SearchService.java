package bitc.full502.movie.service;

import bitc.full502.movie.dto.MediaDTO;
import bitc.full502.movie.dto.MovieDTO;
import bitc.full502.movie.dto.SearchDTO;
import bitc.full502.movie.dto.TvDTO;

import java.util.List;

public interface SearchService {

    List<MovieDTO> searchMovies(String keyword, String pageNum) throws Exception;

    List<TvDTO> searchTvs(String keyword,  String pageNum)  throws Exception;

    List<MovieDTO> searchAllMovies(String pageNum) throws Exception;

    List<TvDTO> searchAllTvs(String pageNum) throws Exception;

    <T> List<T> searchPageFilters(Class<T> dtoClass, SearchDTO searchDTO) throws Exception;

    List<MovieDTO> searchFilteredAllMovies(SearchDTO searchDTO) throws Exception;

    List<TvDTO> searchFilteredAllTvs(SearchDTO searchDTO) throws Exception;
}
