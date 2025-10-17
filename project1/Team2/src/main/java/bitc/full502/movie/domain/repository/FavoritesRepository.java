package bitc.full502.movie.domain.repository;

import bitc.full502.movie.domain.entity.FavoritesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FavoritesRepository extends JpaRepository<FavoritesEntity, Long> {

    @Query("SELECT f.contentsId FROM FavoritesEntity f WHERE f.user.id = :userId AND f.type = :type")
    List<Integer> findContentsIdsByUserAndType(@Param("userId") String userId, @Param("type") String type);

    void deleteByTypeAndContentsIdAndUser_Id(String type, int contentsId, String userId);
}
