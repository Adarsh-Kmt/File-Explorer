package kamathadarsh.FileExplorer.service;

import kamathadarsh.FileExplorer.JOOQRepository.JOOQGroupRepository;
import kamathadarsh.FileExplorer.JOOQRepository.JOOQUserRepository;
import kamathadarsh.FileExplorer.request.RegisterUserRequest;
import kamathadarsh.FileExplorer.response.CustomResponse;
import kamathadarsh.FileExplorer.response.FailureResponse;
import kamathadarsh.FileExplorer.response.SuccessResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private final JOOQUserRepository userRepository;

    private final JOOQGroupRepository groupRepository;
    public CustomResponse registerUser(RegisterUserRequest request){

        if(userRepository.userExists(request.getUsername())){

            return FailureResponse.builder()
                    .errorString("user with username " + request.getUsername() + " already exists")
                    .errorStatus(HttpStatus.CONFLICT)
                    .build();
        }

        if(request.getGroupName() == null) request.setGroupName("DEFAULT");

        groupRepository.checkIfGroupExists(request.getGroupName());
        userRepository.registerNewUser(request);
        return SuccessResponse.builder()
                .successMessage("you have registered")
                .httpStatus(HttpStatus.OK)
                .build();
    }
}
