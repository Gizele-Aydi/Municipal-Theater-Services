package org.example.municipaltheater.security;

import org.example.municipaltheater.services.RegisteredUsersServices.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final UsersService UserService;

    @Autowired
    public SecurityConfig(UsersService userService) {
        UserService = userService;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return UserService;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(UserService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(httpForm -> {
                    httpForm.loginPage("/LogIn").permitAll();
                    httpForm.defaultSuccessUrl("/Home", true);
                })
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login/oauth2/code/google")
                        .defaultSuccessUrl("/Home", true)
                )
                .authorizeHttpRequests(registry -> {
                    registry.requestMatchers("/Shows/{id}/book", "/Shows/{id}/pay", "/Users/{id}","/Users/All","/Users/Update/{id}","/Users/Delete/{id}","/Home", "/About", "/SignUp", "/LogIn", "/Events/All", "/Events/{id}","/Events/Update/{id}","/Events/Delete/{id}", "/Shows/All", "/Shows/{id}","/Shows/Update/{id}","/Shows/Delete/{id}").permitAll();
                    registry.requestMatchers("/favicon.ico").permitAll();
                    registry.requestMatchers("/Profile").authenticated();
                    registry.anyRequest().authenticated();
                })
                .oauth2Login(withDefaults())
                .build();
    }

}
