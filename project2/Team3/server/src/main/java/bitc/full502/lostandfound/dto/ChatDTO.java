package bitc.full502.lostandfound.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatDTO {

    private Long roomIdx;

    private String sender;   // 보내는 사람

    private String target;   // 받는 사람

    private String message;  // 메시지 내용

    private LocalDateTime sendDate;

    private String status;
}

