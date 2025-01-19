package org.example.municipaltheater.security;

import jakarta.servlet.http.HttpServletResponse;
import org.example.municipaltheater.security.jwt.AuthEntryPointJWT;
import org.example.municipaltheater.security.jwt.AuthTokenFilter;
import org.example.municipaltheater.security.services.UserDetailsImpl;
import org.example.municipaltheater.services.AuthenticationServices.UserDetailsServiceImpl;
import org.example.municipaltheater.services.RegisteredUsersServices.UsersService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
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
    private final UserDetailsServiceImpl UserService;

    @Autowired
    public SecurityConfig(AuthEntryPointJWT unAuthHandler, UserDetailsServiceImpl userService) {
        UnAuthHandler = unAuthHandler;
        UserService = userService;
    }

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() { return new AuthTokenFilter(); }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

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
    @Order(1)
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.securityMatcher("/**")
                .csrf(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(UnAuthHandler))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(registry -> {
                    registry.requestMatchers("/signup", "/login", "/events/all", "/events/{id}", "/events/search", "/shows/all", "/shows/{id}", "/shows/search").permitAll();
                    registry.requestMatchers("/favicon.ico").permitAll();
                    registry.requestMatchers("/logout","/profile/**", "/shows/{id}/book", "/shows/{id}/pay","/shows/add", "/shows/update/{id}","/shows/delete/{id}","/events/update/{id}","/events/delete/{id}","/users/**").authenticated();
                    registry.anyRequest().authenticated();
                })
                .logout(logout -> logout
                        .logoutUrl("/logout") // Customize logout endpoint
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_OK);
                            response.getWriter().write("Logged out successfully");
                        }))
                .authenticationProvider(authenticationProvider());
                httpSecurity.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain oauth2FilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/login/oauth2/**", "/oauth2/**")
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login/oauth2/code/google")
                        .defaultSuccessUrl("/", true)
                ).authorizeHttpRequests(auth -> auth.anyRequest().authenticated());
        return http.build();
    }

}
