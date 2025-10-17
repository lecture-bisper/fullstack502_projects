package bitc.full502.sceneshare.controller.user;

import bitc.full502.sceneshare.service.user.BoardService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class BoardApiController {

  private final BoardService boardService;

  private String loginOr401(HttpSession session) {
    Object uid = session.getAttribute("userId");
    if (uid == null) uid = session.getAttribute("loginId");
    if (uid == null) uid = session.getAttribute("userid");
    if (uid == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 필요");
    return String.valueOf(uid);
  }

  @PutMapping("/{boardId}")
  public ResponseEntity<Void> update(@PathVariable int boardId,
                                     @RequestParam String contents,
                                     @RequestParam(required = false, defaultValue = "0") double rating,
                                     HttpSession session) {
    String loginUserId = loginOr401(session);
    boardService.update(boardId, loginUserId, contents, rating);
    // 프런트는 바디를 쓰지 않으므로 200 OK만 반환
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{boardId}")
  public ResponseEntity<Void> delete(@PathVariable int boardId, HttpSession session) {
    String loginUserId = loginOr401(session);
    boardService.delete(boardId, loginUserId);
    return ResponseEntity.ok().build();
  }
}
