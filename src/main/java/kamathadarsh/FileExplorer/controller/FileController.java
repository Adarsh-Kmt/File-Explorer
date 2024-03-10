package kamathadarsh.FileExplorer.controller;

import kamathadarsh.FileExplorer.request.CreateFileRequest;
import kamathadarsh.FileExplorer.response.CustomResponse;
import kamathadarsh.FileExplorer.response.FailureResponse;
import kamathadarsh.FileExplorer.service.FileService;
import lombok.AllArgsConstructor;
import org.apache.coyote.Response;
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

    @GetMapping("/app/file/readFile/{fileName}")
    public ResponseEntity<CustomResponse> readFile(@PathVariable("fileName") String fileName){

        CustomResponse responseToRequest = fileService.readFile(fileName);

        HttpStatus statusOfRequest = (responseToRequest instanceof FailureResponse) ? HttpStatus.FORBIDDEN: HttpStatus.OK;

        return ResponseEntity.status(statusOfRequest).body(responseToRequest);
    }

    @DeleteMapping("/app/file/deleteFile/{fileName}")
    public ResponseEntity<CustomResponse> deleteFile(@PathVariable("fileName") String fileName){

        CustomResponse responseToRequest = fileService.deleteFile(fileName);

        HttpStatus statusOfRequest = (responseToRequest instanceof FailureResponse) ? HttpStatus.FORBIDDEN: HttpStatus.OK;

        return ResponseEntity.status(statusOfRequest).body(responseToRequest);
    }


}
