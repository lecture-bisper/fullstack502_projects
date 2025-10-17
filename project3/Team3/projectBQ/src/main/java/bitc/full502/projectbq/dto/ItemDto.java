package bitc.full502.projectbq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDto {

    //    비품 List Dto (20250917 완료)
    private Long id;

    private Long categoryId;

    private String categoryName;

    private String code = "";

    private String name;

    private String manufacturer;

    private Long price;

    private Long allQuantity = 0L;

    private LocalDateTime addDate;

    private String addUser;

    private String approveUser = "";

    private String addUserName;

    private String approveUserName;

    private String status;
}
