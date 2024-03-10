package kamathadarsh.FileExplorer.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Builder
@AllArgsConstructor
@Data
@NoArgsConstructor
public class SuccessResponse extends CustomResponse{

    private String successMessage;
    private HttpStatus httpStatus;
}
