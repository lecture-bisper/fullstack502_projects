package bitc.full502.movie.service;

import bitc.full502.movie.domain.entity.UserEntity;
import bitc.full502.movie.dto.MediaDTO;
import bitc.full502.movie.dto.MovieDTO;
import bitc.full502.movie.dto.TvDTO;

import java.util.List;

public interface JPAService {

    Boolean duplicateCheck(String userId) throws Exception;

    UserEntity registerUser(UserEntity user) throws Exception;

    String getPreferredGenres(String userId, String type) throws Exception;

    int[] getFavoriteContentsIds(String id, String type) throws Exception;

    List<MediaDTO> convertMovieToMedia(List<MovieDTO> DTOList) throws Exception;

    MediaDTO convertMovieToMedia(MovieDTO dto) throws Exception;

    List<MediaDTO> convertTVToMedia(List<TvDTO> DTOList) throws Exception;

    MediaDTO convertTVToMedia(TvDTO dto) throws Exception;

    String convertGenre(String genreIds) throws Exception;
}
