package kamathadarsh.FileExplorer.JOOQRepository;

import kamathadarsh.FileExplorer.controller.FileController;
import kamathadarsh.FileExplorer.request.CreateFileRequest;
import kamathadarsh.FileExplorer.service.FolderService;
import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static kamathadarsh.Conduit.jooq.jooqGenerated.tables.FileTable.FILE_TABLE;
@Repository
@AllArgsConstructor
public class JOOQFileRepository {

    private final DSLContext dslContext;

    public void deleteAllFilesInAFolder(int folderId){

        dslContext.deleteFrom(FILE_TABLE)
                .where(FILE_TABLE.PARENT_FOLDER_ID.eq(folderId))
                .execute();
    }

    public boolean checkIfFileExistsInCurrentFolder(String fileName, int folderId){

        return dslContext.fetchExists(dslContext.select(FILE_TABLE.FILE_ID)
                .from(FILE_TABLE)
                .where(FILE_TABLE.FILE_NAME.eq(fileName))
                .and(FILE_TABLE.PARENT_FOLDER_ID.eq(folderId)));
    }

    public void createFile(CreateFileRequest createFileRequest, String ownerUsername){

        dslContext.insertInto(FILE_TABLE)
                .set(FILE_TABLE.FILE_NAME, createFileRequest.getFileName())
                .set(FILE_TABLE.PERMISSIONS, createFileRequest.getFilePermissions())
                .set(FILE_TABLE.CREATED_AT, LocalDateTime.now())
                .set(FILE_TABLE.ACCESSED_AT, LocalDateTime.now())
                .set(FILE_TABLE.MODIFIED_AT, LocalDateTime.now())
                .set(FILE_TABLE.PARENT_FOLDER_ID, FolderService.currentFolderId)
                .set(FILE_TABLE.PATH, "///")
                .set(FILE_TABLE.FILE_SIZE, 0L)
                .set(FILE_TABLE.IN_RECYCLE_BIN, "F")
                .set(FILE_TABLE.OWNER, ownerUsername)
                .execute();

    }


    public String getFilePermissions(String fileName, int parentFolderId){

        return dslContext.select(FILE_TABLE.PERMISSIONS)
                .from(FILE_TABLE)
                .where(FILE_TABLE.FILE_NAME.eq(fileName))
                .and(FILE_TABLE.PARENT_FOLDER_ID.eq(parentFolderId))
                .fetchOneInto(String.class);
    }


    public String getFileOwnerUsername(String fileName, int parentFolderId){

        return dslContext.select(FILE_TABLE.OWNER)
                .from(FILE_TABLE)
                .where(FILE_TABLE.FILE_NAME.eq(fileName))
                .and(FILE_TABLE.PARENT_FOLDER_ID.eq(parentFolderId))
                .fetchOneInto(String.class);
    }


    public void deleteFileInFolder(String fileName, int parentFolderId){

        dslContext.deleteFrom(FILE_TABLE)
                .where(FILE_TABLE.FILE_NAME.eq(fileName))
                .and(FILE_TABLE.PARENT_FOLDER_ID.eq(parentFolderId))
                .execute();
    }


}
