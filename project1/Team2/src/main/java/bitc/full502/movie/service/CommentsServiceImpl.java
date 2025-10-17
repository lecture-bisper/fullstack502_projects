package bitc.full502.movie.service;

import bitc.full502.movie.domain.entity.CommentsEntity;
import bitc.full502.movie.domain.repository.CommentsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentsServiceImpl implements CommentsService {

    private final CommentsRepository commentsRepository;

    @Override
    public List<CommentsEntity> getCommentList(int contentId, String type) throws Exception {
        return commentsRepository.findByContentIdAndTypeOrderByCommentDateDesc(contentId, type);
    }

    @Override
    public void deleteComment(String type, int contentId, int commentId, String userId) throws Exception {
        CommentsEntity comment = commentsRepository.findById(commentId)
                .orElseThrow(() -> new Exception("댓글이 존재하지 않습니다."));

        if (!comment.getUser().getId().equals(userId)) {
            throw new Exception("본인 댓글만 삭제 가능합니다..");
        }

        commentsRepository.deleteById(commentId);
    }

    @Override
    public CommentsEntity insertComment(CommentsEntity commentsEntity) throws Exception {
        return commentsRepository.save(commentsEntity);
    }
}
