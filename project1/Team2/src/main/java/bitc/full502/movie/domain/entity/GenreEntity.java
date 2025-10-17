package bitc.full502.movie.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "genre")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GenreEntity {

    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String krName;
}
