package bitc.full502.movie.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class SearchDTO extends BaseDTO {

    String keyword;

    String sort;

    String country;

    String genre;

    String type;

    String pageNum;

    LocalDate startDate;

    LocalDate endDate;
}
