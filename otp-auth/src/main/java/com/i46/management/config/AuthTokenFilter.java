package com.i46.management.config;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.i46.management.model.UserDTO;
import com.i46.management.model.entity.User;
import com.i46.management.model.service.IdTokenVerify;
import com.i46.management.model.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;


@Component
public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private IdTokenVerify idTokenVerify;

    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

            String jwt = parseJwt(request);
            if (jwt != null && idTokenVerify.validateJwtToken(jwt)) {
                String appId = idTokenVerify.userDetails.get("appId") == null ? null : idTokenVerify.userDetails.get("appId").toString();
                String name = idTokenVerify.userDetails.get("name") == null ? null :  idTokenVerify.userDetails.get("name").toString();
                String email = idTokenVerify.userDetails.get("email") == null ? null : idTokenVerify.userDetails.get("email").toString();
                String provider = idTokenVerify.userDetails.get("provider") == null ? "google" : idTokenVerify.userDetails.get("provider").toString();
                logger.info(name + " " + email + " " + provider);
                Optional<User> userDetails = userService.getByAppId(appId);
                if(userDetails.isPresent()) {
                    logger.info("Existing user with appId {}", appId);
                    User userData = userDetails.get();
                    userData.setLastLogin(Timestamp.valueOf(LocalDateTime.now()));
                    userService.save(userData);
                }else{
                    logger.info("New user with appId {}", appId);

                    UserDTO userDTO = new UserDTO();
                    userDTO.setAppId(appId);
                    if (name != null){
                        userDTO.setName(name);
                    }
                    if (email != null){
                        userDTO.setEmail(email);
                    }
                    userDTO.setProvider(provider);
                    userDTO.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
                    userDTO.setLastLogin(Timestamp.valueOf(LocalDateTime.now()));
                    User user = new User(userDTO);
                    userService.save(user);
                }

                List<GrantedAuthority> authorities = new ArrayList<>();
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        filterChain.doFilter(request, response);
    }
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }
}