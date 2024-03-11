package kamathadarsh.FileExplorer.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class JWTResponse {

    public String jwtToken;
    public List<String> roles;
}
