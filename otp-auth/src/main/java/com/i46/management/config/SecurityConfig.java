package com.i46.management.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // User Creation
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserDetails admin = User.withUsername("admin")
                .password(encoder.encode("aekUpN8MRw0cXnLC9tjYycnc7Z6Q9S4X"))
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(admin);
    }

    // Configuring HttpSecurity
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests().anyRequest().authenticated()
                .and()
                .httpBasic();

        return http.build();
    }

    // Password Encoding
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    // Configuring HttpSecurity
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/", "/public/**").permitAll() // Allow access to public resources
//                        .anyRequest().authenticated() // Require authentication for any other request
//                )
//                .oauth2Login(oauth2 -> oauth2
//                                // You can add customizations here if needed, for example:
//                                // .authorizationEndpoint(authorization -> authorization.baseUri("/oauth2/authorize"))
//                                .redirectionEndpoint(redirection -> redirection.baseUri("/login/oauth2/code/*"))
//                        // .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
//                );
//        return http.build();
//    }

}