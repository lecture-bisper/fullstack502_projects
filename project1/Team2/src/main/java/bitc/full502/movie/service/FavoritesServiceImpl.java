package bitc.full502.movie.service;

import bitc.full502.movie.domain.entity.FavoritesEntity;
import bitc.full502.movie.domain.repository.FavoritesRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FavoritesServiceImpl implements FavoritesService {

    private final FavoritesRepository favoritesRepository;

    @Override
    public void insertBookmark(FavoritesEntity favorite) throws Exception {
        favoritesRepository.save(favorite);
    }

    @Override
    @Transactional
    public void deleteBookmark(String id, String type, String contentId) throws Exception {
        favoritesRepository.deleteByTypeAndContentsIdAndUser_Id(type, Integer.parseInt(contentId), id);
    }
}
