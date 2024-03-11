package kamathadarsh.FileExplorer.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data

public class FolderContentsDTO {

    private List<FileDTO> allFilesInFolder;
    private List<String> allSubFoldersInFolder;
}
