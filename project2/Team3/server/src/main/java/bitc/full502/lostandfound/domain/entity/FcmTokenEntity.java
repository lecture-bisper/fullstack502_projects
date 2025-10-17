package bitc.full502.lostandfound.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "fcm_token")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class FcmTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private String deviceId;

    @Column(nullable = false)
    @CreatedDate
    private LocalDateTime createDate;

    public FcmTokenEntity(String userId, String token, String deviceId) {
        this.userId = userId;
        this.token = token;
        this.deviceId = deviceId;
    }
}
