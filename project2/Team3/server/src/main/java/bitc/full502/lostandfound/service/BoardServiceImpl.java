package bitc.full502.lostandfound.service;

import bitc.full502.lostandfound.domain.entity.BoardEntity;
import bitc.full502.lostandfound.domain.entity.CategoryEntity;
import bitc.full502.lostandfound.domain.entity.UserEntity;
import bitc.full502.lostandfound.domain.repository.BoardRepository;
import bitc.full502.lostandfound.domain.repository.CategoryRepository;
import bitc.full502.lostandfound.domain.repository.UserRepository;
import bitc.full502.lostandfound.dto.BoardDTO;
import bitc.full502.lostandfound.util.BoardUtil;
import bitc.full502.lostandfound.util.Constants;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final FileService fileService;

    @Override
    public List<BoardDTO> getAllBoardList() throws Exception {
        return BoardUtil.convertToBoardDTOList(boardRepository.findAllByOrderByIdxDesc());
    }

    //    게시글 수정(이미지 포함)
    @Override
    @Transactional
    public BoardDTO updateBoardWithImage(BoardDTO dto, MultipartFile file) throws Exception {
        BoardEntity e = boardRepository.findById(dto.getIdx())
                .orElseThrow(() -> new IllegalArgumentException("해당 게시물이 없습니다."));

        if (dto.getCategoryId() > 0) {
            CategoryEntity category = categoryRepository.findById((long) dto.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("카테고리가 없습니다. id=" + dto.getCategoryId()));
            e.setCategory(category);
        }

        if (dto.getTitle() != null) e.setTitle(dto.getTitle());
        if (dto.getOwnerName() != null) e.setOwnerName(dto.getOwnerName());
        if (dto.getDescription() != null) e.setDescription(dto.getDescription());
        if (dto.getEventDate() != null) e.setEventDate(dto.getEventDate());
        if (dto.getEventLat() != null) e.setEventLat(dto.getEventLat());
        if (dto.getEventLng() != null) e.setEventLng(dto.getEventLng());
        if (dto.getEventDetail() != null) e.setEventDetail(dto.getEventDetail());
        if (dto.getStorageLocation() != null) e.setStorageLocation(dto.getStorageLocation());
        if (dto.getType() != null) e.setType(dto.getType());
        if (dto.getStatus() != null) e.setStatus(dto.getStatus());


        if (file != null && !file.isEmpty()) {
            // 기존 이미지 삭제
            if (e.getImgUrl() != null && !e.getImgUrl().isEmpty()) {
                fileService.deleteFile(e.getImgUrl(), "board");
            }
            // 새 이미지 업로드
            String newImgUrl = fileService.uploadFile(file, "/board", e.getUser().getUserId());
            e.setImgUrl(newImgUrl);
        }
        // file이 없으면 기존 이미지 유지

        BoardEntity saved = boardRepository.save(e);
        return toDto(saved);
    }

    //    게시글 삭제
    @Override
    public void deleteBoard(Long id) throws Exception {
        BoardEntity e = boardRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("게시물이 없습니다. id=" + id));
        boardRepository.delete(e);
    }

    //    게시글 검색
    @Override
    @Transactional(readOnly = true)
    public List<BoardDTO> searchBoardList(String keyword, Integer categoryId, String type, LocalDateTime fromDate, LocalDateTime toDate) throws Exception {
        String kw = (keyword == null || keyword.isBlank()) ? null : keyword.trim();
        return BoardUtil.convertToBoardDTOList(boardRepository.search(kw, categoryId, type, fromDate, toDate));
    }

    //    게시글 등록
    @Override
    public BoardDTO insertBoard(BoardDTO dto) throws Exception {

        UserEntity user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("유저 없음:" + dto.getUserId()));

        CategoryEntity category = categoryRepository.findById((long) dto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("카테고리 없음 :" + dto.getCategoryId()));

        BoardEntity entity = toEntity(dto, user, category);
        BoardEntity saved = boardRepository.save(entity);

        return toDto(saved);
    }

    @Override
    public BoardDTO getBoardDetail(Long id) throws Exception {
        BoardEntity board = boardRepository.findById(id).orElseThrow(() -> new RuntimeException("게시글 없음"));
        return toDto(board);
    }

    @Override
    public void updateBoardStatus(Long idx) throws Exception {
        BoardEntity board = boardRepository.findById(idx).orElseThrow(() -> new Exception("해당 게시글이 존재하지 않습니다."));
        board.setStatus(Constants.STATUS_COMPLETE);
        boardRepository.save(board);
    }


    //    DB에서 꺼넨 데이터를 API 응답용으로 변환해주는 코드임
    private BoardDTO toDto(BoardEntity e) {
        return new BoardDTO(
                e.getIdx(),
                e.getUser() != null ? e.getUser().getUserId() : null,
                e.getCategory().getCategoryId(),
                e.getTitle(),
                e.getImgUrl(),
                e.getOwnerName(),
                e.getDescription(),
                e.getEventDate(),
                e.getEventLat(),
                e.getEventLng(),
                e.getEventDetail(),
                e.getStorageLocation(),
                e.getType(),
                e.getStatus(),
                e.getCreateDate()
        );
    }

    //    사용자하네 받은 데이터 DB에 저장하는 용도임
    private BoardEntity toEntity(BoardDTO dto, UserEntity user, CategoryEntity category) {

        BoardEntity entity = new BoardEntity();
        entity.setUser(user);
        entity.setCategory(category);
        entity.setTitle(dto.getTitle());
        entity.setImgUrl(dto.getImgUrl());
        entity.setOwnerName(dto.getOwnerName());
        entity.setDescription(dto.getDescription());
        entity.setEventDate(dto.getEventDate());
        entity.setEventLat(dto.getEventLat());
        entity.setEventLng(dto.getEventLng());
        entity.setEventDetail(dto.getEventDetail());
        entity.setStorageLocation(dto.getStorageLocation());
        entity.setType(dto.getType());
        entity.setStatus(dto.getStatus());

        return entity;
    }
}