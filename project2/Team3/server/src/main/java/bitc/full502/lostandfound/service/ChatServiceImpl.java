package bitc.full502.lostandfound.service;

import bitc.full502.lostandfound.domain.entity.BoardEntity;
import bitc.full502.lostandfound.domain.entity.ChatEntity;
import bitc.full502.lostandfound.domain.entity.ChatRoomEntity;
import bitc.full502.lostandfound.domain.repository.BoardRepository;
import bitc.full502.lostandfound.domain.repository.ChatRepository;
import bitc.full502.lostandfound.domain.repository.ChatRoomRepository;
import bitc.full502.lostandfound.dto.ChatDTO;
import bitc.full502.lostandfound.dto.ChatRoomDTO;
import bitc.full502.lostandfound.util.ChatUtil;
import bitc.full502.lostandfound.util.Constants;
import bitc.full502.lostandfound.websocket.ChatHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRepository chatRepository;
    private final JpaService jpaService;
    private final BoardRepository boardRepository;
    private final ChatHandler chatHandler;

    @Override
    public List<ChatRoomDTO> getAllRooms(String token) throws Exception {
        String userId = jpaService.getUserIdByToken(token);
        List<ChatRoomEntity> rooms = chatRoomRepository.findAllByUserId(userId);
        List<ChatRoomDTO> dtos = new ArrayList<>();

        rooms.forEach(room -> {
            if (!room.getChats().isEmpty()) {
                ChatRoomDTO chatRoomDTO = new ChatRoomDTO();

                chatRoomDTO.setRoomIdx(room.getIdx());
                chatRoomDTO.setBoardIdx(room.getBoard().getIdx());
                chatRoomDTO.setUserId1(room.getUserId1());
                chatRoomDTO.setUserId2(room.getUserId2());
                chatRoomDTO.setCreatedDate(room.getCreatedDate());
                chatRoomDTO.setUnreadCount(chatRepository.countByChatRoomIdxAndStatusAndSenderNot(room.getIdx(), Constants.CHAT_UNREAD, userId));
                chatRoomDTO.setImgUrl(room.getBoard().getImgUrl());
                chatRoomDTO.setTitle(room.getBoard().getTitle());
                chatRoomDTO.setCategoryId((long) room.getBoard().getCategory().getCategoryId());
                chatRoomDTO.setBoardType(room.getBoard().getType());
                chatRoomDTO.setUpdatedDate(room.getUpdateDate());

                dtos.add(chatRoomDTO);
            }
        });

        return dtos;
    }

    @Override
    @Transactional
    public List<ChatDTO> getChatOrCreateRoom(String token, String otherUserId, Long boardIdx) throws Exception {
        String userId = jpaService.getUserIdByToken(token);
        Optional<ChatRoomEntity> chatRoom = chatRoomRepository.findChatRoomBetweenUsers(userId, otherUserId, boardIdx);
        BoardEntity board = boardRepository.findById(boardIdx).orElse(null);

        ChatRoomEntity room = chatRoom.orElseGet(() -> {
            ChatRoomEntity newRoom = new ChatRoomEntity();
            newRoom.setUserId1(userId);
            newRoom.setUserId2(otherUserId);
            newRoom.setBoard(board);
            return chatRoomRepository.save(newRoom);
        });

        List<ChatEntity> chats = chatRepository.findAllByChatRoomOrderBySendDateAsc(room);
        chats.forEach(chat -> {
            if (!chat.getSender().equals(userId)) {
                chat.setStatus(Constants.CHAT_READ);
            }
        });
        return ChatUtil.convertToChatDTOList(chats);
    }

    @Override
    public String insertChat(String token, Long idx, ChatDTO chatDTO) throws Exception {
        String userId = jpaService.getUserIdByToken(token);
        if (userId == null) return Constants.FAILURE;

        Optional<ChatRoomEntity> chatRoom = chatRoomRepository.findById(idx);
        if (chatRoom.isPresent()) {
            ChatEntity newChat = new ChatEntity();
            newChat.setChatRoom(chatRoom.get());
            newChat.setSender(userId);
            newChat.setMessage(chatDTO.getMessage());
            // 맵으로 타켓이 입장 중이면 STATUS 를 읽음으로 변경
            newChat.setStatus(chatHandler.isUserOnline(chatDTO.getTarget()) ? Constants.CHAT_READ : Constants.CHAT_UNREAD);
            chatRoom.get().setUpdateDate(LocalDateTime.now());
            chatRepository.save(newChat);
            return Constants.SUCCESS;
        }

        return Constants.FAILURE;
    }

    @Override
    public ChatRoomDTO getChatRoom(String token, Long boardIdx) throws Exception {
        String userId = jpaService.getUserIdByToken(token);
        ChatRoomEntity chatroom = chatRoomRepository.getRoomByUserIdAndBoardIdx(userId, boardIdx);
        ChatRoomDTO roomDTO = new ChatRoomDTO();

        roomDTO.setRoomIdx(chatroom.getIdx());
        roomDTO.setBoardIdx(chatroom.getBoard().getIdx());
        roomDTO.setUserId1(chatroom.getUserId1());
        roomDTO.setUserId2(chatroom.getUserId2());
        roomDTO.setCreatedDate(chatroom.getCreatedDate());
        roomDTO.setUnreadCount(chatRepository.countByChatRoomIdxAndStatus(chatroom.getIdx(), Constants.CHAT_UNREAD));
        roomDTO.setImgUrl(chatroom.getBoard().getImgUrl());
        roomDTO.setTitle(chatroom.getBoard().getTitle());
        roomDTO.setCategoryId((long) chatroom.getBoard().getCategory().getCategoryId());
        roomDTO.setBoardType(chatroom.getBoard().getType());
        roomDTO.setUpdatedDate(chatroom.getUpdateDate());
        return roomDTO;
    }
}
