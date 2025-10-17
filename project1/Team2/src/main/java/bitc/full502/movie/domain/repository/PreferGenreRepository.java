package bitc.full502.movie.domain.repository;

import bitc.full502.movie.domain.entity.PreferGenreEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PreferGenreRepository extends JpaRepository<PreferGenreEntity, Long> {

    //    @Query 문 추가
    @Query("SELECT pg.genre.id FROM PreferGenreEntity pg WHERE pg.user.id = :userId AND pg.type = :type")
    List<String> findGenreIdsByUserId(@Param("userId") String userId, @Param("type") String type);

    @Modifying
    @Transactional
    @Query("DELETE FROM PreferGenreEntity p WHERE p.user.id = :userId")
    void deleteGenreIdsByUserId(@Param("userId") String userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM PreferGenreEntity pg WHERE pg.user.id = :id")
    void deleteAllByUserId(@Param("id") String id) throws Exception;
}
