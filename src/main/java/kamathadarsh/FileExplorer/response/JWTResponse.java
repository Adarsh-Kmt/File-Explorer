package kamathadarsh.FileExplorer.response;

import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@AllArgsConstructor
@Builder
public class JWTResponse {

    public String jwtToken;
    public List<String> roles;
}
