package kamathadarsh.FileExplorer.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class CreateFileRequest {

    public String fileName;
    public String filePermissions;
}
