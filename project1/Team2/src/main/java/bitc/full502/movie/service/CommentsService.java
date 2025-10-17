package bitc.full502.movie.service;

import bitc.full502.movie.domain.entity.CommentsEntity;

import java.util.List;

public interface CommentsService {

    List<CommentsEntity> getCommentList(int contentId, String type) throws Exception;

    void deleteComment(String type, int contentId, int commentId, String userId) throws Exception;

    CommentsEntity insertComment(CommentsEntity commentsEntity) throws Exception;
}
