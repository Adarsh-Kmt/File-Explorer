package kamathadarsh.FileExplorer.service;

import com.nimbusds.jose.proc.SecurityContext;
import kamathadarsh.FileExplorer.JOOQRepository.JOOQFileRepository;
import kamathadarsh.FileExplorer.JOOQRepository.JOOQUserRepository;
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
import java.util.List;

@AllArgsConstructor
@Service
public class FileService {

    private final JOOQFileRepository fileRepository;
    private final JOOQUserRepository userRepository;

    public CustomResponse createFile(CreateFileRequest createFileRequest){

        boolean fileExists = fileRepository.checkIfFileExistsInCurrentFolder(createFileRequest.getFileName(), FolderService.currentFolderId);

        if(!fileExists){

            return FailureResponse.builder()
                    .errorString("file with name " + createFileRequest.getFileName() + " already exists in current folder")
                    .errorStatus(HttpStatus.CONFLICT)
                    .build();
        }

        String owner_username = SecurityContextHolder.getContext().getAuthentication().getName();
        fileRepository.createFile(createFileRequest, owner_username);

        return SuccessResponse.builder()
                .successMessage("file has been created successfully")
                .httpStatus(HttpStatus.OK)
                .build();
    }

    public CustomResponse readFile(String fileName){

        boolean fileExists = fileRepository.checkIfFileExistsInCurrentFolder(fileName, FolderService.currentFolderId);

        if(!fileExists){

            return FailureResponse.builder()
                    .errorString("file: " + fileName + " not found in current folder.")
                    .errorStatus(HttpStatus.NOT_FOUND)
                    .build();
        }

        String filePermissions = fileRepository.getFilePermissions(fileName, FolderService.currentFolderId);

        String ownerUsername = fileRepository.getFileOwnerUsername(fileName, FolderService.currentFolderId);

        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();

        if(currentUser.equals(ownerUsername)){

            if(filePermissions.charAt(0) == 'r'){

                return SuccessResponse.builder()
                        .successMessage("reading....")
                        .httpStatus(HttpStatus.OK)
                        .build();
            }
            else{

                return FailureResponse.builder()
                        .errorString("you are not authorized to read this file")
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

        if(currentUserGroup.equals(ownerGroup)){

            if(filePermissions.charAt(3) == 'r'){

                return SuccessResponse.builder()
                        .successMessage("reading....")
                        .httpStatus(HttpStatus.OK)
                        .build();
            }

            else{

                return FailureResponse.builder()
                        .errorString("you are not authorized to read this file")
                        .errorStatus(HttpStatus.FORBIDDEN)
                        .build();
            }
        }

        if(filePermissions.charAt(6) == 'r'){
            return SuccessResponse.builder()
                    .successMessage("reading....")
                    .httpStatus(HttpStatus.OK)
                    .build();

        }

        else{

            return FailureResponse.builder()
                    .errorString("you are not authorized to read this file")
                    .errorStatus(HttpStatus.FORBIDDEN)
                    .build();
        }




    }


    public CustomResponse deleteFile(String fileName){

        boolean fileExists = fileRepository.checkIfFileExistsInCurrentFolder(fileName, FolderService.currentFolderId);

        if(!fileExists){

            return FailureResponse.builder()
                    .errorString("file: " + fileName + " not found in current folder.")
                    .errorStatus(HttpStatus.NOT_FOUND)
                    .build();
        }

        String ownerUsername = fileRepository.getFileOwnerUsername(fileName, FolderService.currentFolderId);

        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();

        if(currentUser.equals(ownerUsername)){

            fileRepository.deleteFileInFolder(fileName, FolderService.currentFolderId);

            return SuccessResponse.builder()
                    .successMessage("file has been successfully deleted.")
                    .build();
        }

        else{

            return FailureResponse.builder()
                    .errorString("you are not authorized to read this file")
                    .errorStatus(HttpStatus.FORBIDDEN)
                    .build();
        }



    }



}
