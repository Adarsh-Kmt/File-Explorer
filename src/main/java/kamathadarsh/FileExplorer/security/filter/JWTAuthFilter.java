package kamathadarsh.FileExplorer.security.filter;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kamathadarsh.FileExplorer.security.DTO.CustomUserDetails;
import kamathadarsh.FileExplorer.security.service.CustomUserDetailsService;
import kamathadarsh.FileExplorer.security.utils.JWTUtils;
import lombok.AllArgsConstructor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@AllArgsConstructor
@Component
public class JWTAuthFilter extends OncePerRequestFilter {
    private final JWTUtils jwtUtils;

    private final CustomUserDetailsService customUserDetailsService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {


        try{

            String JWTToken = parseJWT(request);
            System.out.println("JWT token has been received");
            if(JWTToken != null && jwtUtils.validateJwtToken(JWTToken)){

                String username = jwtUtils.getUsernameFromJwtToken(JWTToken);

                CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(username);

                System.out.println("jwt has been validated.");
                System.out.println("userDetails: " + userDetails.getUsername() + ", authorities " + userDetails.getAuthorities().toString());
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
                        userDetails.getAuthorities());

                System.out.println("Is user authenticated? " + authentication.isAuthenticated());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);

            }

        }
        catch(Exception e){
            System.out.println("error in authentication.");
        }

        System.out.println("sending request to next filter");
        filterChain.doFilter(request, response);



    }

    private String parseJWT(HttpServletRequest request){

        String authorizationHeader = request.getHeader("Authorization");

        System.out.println("");
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){

            String JWTToken = authorizationHeader.substring(7, authorizationHeader.length());
            return JWTToken;

        }

        return null;
    }
}
