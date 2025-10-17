package bitc.full502.springproject_team1.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "review")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"order", "product"})
public class ReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_idx", nullable = false)
    private Integer reviewIdx;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "order_idx_review")
    private OrderEntity order;
//    원래 orderIdx 였음

    @Column(name="star")
    private Integer reviewStar;

    @CreatedDate
    @Column(name = "review_date", nullable = false)
    private LocalDateTime reviewDate;

    @Column(name = "review_photo", length = 500)
    private String reviewPhoto;

    @Column(name = "content" , length = 500)
    private String reviewContent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "p_id", nullable = false)
    private ProductEntity product;
//    productId 원래 이거였음
}
