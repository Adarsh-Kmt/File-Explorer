package kamathadarsh.FileExplorer.controller;

import kamathadarsh.FileExplorer.request.CopyPasteRequest;
import kamathadarsh.FileExplorer.request.CreateFileRequest;
import kamathadarsh.FileExplorer.response.CustomResponse;
import kamathadarsh.FileExplorer.response.FailureResponse;
import kamathadarsh.FileExplorer.service.FileService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@AllArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping("/app/file/createFile")
    public ResponseEntity<CustomResponse> createFile(@RequestBody CreateFileRequest createFileRequest){

        CustomResponse responseToRequest = fileService.createFile(createFileRequest);

        HttpStatus statusOfRequest = (responseToRequest instanceof FailureResponse) ? HttpStatus.CONFLICT : HttpStatus.OK;

        return ResponseEntity.status(statusOfRequest).body(responseToRequest);
    }

    @GetMapping("/app/file/readFile/{fileName}/folder/{folderName}")
    public ResponseEntity<CustomResponse> readFile(@PathVariable("fileName") String fileName,
                                                   @PathVariable("folderName") String folderName){

        CustomResponse responseToRequest = fileService.readFile(fileName, folderName);

        System.out.println(responseToRequest.toString());
        HttpStatus statusOfRequest = (responseToRequest instanceof FailureResponse) ? HttpStatus.FORBIDDEN: HttpStatus.OK;

        return ResponseEntity.status(statusOfRequest).body(responseToRequest);
    }

    @DeleteMapping("/app/file/deleteFile/{fileName}/folder/{folderName}")
    public ResponseEntity<CustomResponse> deleteFile(@PathVariable("fileName") String fileName,
                                                     @PathVariable("folderName") String folderName){

        CustomResponse responseToRequest = fileService.deleteFile(fileName, folderName);

        HttpStatus statusOfRequest = (responseToRequest instanceof FailureResponse) ? HttpStatus.FORBIDDEN: HttpStatus.OK;

        return ResponseEntity.status(statusOfRequest).body(responseToRequest);
    }

    @GetMapping("/app/file/getFilePath/{folderName}/{fileName}")
    public ResponseEntity<CustomResponse> getFilePath(@PathVariable("fileName") String fileName,
                                                      @PathVariable("folderName") String folderName){

        CustomResponse responseToRequest = fileService.getFilePath(fileName, folderName);

        HttpStatus statusOfRequest = (responseToRequest instanceof FailureResponse) ? HttpStatus.FORBIDDEN: HttpStatus.OK;

        return ResponseEntity.status(statusOfRequest).body(responseToRequest);
    }

    @PostMapping("/app/file/copyPaste")
    public ResponseEntity<CustomResponse> copyPaste(@RequestBody CopyPasteRequest copyPasteRequest){

        CustomResponse responseToRequest = fileService.copyPasteFile(copyPasteRequest);

        HttpStatus statusOfRequest = HttpStatus.OK;

        if(responseToRequest instanceof FailureResponse){

            if(((FailureResponse) responseToRequest).getErrorStatus().isSameCodeAs(HttpStatus.CONFLICT)){

                statusOfRequest = HttpStatus.CONFLICT;
            }
            else statusOfRequest = HttpStatus.NOT_FOUND;
        }

        return ResponseEntity.status(statusOfRequest).body(responseToRequest);

    }



}
