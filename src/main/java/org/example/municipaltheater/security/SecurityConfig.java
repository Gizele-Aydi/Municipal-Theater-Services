package org.example.municipaltheater.security;

import org.example.municipaltheater.security.jwt.AuthEntryPointJWT;
import org.example.municipaltheater.security.jwt.AuthTokenFilter;
import org.example.municipaltheater.services.RegisteredUsersServices.UsersService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final AuthEntryPointJWT UnAuthHandler;
    private final UsersService UserService;

    @Autowired
    public SecurityConfig(AuthEntryPointJWT unAuthHandler, UsersService userService) {
        UnAuthHandler = unAuthHandler;
        UserService = userService;
    }

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() { return new AuthTokenFilter(); }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(UserService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(UnAuthHandler))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(httpForm -> {
                    httpForm.loginPage("/LogIn").permitAll();
                    httpForm.defaultSuccessUrl("/", true);
                })
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login/oauth2/code/google")
                        .defaultSuccessUrl("/", true)
                )
                .authorizeHttpRequests(registry -> {
                    registry.requestMatchers("/SignUp", "/LogIn", "/Events/All", "/Events/{id}", "/Shows/All", "/Shows/{id}").permitAll();
                    registry.requestMatchers("/favicon.ico").permitAll();
                    registry.requestMatchers("/Profile", "/Shows/{id}/Book", "/Shows/{id}/Pay","/Shows/Update/{id}","/Shows/Delete/{id}","/Events/Update/{id}","/Events/Delete/{id}","/Users/All","/Users/{id}","/Users/Update/{id}","/Users/Delete/{id}").authenticated();
                    registry.anyRequest().authenticated();
                })
                .oauth2Login(withDefaults());
        httpSecurity.authenticationProvider(authenticationProvider()); // Set the authentication provider
        httpSecurity.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

}
