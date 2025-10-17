package bitc.full502.lostandfound.domain.entity;

import bitc.full502.lostandfound.util.Constants;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "board")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "user")
@EntityListeners(AuditingEntityListener.class)
public class BoardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private CategoryEntity category;

    @Column(nullable = false)
    private String title;

    @Column
    private String imgUrl = "";

    @Column
    private String ownerName = "";

    @Column
    private String description = "";

    @Column(nullable = false)
    private LocalDateTime eventDate;

    @Column(nullable = false)
    private Double eventLat;

    @Column(nullable = false)
    private Double eventLng;

    @Column
    private String eventDetail = "";

    @Column
    private String storageLocation = "";

    @Column(nullable = false)
    private String type; // "LOST" or "FOUND"

    @Column(nullable = false)
    private String status = Constants.STATUS_PENDING; // "PENDING" or "COMPLETE" or "CANCEL"

    @Column(nullable = false)
    @CreatedDate
    private LocalDateTime createDate;
}
