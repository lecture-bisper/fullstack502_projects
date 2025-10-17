package bitc.full502.lostandfound.service;

import bitc.full502.lostandfound.dto.ChatDTO;
import bitc.full502.lostandfound.dto.ChatRoomDTO;

import java.util.List;

public interface ChatService {

    List<ChatRoomDTO> getAllRooms(String token) throws Exception;

    List<ChatDTO> getChatOrCreateRoom(String token, String otherUserId, Long boardIdx) throws Exception;

    String insertChat(String token, Long idx, ChatDTO chatDTO) throws Exception;

    ChatRoomDTO getChatRoom(String token, Long boardIdx) throws Exception;
}
