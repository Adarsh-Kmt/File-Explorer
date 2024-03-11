package kamathadarsh.FileExplorer.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data

public class FailureResponse extends CustomResponse{

    private String errorString;
    private HttpStatus errorStatus;
}
