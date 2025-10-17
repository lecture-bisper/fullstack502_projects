package bitc.full502.lostandfound.controller;

import bitc.full502.lostandfound.domain.entity.BoardEntity;
import bitc.full502.lostandfound.domain.repository.BoardRepository;
import bitc.full502.lostandfound.dto.BoardDTO;
import bitc.full502.lostandfound.service.BoardService;
import bitc.full502.lostandfound.service.FileService;
import bitc.full502.lostandfound.service.JpaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardController {

    private final BoardRepository boardRepository;
    private final BoardService boardService;
    private final FileService fileService;
    private final JpaService jpaService;

    //    게시글 목록
    @GetMapping("/list")
    public List<BoardDTO> getAllBoardList() throws Exception {
        return boardService.getAllBoardList();
    }

    //    게시글 수정 (이미지 포함)
    @PutMapping("/update")
    public ResponseEntity<BoardDTO> updateBoard(@RequestPart("dto") BoardDTO dto,
                                                @RequestPart(value = "file", required = false) MultipartFile file) throws Exception {
        BoardDTO updated = boardService.updateBoardWithImage(dto, file);
        return ResponseEntity.ok(updated);
    }

    //    게시글 삭제
    @DeleteMapping("/{id}")
    public void deleteBoard(@PathVariable Long id) throws Exception {
        BoardEntity board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다."));

        // 이미지가 있다면 삭제
        if (board.getImgUrl() != null && !board.getImgUrl().isEmpty()) {
            fileService.deleteFile(board.getImgUrl(), "board");
        }

        boardService.deleteBoard(id);
    }

    //    게시글 검색
    @GetMapping("/search")
    public List<BoardDTO> searchBoardList(@RequestParam(required = false, name = "keyword") String keyword,
                                          @RequestParam(required = false) Integer categoryId,
                                          @RequestParam(required = false) String type,
                                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) throws Exception {

        LocalDateTime from = (fromDate != null) ? fromDate.atStartOfDay() : null;
        LocalDateTime to = (toDate != null) ? toDate.plusDays(1).atStartOfDay() : null;
        return boardService.searchBoardList(keyword, categoryId, type, from, to);
    }

    //    게시글 등록
    @PostMapping("/insert")
    public ResponseEntity<BoardDTO> insertBoard(@RequestParam("dto") String dtoJson, @RequestParam(value = "file", required = false) MultipartFile file,
                                                @RequestHeader(value = "Authorization", required = false) String authorizationHeader) throws Exception {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = authorizationHeader.substring(7);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        BoardDTO dto = mapper.readValue(dtoJson, BoardDTO.class);
        dto.setUserId(jpaService.getUserIdByToken(token));

        if (file != null && !file.isEmpty()) {
            String imgUrl = fileService.uploadFile(file, "/board", dto.getUserId());
            dto.setImgUrl(imgUrl);
        }

        BoardDTO saved = boardService.insertBoard(dto);
        return ResponseEntity.ok(saved);
    }

    //    게시글 상세
    @GetMapping("/{id}")
    public ResponseEntity<BoardDTO> getBoardDetail(@PathVariable Long id) throws Exception {
        BoardDTO dto = boardService.getBoardDetail(id);
        return ResponseEntity.ok(dto);
    }

    //    게시글 상태 변경
    @PutMapping("/{idx}/complete")
    public ResponseEntity<String> completeBoard(@PathVariable Long idx) throws Exception {
        boardService.updateBoardStatus(idx); // ✅ 여기서는 문제 없음
        return ResponseEntity.ok("처리 완료 되었습니다.");
    }
}