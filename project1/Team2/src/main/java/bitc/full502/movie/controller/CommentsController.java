package bitc.full502.movie.controller;

import bitc.full502.movie.domain.entity.CommentsEntity;
import bitc.full502.movie.domain.entity.UserEntity;
import bitc.full502.movie.service.CommentsService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentsController {

    private final CommentsService commentsService;

    // 댓글 목록 조회 (영화)
    @GetMapping("/comment/{type}/{contentId}")
    public List<CommentsEntity> getMovieComments(@PathVariable int contentId, @PathVariable String type) throws Exception {
        return commentsService.getCommentList(contentId, type);
    }

    // 댓글 저장
    @PostMapping("/comment/{type}/{contentId}")
    public CommentsEntity saveComment(@PathVariable String type, @PathVariable int contentId,
                            @RequestBody CommentsEntity commentsEntity, HttpSession session) throws Exception {
        UserEntity loginUser = (UserEntity) session.getAttribute("loginUser");

        commentsEntity.setUserName(loginUser.getName());
        commentsEntity.setContentId(contentId);
        commentsEntity.setUser(loginUser);
        commentsEntity.setType(type);

        return commentsService.insertComment(commentsEntity);
    }

    // 댓글 삭제
    @DeleteMapping("/comment/{type}/{contentId}/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable String type, @PathVariable int contentId,
                                           @PathVariable int commentId, HttpSession session) throws Exception {
        UserEntity loginUser = (UserEntity) session.getAttribute("loginUser");
        try {
            commentsService.deleteComment(type, contentId, commentId, loginUser.getId());
            return ResponseEntity.ok("댓글이 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }
}
