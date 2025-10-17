package bitc.full502.lostandfound.domain.entity;

import bitc.full502.lostandfound.util.Constants;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "chatRoom")
@EntityListeners(AuditingEntityListener.class)
public class ChatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_idx", nullable = false)
    private ChatRoomEntity chatRoom;

    @Column(nullable = false)
    private String sender;

    @Column(nullable = false)
    @CreatedDate
    private LocalDateTime sendDate;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private String status = Constants.CHAT_UNREAD;
}
