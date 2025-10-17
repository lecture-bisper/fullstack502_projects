package bitc.full502.lostandfound.util;

import bitc.full502.lostandfound.domain.entity.ChatEntity;
import bitc.full502.lostandfound.domain.entity.ChatRoomEntity;
import bitc.full502.lostandfound.dto.ChatDTO;
import bitc.full502.lostandfound.dto.ChatRoomDTO;

import java.util.List;
import java.util.stream.Collectors;

public class ChatUtil {

    // 단일 ChatEntity → ChatDTO
    public static ChatDTO convertToChatDTO(ChatEntity chat) {
        return new ChatDTO(
                chat.getChatRoom().getIdx(),
                chat.getSender(),
                "",
                chat.getMessage(),
                chat.getSendDate(),
                chat.getStatus()
        );
    }

    // 리스트 변환
    public static List<ChatDTO> convertToChatDTOList(List<ChatEntity> chats) {
        return chats.stream()
                .map(ChatUtil::convertToChatDTO)
                .collect(Collectors.toList());
    }
}
