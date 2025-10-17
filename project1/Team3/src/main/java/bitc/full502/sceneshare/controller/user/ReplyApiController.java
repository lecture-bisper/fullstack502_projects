package bitc.full502.sceneshare.controller.user;

import bitc.full502.sceneshare.domain.entity.dto.ReplyDTO;
import bitc.full502.sceneshare.service.user.ReplyService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/replies")
public class ReplyApiController {

  private final ReplyService replyService;

  private String loginOr401(HttpSession session) {
    Object uid = session.getAttribute("userId");
    if (uid == null) uid = session.getAttribute("loginId");
    if (uid == null) uid = session.getAttribute("userid");
    if (uid == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 필요");
    return String.valueOf(uid);
  }

  @GetMapping
  public Map<String,Object> list(@RequestParam int boardId, HttpSession session) {
    String login = (String) session.getAttribute("userId"); // null OK
    List<ReplyDTO> list = replyService.list(boardId, login);
    long count = replyService.count(boardId);
    Map<String,Object> res = new HashMap<>();
    res.put("items", list);
    res.put("count", count);
    return res;
  }

  @PostMapping(consumes = "application/x-www-form-urlencoded")
  public ResponseEntity<?> create(@RequestParam int boardId,
                                  @RequestParam String contents,
                                  HttpServletRequest req) {
    HttpSession sess = req.getSession(false); // 새로 만들지 않음
    String loginId = (sess != null) ? (String) sess.getAttribute("userId") : null;
    if (loginId == null) return ResponseEntity.status(401).body(Map.of("message","로그인 필요"));

    replyService.create(boardId, loginId, contents);
    return ResponseEntity.ok().build(); // 프론트는 즉시 refreshReplies() 호출하므로 OK만으로 충분
  }


  @PutMapping("/{replyId}")
  public Map<String,Object> update(@PathVariable int replyId,
                                   @RequestParam String contents,
                                   HttpSession session) {
    String login = loginOr401(session);
    ReplyDTO dto = replyService.update(replyId, login, contents);
    return Map.of("item", dto);
  }

  @DeleteMapping("/{replyId}")
  public Map<String,Object> delete(@PathVariable int replyId,
                                   HttpSession session) {
    String login = loginOr401(session);
    replyService.delete(replyId, login);
    return Map.of("ok", true);
  }
}
