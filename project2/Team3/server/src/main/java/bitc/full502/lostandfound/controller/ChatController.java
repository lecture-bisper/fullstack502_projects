package bitc.full502.lostandfound.controller;

import bitc.full502.lostandfound.dto.ChatDTO;
import bitc.full502.lostandfound.dto.ChatRoomDTO;
import bitc.full502.lostandfound.service.ChatService;
import bitc.full502.lostandfound.service.FcmService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;
    private final FcmService fcmService;

    // 토큰으로 전체 채팅방 불러오기
    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomDTO>> getAllRooms(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) throws Exception {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = authorizationHeader.substring(7);
        return ResponseEntity.ok().body(chatService.getAllRooms(token));
    }

    // boardIdx로 채팅방 반환
    @GetMapping("/rooms/{boardIdx}")
    public ResponseEntity<ChatRoomDTO> getChatRoom(@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                                                   @PathVariable("boardIdx") Long boardIdx) throws Exception {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = authorizationHeader.substring(7);
        return ResponseEntity.ok(chatService.getChatRoom(token, boardIdx));
    }

    // 유저1과 유저2의 채팅방 존재 여부 확인 후 없으면 생성,
    @PostMapping("/rooms")
    public ResponseEntity<List<ChatDTO>> getOrCreateChatRoom(@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                                                             @RequestParam String otherUserId, @RequestParam Long boardIdx) throws Exception {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = authorizationHeader.substring(7);
        return ResponseEntity.ok(chatService.getChatOrCreateRoom(token, otherUserId, boardIdx));
    }

    // 채팅 저장 (SUCCESS/FAILURE 반환)
    @PostMapping("/rooms/{roomIdx}")
    public ResponseEntity<String> insertChat(@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                                             @RequestBody ChatDTO chatDTO, @PathVariable Long roomIdx) throws Exception {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = authorizationHeader.substring(7);
        fcmService.sendNotification(token, chatDTO.getTarget(), chatDTO.getMessage(), roomIdx);
        return ResponseEntity.ok(chatService.insertChat(token, roomIdx, chatDTO));
    }
}
