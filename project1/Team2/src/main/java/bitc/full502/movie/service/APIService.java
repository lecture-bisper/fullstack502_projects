package bitc.full502.movie.service;

import bitc.full502.movie.dto.MediaDTO;
import bitc.full502.movie.dto.MemberDTO;

import java.util.List;

public interface APIService {

    String createMovieListUrl(String keyword, String pageNum) throws Exception;

    String createTVListUrl(String keyword, String pageNum) throws Exception;

    String createFavoriteUrl(String keyword, String type) throws Exception;

    <T> List<T> getContentList(Class<T> dtoClass, String url) throws Exception;

    <T> T getContent(Class<T> dtoClass, String url) throws Exception;

    List<MediaDTO> getFavoritesList(String userId) throws Exception;

    String createUrl(String type, String contentId) throws Exception;

    List<MemberDTO> getCastInfo(String type, String contentId) throws Exception;
}
