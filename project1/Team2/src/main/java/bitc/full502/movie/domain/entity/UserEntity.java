package bitc.full502.movie.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EntityListeners(AuditingEntityListener.class)
public class UserEntity {

    @Id
    @Column(length = 50)
    private String id;

    @Column(nullable = false, length = 50)
    private String password;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false, unique = true, length = 40)
    private String email;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Column(nullable = false)
    private char gender = 'N';

    @Column(nullable = false)
    private String profileImg = "/images/profile_male4.png";

    @Column(nullable = false)
    @CreatedDate
    private LocalDateTime createDate;

    @Column(nullable = false)
    private char admin = 'N';

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @JsonIgnore
    private List<FavoritesEntity> favorites = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @JsonIgnore
    private List<PreferGenreEntity> preferGenres = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<CommentsEntity> comments = new ArrayList<>();
}
