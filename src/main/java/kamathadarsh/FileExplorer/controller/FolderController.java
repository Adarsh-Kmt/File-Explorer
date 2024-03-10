package kamathadarsh.FileExplorer.controller;

import kamathadarsh.FileExplorer.request.CreateFolderRequest;
import kamathadarsh.FileExplorer.response.CustomResponse;
import kamathadarsh.FileExplorer.response.FailureResponse;
import kamathadarsh.FileExplorer.service.FolderService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class FolderController {


    private final FolderService folderService;

    @GetMapping("/app/folder/cd/{folderName}")
    public ResponseEntity<CustomResponse> changeDirectory(@PathVariable("folderName") String folderName){
        CustomResponse responseToRequest = folderService.changeDirectory(folderName);

        HttpStatus statusOfRequest = (responseToRequest instanceof FailureResponse) ? HttpStatus.NOT_FOUND : HttpStatus.OK;

        return ResponseEntity.status(statusOfRequest).body(responseToRequest);
    }


    @PostMapping("/app/folder/mkdir")
    public ResponseEntity<CustomResponse> createDirectory(@RequestBody CreateFolderRequest request){

        CustomResponse responseToRequest = folderService.createFolder(request.getFolderName());

        HttpStatus statusOfRequest = (responseToRequest instanceof FailureResponse) ? HttpStatus.CONFLICT : HttpStatus.OK;

        return ResponseEntity.status(statusOfRequest).body(responseToRequest);
    }



    @DeleteMapping("/app/folder/delete/{folderName}")
    public ResponseEntity<CustomResponse> deleteFolder(@PathVariable("folderName") String folderName){

        CustomResponse responseToRequest = folderService.deleteFolder(folderName);

        HttpStatus statusOfRequest = (responseToRequest instanceof FailureResponse) ? HttpStatus.CONFLICT : HttpStatus.OK;

        return ResponseEntity.status(statusOfRequest).body(responseToRequest);
    }


}
