package kamathadarsh.FileExplorer.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUserRequest {

    public String username;
    public String password;
    public String groupName;
}
