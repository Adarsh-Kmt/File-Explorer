package kamathadarsh.FileExplorer.response;

import kamathadarsh.FileExplorer.DTO.FolderContentsDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder

public class FolderContentResponse extends CustomResponse{

    public FolderContentsDTO folderContentsDTO;
    public HttpStatus httpStatus;
}
