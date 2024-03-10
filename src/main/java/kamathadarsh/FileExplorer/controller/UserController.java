package kamathadarsh.FileExplorer.controller;

import kamathadarsh.FileExplorer.request.RegisterUserRequest;
import kamathadarsh.FileExplorer.request.UserLoginRequest;
import kamathadarsh.FileExplorer.response.CustomResponse;
import kamathadarsh.FileExplorer.response.FailureResponse;
import kamathadarsh.FileExplorer.response.JWTResponse;
import kamathadarsh.FileExplorer.security.DTO.CustomUserDetails;
import kamathadarsh.FileExplorer.security.utils.JWTUtils;
import kamathadarsh.FileExplorer.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    private final JWTUtils jwtUtils;

    @PostMapping("/auth/register")
    public ResponseEntity<CustomResponse> registerUser(@RequestBody RegisterUserRequest request){

        CustomResponse responseToRequest = userService.registerUser(request);

        HttpStatus statusOfRequest = (responseToRequest instanceof FailureResponse) ? HttpStatus.CONFLICT : HttpStatus.OK;

        return ResponseEntity.status(statusOfRequest).body(responseToRequest);


    }

    @PostMapping("/auth/login")
    public ResponseEntity<JWTResponse> loginExistingUser(@RequestBody UserLoginRequest loginRequest) throws Exception {

        System.out.println("reached controller");
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String jwt = jwtUtils.generateJwtToken(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(new JWTResponse(jwt, roles));
    }


}
