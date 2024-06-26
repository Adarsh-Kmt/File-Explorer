package kamathadarsh.FileExplorer.service;

import com.nimbusds.jose.proc.SecurityContext;
import kamathadarsh.Conduit.jooq.jooqGenerated.tables.records.FileTableRecord;
import kamathadarsh.FileExplorer.JOOQRepository.JOOQFileRepository;
import kamathadarsh.FileExplorer.JOOQRepository.JOOQFolderRepository;
import kamathadarsh.FileExplorer.JOOQRepository.JOOQUserRepository;
import kamathadarsh.FileExplorer.request.CopyPasteRequest;
import kamathadarsh.FileExplorer.request.CreateFileRequest;
import kamathadarsh.FileExplorer.response.CustomResponse;
import kamathadarsh.FileExplorer.response.FailureResponse;
import kamathadarsh.FileExplorer.response.SuccessResponse;
import kamathadarsh.FileExplorer.security.DTO.CustomGrantedAuthority;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@AllArgsConstructor
@Service
public class FileService {

    private final JOOQFileRepository fileRepository;
    private final JOOQUserRepository userRepository;

    private final JOOQFolderRepository folderRepository;
    private final PathFinder pathFinder;

    public CustomResponse createFile(CreateFileRequest createFileRequest){

        boolean fileExists = fileRepository.checkIfFileExistsInCurrentFolder(createFileRequest.getFileName(), FolderService.currentFolderId);

        if(fileExists){

            return FailureResponse.builder()
                    .errorString("file with name " + createFileRequest.getFileName() + " already exists in current folder")
                    .errorStatus(HttpStatus.CONFLICT)
                    .build();
        }

        String owner_username = SecurityContextHolder.getContext().getAuthentication().getName();



        fileRepository.createFile(createFileRequest, owner_username);

        String filePath = pathFinder.determinePathFromRootFolder(createFileRequest.getFileName(), FolderService.currentFolderId);

        fileRepository.setFilePath(createFileRequest.getFileName(), FolderService.currentFolderId, filePath);

        return SuccessResponse.builder()
                .successMessage("file has been created successfully")
                .httpStatus(HttpStatus.OK)
                .build();
    }

    public CustomResponse checkPermissions(String fileName, char operation, int parentFolderId){

        String filePermissions = fileRepository.getFilePermissions(fileName, parentFolderId);

        String ownerUsername = fileRepository.getFileOwnerUsername(fileName, parentFolderId);

        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();

        int permissionIndex = 0;
        if(operation == 'w') permissionIndex++;
        else if(operation == 'x') permissionIndex += 2;
        if(currentUser.equals(ownerUsername)){

            if(filePermissions.charAt(permissionIndex) == operation){

                return SuccessResponse.builder()
                        .successMessage("you are authorized to perform this operation.")
                        .httpStatus(HttpStatus.OK)
                        .build();
            }
            else{

                return FailureResponse.builder()
                        .errorString("you are not authorized to perform this operation")
                        .errorStatus(HttpStatus.FORBIDDEN)
                        .build();
            }
        }
        Collection<? extends GrantedAuthority> currentUserAuthorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        String currentUserGroup = "DEFAULT";
        for(GrantedAuthority authority : currentUserAuthorities){
            currentUserGroup = authority.getAuthority();
        }

        String ownerGroup = userRepository.getGroupName(ownerUsername);

        System.out.println("current users group " + currentUserGroup);
        System.out.println("owner group: " + ownerGroup);

        if(currentUserGroup.equals(ownerGroup)){

            if(filePermissions.charAt(permissionIndex+3) == operation){

                return SuccessResponse.builder()
                        .successMessage("you are authorized to perform this operation.")
                        .httpStatus(HttpStatus.OK)
                        .build();
            }

            else{

                return FailureResponse.builder()
                        .errorString("you are not authorized to perform this operation")
                        .errorStatus(HttpStatus.FORBIDDEN)
                        .build();
            }
        }

        if(filePermissions.charAt(permissionIndex+6) == operation){
            return SuccessResponse.builder()
                    .successMessage("you are authorized to perform this operation.")
                    .httpStatus(HttpStatus.OK)
                    .build();

        }

        else{

            return FailureResponse.builder()
                    .errorString("you are not authorized to perform this operation")
                    .errorStatus(HttpStatus.FORBIDDEN)
                    .build();
        }



    }
    public CustomResponse readFile(String fileName, String folderName){


        Optional<Integer> folderExists = folderRepository.findFolder(folderName);

        if(folderExists.isEmpty()){

            return FailureResponse.builder()
                    .errorString("file: " + fileName + " not found in current folder.")
                    .errorStatus(HttpStatus.NOT_FOUND)
                    .build();
        }

        int folderId = folderExists.get();

        boolean fileExists = fileRepository.checkIfFileExistsInCurrentFolder(fileName, folderId);

        if(!fileExists){

            return FailureResponse.builder()
                    .errorString("file " + fileName + " not found in folder " + folderName)
                    .errorStatus(HttpStatus.NOT_FOUND)
                    .build();
        }


        CustomResponse checkFileReadPermissions = checkPermissions(fileName, 'r', folderId);

        if(checkFileReadPermissions instanceof FailureResponse){

            return FailureResponse.builder()
                    .errorString("you are not authorized to read this file")
                    .errorStatus(HttpStatus.FORBIDDEN)
                    .build();
        }

        String fileData = fileRepository.getFileData(fileName, folderId);


        return SuccessResponse.builder()
                .successMessage(fileData)
                .httpStatus(HttpStatus.OK)
                .build();


    }


