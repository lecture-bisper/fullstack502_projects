package bitc.full502.lostandfound.service;

import bitc.full502.lostandfound.dto.BoardDTO;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public interface BoardService {
    //    목록 불러오기
    List<BoardDTO> getAllBoardList() throws Exception;

    //    게시글 수정(이미지 포함)
    BoardDTO updateBoardWithImage(BoardDTO dto, MultipartFile file) throws Exception;

    //    게시글 삭제
    void deleteBoard(Long id) throws Exception;

    //    게시글 검색
    List<BoardDTO> searchBoardList(String keyword, Integer categoryId, String type, LocalDateTime fromDate, LocalDateTime toDate) throws Exception;

    //    게시글 등록
    BoardDTO insertBoard(BoardDTO dto) throws Exception;

    BoardDTO getBoardDetail(Long id) throws Exception;

    void updateBoardStatus(Long idx) throws Exception;
}
