package kamathadarsh.FileExplorer.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserLoginRequest {

    public String username;
    public String password;
}
