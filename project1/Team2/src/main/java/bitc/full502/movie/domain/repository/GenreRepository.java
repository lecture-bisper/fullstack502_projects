package bitc.full502.movie.domain.repository;

import bitc.full502.movie.domain.entity.GenreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GenreRepository extends JpaRepository<GenreEntity, String> {

    @Query("SELECT g.krName FROM GenreEntity g WHERE g.id = :id")
    String findKrNameById(@Param("id") String id);
}
