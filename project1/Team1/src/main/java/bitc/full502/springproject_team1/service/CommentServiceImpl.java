package bitc.full502.springproject_team1.service;

import bitc.full502.springproject_team1.repository.BoardCommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private BoardCommentRepository boardCommentRepository;

    @Override
    public void deleteById(Integer commentId) {
        boardCommentRepository.deleteById(commentId);
    }
}
