package bitc.full502.movie.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.action.internal.OrphanRemovalAction;
import org.hibernate.annotations.Cascade;

@Entity
@Table(name = "prefer_genre")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PreferGenreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id", nullable = false)
    @JsonIgnore
    private GenreEntity genre;
}
