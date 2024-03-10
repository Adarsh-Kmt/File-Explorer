package kamathadarsh.FileExplorer.security.service;

import kamathadarsh.Conduit.jooq.jooqGenerated.tables.pojos.UserTable;
import kamathadarsh.FileExplorer.JOOQRepository.JOOQGroupRepository;
import kamathadarsh.FileExplorer.JOOQRepository.JOOQUserRepository;
import kamathadarsh.FileExplorer.security.DTO.CustomGrantedAuthority;
import kamathadarsh.FileExplorer.security.DTO.CustomUserDetails;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final JOOQUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        System.out.println("searching for user");
        Optional<UserTable> user = userRepository.findUser(username);

        if(user.isEmpty()){
            System.out.println("user not found");
            throw new UsernameNotFoundException("user with username " + username + " not found");
        }
        System.out.println("user with username " + user.get().getUsername() + " was found.");

        CustomGrantedAuthority customGrantedAuthority = new CustomGrantedAuthority(user.get().getUserGroupName());
        return new CustomUserDetails(user.get(), List.of(customGrantedAuthority));
    }
}
