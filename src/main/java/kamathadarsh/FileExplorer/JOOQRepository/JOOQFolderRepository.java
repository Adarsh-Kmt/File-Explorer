package kamathadarsh.FileExplorer.JOOQRepository;

import kamathadarsh.Conduit.jooq.jooqGenerated.tables.records.FileTableRecord;
import kamathadarsh.Conduit.jooq.jooqGenerated.tables.records.FolderTableRecord;

import kamathadarsh.FileExplorer.service.FolderService;
import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import static kamathadarsh.Conduit.jooq.jooqGenerated.tables.FolderTable.FOLDER_TABLE;
@Repository
@AllArgsConstructor

public class JOOQFolderRepository {

    public DSLContext dslContext;

    public Optional<Integer> findFolder(String folderName){

        return Optional.ofNullable(dslContext.select(FOLDER_TABLE.FOLDER_ID)
                        .from(FOLDER_TABLE)
                .where(FOLDER_TABLE.FOLDER_NAME.eq(folderName))
                .fetchOneInto(Integer.class));
    }

    public void createFolder(String folderName){

        dslContext.insertInto(FOLDER_TABLE)
                .set(FOLDER_TABLE.FOLDER_NAME, folderName)
                .set(FOLDER_TABLE.FOLDER_SIZE, 0L)
                .set(FOLDER_TABLE.PARENT_FOLDER_ID, FolderService.currentFolderId)
                .execute();


    }

    public List<Integer> allSubFoldersInFolder(int folderId){

        return dslContext.select(FOLDER_TABLE.FOLDER_ID)
                .from(FOLDER_TABLE)
                .where(FOLDER_TABLE.PARENT_FOLDER_ID.eq(folderId))
                .fetchInto(Integer.class);
    }

    public void deleteAllSubFoldersInFolder(int folderId){

        dslContext.deleteFrom(FOLDER_TABLE)
                .where(FOLDER_TABLE.PARENT_FOLDER_ID.eq(folderId))
                .execute();


    }

    public void deleteFolder(int folderId){

        dslContext.deleteFrom(FOLDER_TABLE)
                .where(FOLDER_TABLE.FOLDER_ID.eq(folderId))
                .execute();
    }

    public List<FolderTableRecord> allSubFolderPathsInFolder(int parentFolderId){

        List<FolderTableRecord> subFolderList = dslContext.select(FOLDER_TABLE.FOLDER_NAME, FOLDER_TABLE.FOLDER_ID)
                .from(FOLDER_TABLE)
                .where(FOLDER_TABLE.PARENT_FOLDER_ID.eq(parentFolderId))
                .fetchInto(FolderTableRecord.class);

        return subFolderList;
    }


    public List<String> allSubFolderNamesInFolder(int folderId){

        return dslContext.select(FOLDER_TABLE.FOLDER_NAME)
                .from(FOLDER_TABLE)
                .where(FOLDER_TABLE.PARENT_FOLDER_ID.eq(folderId))
                .fetchInto(String.class);
    }



}


