package bitc.full502.lostandfound.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDTO {

    private Long roomIdx;

    private Long boardIdx;

    private String userId1;

    private String userId2;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

    private int unreadCount;

    private String imgUrl;

    private String title;

    private Long categoryId;

    private String boardType;
}
