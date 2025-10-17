package bitc.full502.movie.domain.repository;

import bitc.full502.movie.domain.entity.CommentsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentsRepository extends JpaRepository<CommentsEntity, Integer> {

    @Query("SELECT c FROM CommentsEntity c WHERE c.contentId = :contentId AND c.type = :type ORDER BY c.commentDate DESC")
    List<CommentsEntity> findByContentIdAndTypeOrderByCommentDateDesc(@Param("contentId") int contentId, @Param("type") String type);

}
