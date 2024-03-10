package kamathadarsh.FileExplorer.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FailureResponse extends CustomResponse{

    private String errorString;
    private HttpStatus errorStatus;
}