    public CustomResponse deleteFile(String fileName, String folderName){

        Optional<Integer> folderExists = folderRepository.findFolder(folderName);

        if(folderExists.isEmpty()){

            return FailureResponse.builder()
                    .errorString("file: " + fileName + " not found in current folder.")
                    .errorStatus(HttpStatus.NOT_FOUND)
                    .build();
        }

        int folderId = folderExists.get();

        boolean fileExists = fileRepository.checkIfFileExistsInCurrentFolder(fileName, folderId);

        if(!fileExists){

            return FailureResponse.builder()
                    .errorString("file: " + fileName + " not found in current folder.")
                    .errorStatus(HttpStatus.NOT_FOUND)
                    .build();
        }

        CustomResponse hasPermissionToDelete = checkPermissions(fileName, 'w', folderId);

        if(hasPermissionToDelete instanceof FailureResponse){

            return FailureResponse.builder()
                    .errorStatus(HttpStatus.FORBIDDEN)
                    .errorString("you are not authorized to delete this file")
                    .build();
        }

        fileRepository.deleteFileInFolder(fileName, FolderService.currentFolderId);

        return FailureResponse.builder()
                .errorString("file " + fileName + " has been deleted from folder " + folderName)
                .build();
    }

    public CustomResponse getFilePath(String fileName, String folderName){

        Optional<Integer> folderExists = folderRepository.findFolder(folderName);

        if(folderExists.isEmpty()){


            return FailureResponse.builder()
                    .errorString("folder does not exist.")
                    .errorStatus(HttpStatus.NOT_FOUND)

                    .build();
        }
        int parentFolderId = folderExists.get();
        boolean fileExists = fileRepository.checkIfFileExistsInCurrentFolder(fileName, parentFolderId);

        if(!fileExists){

            return FailureResponse.builder()
                    .errorString("file: " + fileName + " not found in current folder.")
                    .errorStatus(HttpStatus.NOT_FOUND)
                    .build();
        }
        
        String filePath = pathFinder.determinePathFromRootFolder(fileName, parentFolderId);

        return SuccessResponse.builder()
                .successMessage(filePath)
                .httpStatus(HttpStatus.OK)
                .build();
    }

    public CustomResponse copyPasteFile(CopyPasteRequest copyPasteRequest){

        String sourceFolder = copyPasteRequest.getSourceFolderName();
        String destinationFolder = copyPasteRequest.getDestinationFolderName();
        String fileName = copyPasteRequest.getFileName();

        // check if source and destination folders exist.
        Optional<Integer> sourceFolderExists = folderRepository.findFolder(sourceFolder);
        Optional<Integer> destinationFolderExists = folderRepository.findFolder(destinationFolder);

        if(sourceFolderExists.isEmpty()){

            return FailureResponse.builder()
                    .errorString("folder with name " + sourceFolder + " does not exist")
                    .errorStatus(HttpStatus.NOT_FOUND)
                    .build();
        }

        if(destinationFolderExists.isEmpty()){

            return FailureResponse.builder()
                    .errorString("folder with name " + destinationFolder + " does not exist")
                    .errorStatus(HttpStatus.NOT_FOUND)
                    .build();
        }


        int sourceFolderId = sourceFolderExists.get();
        int destinationFolderId = destinationFolderExists.get();

        // check if file exists.
        boolean fileExists = fileRepository.checkIfFileExistsInCurrentFolder(fileName, sourceFolderId);

        if(!fileExists){
            return FailureResponse.builder()
                    .errorString("file with name " + fileName + " does not exist in folder " + sourceFolder)
                    .errorStatus(HttpStatus.NOT_FOUND)
                    .build();
        }

        // check if a file with same name already exists in destination folder
        boolean fileExistsInDestinationFolder = fileRepository.checkIfFileExistsInCurrentFolder(fileName, destinationFolderId);

        if(fileExistsInDestinationFolder){

            return FailureResponse.builder()
                    .errorString("file with name " + fileName + " already exists in destination folder " + destinationFolder)
                    .errorStatus(HttpStatus.CONFLICT)
                    .build();
        }

        // get original file info from database.
        FileTableRecord originalFileRecord = fileRepository.getFile(fileName, sourceFolderId);

        // create copy of the original file in destination folder
        fileRepository.copyPasteFile(originalFileRecord, destinationFolderId);

        // calculate path of duplicate file.
        String filePath = pathFinder.determinePathFromRootFolder(fileName, destinationFolderId);

        // update duplicate file path.
        fileRepository.setFilePath(fileName, destinationFolderId, filePath);

        return SuccessResponse.builder()
                .successMessage("duplicate file has been created in destination folder " + destinationFolder)
                .httpStatus(HttpStatus.OK)
                .build();
    }



}
