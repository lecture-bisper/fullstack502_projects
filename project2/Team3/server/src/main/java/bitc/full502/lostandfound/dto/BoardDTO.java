package bitc.full502.lostandfound.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardDTO {

    private Long idx;

    private String userId;

    private int categoryId;

    private String title;

    private String imgUrl = "";

    private String ownerName = "";

    private String description = "";

    private LocalDateTime eventDate;

    private Double eventLat;

    private Double eventLng;

    private String eventDetail = "";

    private String storageLocation = "";

    private String type; // "LOST" or "FOUND"

    private String status; // "PENDING" or "COMPLETE" or "CANCEL"

    private LocalDateTime createDate;

}
