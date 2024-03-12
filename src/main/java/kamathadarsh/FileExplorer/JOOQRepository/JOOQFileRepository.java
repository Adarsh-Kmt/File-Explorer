package kamathadarsh.FileExplorer.JOOQRepository;

import kamathadarsh.Conduit.jooq.jooqGenerated.tables.records.FileTableRecord;
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
                .set(FILE_TABLE.FILE_DATA, createFileRequest.getFileData())
                .set(FILE_TABLE.IN_RECYCLE_BIN, "F")
                .set(FILE_TABLE.OWNER, ownerUsername)
                .execute();

    }


    public String getFileData(String fileName, int parentFolderId){


        return dslContext.select(FILE_TABLE.FILE_DATA)
                .from(FILE_TABLE)
                .where(FILE_TABLE.FILE_NAME.eq(fileName))
                .and(FILE_TABLE.PARENT_FOLDER_ID.eq(parentFolderId))
                .fetchOneInto(String.class);
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

    public void setFilePath(String fileName, int parentFolderId, String filePath){

        dslContext.update(FILE_TABLE)
                .set(FILE_TABLE.PATH, filePath)
                .where(FILE_TABLE.FILE_NAME.eq(fileName))
                .and(FILE_TABLE.PARENT_FOLDER_ID.eq(parentFolderId))
                .execute();
    }


    public FileTableRecord getFile(String fileName, int parentFolderId){

        return dslContext.select(
                FILE_TABLE.OWNER,
                FILE_TABLE.FILE_NAME,
                FILE_TABLE.FILE_SIZE,
                FILE_TABLE.ACCESSED_AT,
                FILE_TABLE.CREATED_AT,
                FILE_TABLE.MODIFIED_AT,
                FILE_TABLE.PERMISSIONS)
                .from(FILE_TABLE)
                .where(FILE_TABLE.FILE_NAME.eq(fileName))
                .and(FILE_TABLE.PARENT_FOLDER_ID.eq(parentFolderId))
                .fetchOneInto(FileTableRecord.class);
    }


    public void copyPasteFile(FileTableRecord originalFileRecord, int destinationFolderId){

        dslContext.insertInto(FILE_TABLE)
                .set(FILE_TABLE.FILE_NAME, originalFileRecord.getFileName())
                .set(FILE_TABLE.PERMISSIONS, originalFileRecord.getPermissions())
                .set(FILE_TABLE.CREATED_AT, LocalDateTime.now())
                .set(FILE_TABLE.ACCESSED_AT, LocalDateTime.now())
                .set(FILE_TABLE.MODIFIED_AT, LocalDateTime.now())
                .set(FILE_TABLE.PARENT_FOLDER_ID, destinationFolderId)
                .set(FILE_TABLE.PATH, "///")
                .set(FILE_TABLE.FILE_SIZE, 0L)
                .set(FILE_TABLE.IN_RECYCLE_BIN, "F")
                .set(FILE_TABLE.OWNER, originalFileRecord.getOwner())
                .execute();
    }


    public List<FileTableRecord> getAllFilesInFolder(int folderId){

        return dslContext.select(
                FILE_TABLE.OWNER,
                FILE_TABLE.FILE_NAME,
                FILE_TABLE.PATH,
                FILE_TABLE.PERMISSIONS,
                FILE_TABLE.MODIFIED_AT,
                FILE_TABLE.FILE_SIZE)
                .from(FILE_TABLE)
                .where(FILE_TABLE.PARENT_FOLDER_ID.eq(folderId))
                .fetchInto(FileTableRecord.class);
    }


}
