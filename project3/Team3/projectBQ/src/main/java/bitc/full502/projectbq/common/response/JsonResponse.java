package bitc.full502.projectbq.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JsonResponse {

    private String message = "No Message";
    private String data = "No Data";

    public JsonResponse(String message) {
        this.message = message;
    }
}

