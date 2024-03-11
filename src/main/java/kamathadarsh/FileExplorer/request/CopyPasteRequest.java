package kamathadarsh.FileExplorer.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CopyPasteRequest {

    private String sourceFolderName;
    private String destinationFolderName;
    private String fileName;
}
