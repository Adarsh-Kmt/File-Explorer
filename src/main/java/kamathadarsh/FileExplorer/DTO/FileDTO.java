package kamathadarsh.FileExplorer.DTO;

import kamathadarsh.Conduit.jooq.jooqGenerated.tables.records.FileTableRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class FileDTO {

    private String fileName;
    private String permissions;
    private LocalDateTime lastModifiedAt;
    private String ownerUsername;
    private String ownerGroup;


    public FileDTO(FileTableRecord fileTableRecord, String ownerGroupName){

        fileName = fileTableRecord.getFileName();
        permissions = fileTableRecord.getPermissions();
        lastModifiedAt = fileTableRecord.getModifiedAt();
        ownerUsername = fileTableRecord.getOwner();
        ownerGroup = ownerGroupName;

    }
}
