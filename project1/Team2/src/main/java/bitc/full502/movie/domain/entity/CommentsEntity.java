package bitc.full502.movie.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EntityListeners(AuditingEntityListener.class)
public class CommentsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private int contentId;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    @CreatedDate
    private LocalDateTime commentDate;

    @Column
    @LastModifiedDate
    private LocalDateTime updateDate;

    @Column(nullable = false, length = 500)
    private String comment = "";

    @Column(nullable = false)
    private int rating = 0;

    @Column(nullable = false)
    private String userName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private UserEntity user;

    @JsonProperty("userId")
    public String getUserId() {
        return user != null ? user.getId() : null;
    }
}
