package kamathadarsh.FileExplorer.service;

import kamathadarsh.Conduit.jooq.jooqGenerated.tables.records.FileTableRecord;
import kamathadarsh.Conduit.jooq.jooqGenerated.tables.records.FolderTableRecord;
import kamathadarsh.FileExplorer.DTO.FileDTO;
import kamathadarsh.FileExplorer.DTO.FolderContentsDTO;
import kamathadarsh.FileExplorer.JOOQRepository.JOOQFileRepository;
import kamathadarsh.FileExplorer.JOOQRepository.JOOQFolderRepository;
import kamathadarsh.FileExplorer.JOOQRepository.JOOQUserRepository;
import kamathadarsh.FileExplorer.response.CustomResponse;
import kamathadarsh.FileExplorer.response.FailureResponse;
import kamathadarsh.FileExplorer.response.FolderContentResponse;
import kamathadarsh.FileExplorer.response.SuccessResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class FolderService {

    public static int currentFolderId = 1;

    private final JOOQFolderRepository folderRepository;

    private final JOOQUserRepository userRepository;
    private final JOOQFileRepository fileRepository;
    public CustomResponse changeDirectory(String folderName){

        Optional<Integer> folderExists = folderRepository.findFolder(folderName);

        if(folderExists.isPresent()){
            currentFolderId = folderExists.get();

            return SuccessResponse.builder()
                    .httpStatus(HttpStatus.OK)
                    .successMessage("moved to folder " + folderName)
                    .build();
        }


        return FailureResponse.builder()
                .errorString("folder with name " + folderName + " does not exist")
                .errorStatus(HttpStatus.NOT_FOUND)
                .build();


    }

    public CustomResponse createFolder(String folderName){

        Optional<Integer> folderExists = folderRepository.findFolder(folderName);

        if(folderExists.isPresent()){
            return FailureResponse.builder()
                    .errorString("folder already exists, please create folder with different name.")
                    .errorStatus(HttpStatus.CONFLICT)
                    .build();
        }

        folderRepository.createFolder(folderName);

        return SuccessResponse.builder()
                .successMessage("folder has been created")
                .httpStatus(HttpStatus.OK)
                .build();


    }

    public CustomResponse deleteFolder(String folderName){

        if(folderName.equals("root")){

            return FailureResponse.builder()
                    .errorString("cannot delete root folder")
                    .errorStatus(HttpStatus.BAD_REQUEST)
                    .build();
        }
        Optional<Integer> folderExists = folderRepository.findFolder(folderName);

        if(folderExists.isEmpty()){
            return FailureResponse.builder()
                    .errorString("folder with name " + folderName + " not found.")
                    .errorStatus(HttpStatus.NOT_FOUND)
                    .build();
        }

        Queue<Integer> foldersToBeDeleted = new LinkedList<>();
        foldersToBeDeleted.add(folderExists.get());

        // BFS Deletion
        while(!foldersToBeDeleted.isEmpty()){

            int currSize = foldersToBeDeleted.size();

            for(int i =0 ; i < currSize; i++){

                int folderId = foldersToBeDeleted.remove();
                List<Integer> childFolders = folderRepository.allSubFoldersInFolder(folderId);
                foldersToBeDeleted.addAll(childFolders);
                fileRepository.deleteAllFilesInAFolder(folderId);
                folderRepository.deleteFolder(folderId);

            }
        }

        return SuccessResponse.builder()
                .httpStatus(HttpStatus.OK)
                .successMessage("all files and sub-folders stored in " + folderName + " have been deleted.")
                .build();
    }


    public CustomResponse listContentsInFolder(String folderName){

        Optional<Integer> folderExists = folderRepository.findFolder(folderName);

        if(folderExists.isEmpty()){
            return FailureResponse.builder()
                    .errorString("folder with name " + folderName + " not found.")
                    .errorStatus(HttpStatus.NOT_FOUND)
                    .build();
        }

        int folderId = folderExists.get();
        List<String> allSubFolders = folderRepository.allSubFolderNamesInFolder(folderId);


        List<FileTableRecord> allFiles = fileRepository.getAllFilesInFolder(folderId);

        List<FileDTO> allFileDTOs = new ArrayList<>();

        for(FileTableRecord file : allFiles){

            String ownerGroupName = userRepository.getGroupName(file.getOwner());
            FileDTO fileDTO = new FileDTO(file, ownerGroupName);
            allFileDTOs.add(fileDTO);
        }

        FolderContentsDTO folderContentsDTO = new FolderContentsDTO(allFileDTOs, allSubFolders);
        FolderContentResponse folderContentResponse = new FolderContentResponse(folderContentsDTO, HttpStatus.OK);

        return folderContentResponse;



    }

}
