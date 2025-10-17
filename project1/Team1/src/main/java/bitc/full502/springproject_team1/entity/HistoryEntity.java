package bitc.full502.springproject_team1.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name="history")
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@ToString(callSuper = true)
public class HistoryEntity {
    @Id
    @Column(name = "history_idx", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer historyIdx;

    @Column(name = "p_id_history", nullable = false)
    private Integer productIdx;

    @Column(name = "c_id_history", nullable = false)
    private Integer customerId;

    @CreatedDate
    @Column(name="history_date", nullable = false)
    private LocalDateTime historyDate;
}
