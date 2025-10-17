package bitc.full502.projectbq.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ErrorResponse {

    private int status;
    private String message;
    private List<String> errors;
}
