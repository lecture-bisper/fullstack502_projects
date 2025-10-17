package bitc.full502.sceneshare.service.user;

import bitc.full502.sceneshare.domain.entity.dto.ReplyDTO;
import bitc.full502.sceneshare.domain.entity.user.ReplyEntity;
import bitc.full502.sceneshare.domain.entity.user.UserEntity;
import bitc.full502.sceneshare.domain.repository.user.ReplyRepository;
import bitc.full502.sceneshare.domain.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReplyServiceImpl implements ReplyService {

  private final ReplyRepository replyRepository;
  private final UserRepository userRepository;

  private static final int MAX_LEN = 1000;
//  private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

  @Transactional(readOnly = true)
  @Override
  public List<ReplyDTO> list(int boardId, String loginUserId) {
    // 1) 댓글 목록 먼저 읽기
    List<ReplyEntity> rows = replyRepository.findByBoardIdOrderByCreateDateDesc(boardId);
    // userId null 제거
    Set<String> uids = rows.stream()
        .map(ReplyEntity::getUserId)
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());

    List<UserEntity> users = uids.isEmpty()
        ? Collections.emptyList()
        : userRepository.findByUserIdIn(uids);

    Map<String, String> imgByUserId = users.stream()
        .collect(Collectors.toMap(
            UserEntity::getUserId,
            u -> {
              String img = u.getUserImg();      // 실제 필드명에 맞게
              return (img == null) ? "" : img;  // ★ null 방지
            },
            (a, b) -> a                         // 중복 키는 최초 값 유지
        ));

    return rows.stream()
        .map(e -> {
          String img = imgByUserId.get(e.getUserId());
          // 빈 문자열은 프론트에서 기본 이미지로 처리되도록 그대로 전달하거나,
          // 원하면 null로 되돌려도 됩니다:
          if (img != null && img.isBlank()) img = null;
          return toDto(e, loginUserId, img);
        })
        .toList();
  }

  @Transactional(readOnly = true)
  @Override
  public long count(int boardId) {
    return replyRepository.countByBoardId(boardId);
  }

  @Override
  public void create(int boardId, String loginUserId, String contents) {
    requireLogin(loginUserId);
    validateContents(contents);

    ReplyEntity r = new ReplyEntity();
    r.setBoardId(boardId);
    r.setUserId(loginUserId);           // ⬅️ 엔티티의 userId가 String일 때
    r.setContents(contents);
    replyRepository.save(r);            // @PrePersist 로 createDate 채워짐
  }

  private String getUserImgFor(String userId) {
    if (userId == null || userId.isBlank()) return null;
    try {
      UserEntity u = userRepository.findByUserId(userId); // 단건 조회
      if (u == null) return null;
      String img = u.getUserImg(); // 실제 필드명에 맞게
      return (img != null && !img.isBlank()) ? img : null;
    } catch (Exception e) {
      // 로그만 찍고 기본이미지로 fallback
      // log.warn("findByUserId failed for {}: {}", userId, e.getMessage());
      return null;
    }
  }

  @Override
  public ReplyDTO update(int replyId, String loginUserId, String contents) {
    requireLogin(loginUserId);
    validateContents(contents);

    ReplyEntity r = replyRepository.findById(replyId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    if (!loginUserId.equals(r.getUserId())) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "수정 권한이 없습니다.");
    }

    r.setContents(contents); // @PreUpdate 로 updateDate 세팅됨 (@Transactional이면 save 생략 가능)

    // ⬇️ 여기서 프로필 이미지 한 번 조회
    String userImg = getUserImgFor(r.getUserId());

    // ⬇️ 3번째 인자는 contents가 아니라 userImg입니다!
    return toDto(r, loginUserId, userImg);
  }

  @Override
  public void delete(int replyId, String loginUserId) {
    requireLogin(loginUserId);

    ReplyEntity r = replyRepository.findById(replyId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    if (!loginUserId.equals(r.getUserId())) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "삭제 권한이 없습니다.");
    }

    replyRepository.delete(r);
  }

  // ---------- helpers ----------

  private void requireLogin(String loginUserId) {
    if (loginUserId == null || loginUserId.isBlank()) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 필요");
    }
  }

  private void validateContents(String contents) {
    if (contents == null || contents.isBlank())
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "내용이 비어있습니다.");
    if (contents.length() > MAX_LEN)
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "1000자 이내로 입력하세요.");
  }

  private ReplyDTO toDto(ReplyEntity e, String loginUserId, String userImg) {
    boolean mine = loginUserId != null && loginUserId.equals(e.getUserId());
    return ReplyDTO.builder()
        .replyId(e.getReplyId())
        .userId(e.getUserId())
        .contents(e.getContents())
        .createDate(e.getCreateDate())    // ← 여기서 이미 "yyyy-MM-dd HH:mm" 문자열로 확정
        .mine(mine)
        .userImg(userImg)
        .build();
  }
}
