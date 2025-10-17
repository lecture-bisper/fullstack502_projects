package bitc.full502.movie.service;

import bitc.full502.movie.domain.entity.FavoritesEntity;

public interface FavoritesService {

    void insertBookmark(FavoritesEntity favorite) throws Exception;

    void deleteBookmark(String id, String type, String contentId) throws Exception;
}
