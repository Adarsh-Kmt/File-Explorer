package kamathadarsh.FileExplorer.security.config;

import kamathadarsh.FileExplorer.security.filter.JWTAuthFilter;
import kamathadarsh.FileExplorer.security.service.CustomUserDetailsService;
import kamathadarsh.FileExplorer.security.utils.JWTUtils;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity(debug = true)
@Configuration
@AllArgsConstructor
public class SecurityConfig {

    //private final RsaKeyProperties rsaKeys;

    private final CustomUserDetailsService customUserDetailsService;

    private final JWTAuthenticationEntryPoint unauthorizedHandler;

    private final JWTUtils jwtUtils;

    @Bean
    public JWTAuthFilter jwtTokenFilter(){

        return new JWTAuthFilter(jwtUtils, customUserDetailsService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        {
                            auth.requestMatchers("/auth/**").permitAll();
                            auth.requestMatchers("/app/**").authenticated();
                        }
                );
                //.httpBasic().disable();


        //.formLogin(Customizer.withDefaults())
        //.httpBasic(Customizer.withDefaults());

        http.authenticationProvider(authenticationProvider());

        http.addFilterBefore(jwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){

        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){

        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setPasswordEncoder(passwordEncoder());
        authProvider.setUserDetailsService(customUserDetailsService);

        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {

        return authConfig.getAuthenticationManager();
    }
//    @Bean
//    public JwtEncoder JwtEncoder(){
//        JWK jwk  = new RSAKey.Builder(rsaKeys.publicKey()).privateKey(rsaKeys.privateKey()).build();
//        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
//        return new NimbusJwtEncoder(jwks);
//    }
//
//    @Bean
//    public JwtDecoder JwtDecoder(){
//
//        return NimbusJwtDecoder.withPublicKey(rsaKeys.publicKey()).build();
//    }
}
