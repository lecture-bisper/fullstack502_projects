package bitc.full502.lostandfound.util;

import bitc.full502.lostandfound.domain.entity.BoardEntity;
import bitc.full502.lostandfound.dto.BoardDTO;

import java.util.List;
import java.util.stream.Collectors;

public class BoardUtil {

    public static BoardDTO convertToBoardDTO(BoardEntity boardEntity) {
        return new BoardDTO(
                boardEntity.getIdx(),
                boardEntity.getUser().getUserId(),
                boardEntity.getCategory().getCategoryId(),
                boardEntity.getTitle(),
                boardEntity.getImgUrl(),
                boardEntity.getOwnerName(),
                boardEntity.getDescription(),
                boardEntity.getEventDate(),
                boardEntity.getEventLat(),
                boardEntity.getEventLng(),
                boardEntity.getEventDetail(),
                boardEntity.getStorageLocation(),
                boardEntity.getType(),
                boardEntity.getStatus(),
                boardEntity.getCreateDate()
        );
    }

    public static List<BoardDTO> convertToBoardDTOList(List<BoardEntity> boardEntities) {
        return boardEntities.stream()
                .map(BoardUtil::convertToBoardDTO)
                .collect(Collectors.toList());
    }
}
