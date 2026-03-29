package com.instantservices.backend.config;

import com.instantservices.backend.service.TokenBlacklistService;
import com.instantservices.backend.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final TokenBlacklistService blacklistService;

    public JwtFilter(JwtUtil jwtUtil, UserService userService, TokenBlacklistService blacklistService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.blacklistService = blacklistService;
    }
    //This filter runs for every request and:
    //Reads JWT token from request
    //Validates it
    //Loads user
    //Sets authentication in Spring Security

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
    try {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            String token = authHeader.substring(7);
            //  ADD BLACKLIST CHECK HERE
            if (blacklistService.isBlacklisted(token)) {
                System.out.println("Blacklisted token used!");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            if (jwtUtil.isValid(token)) {

                String email = jwtUtil.extractEmail(token);

                // Avoid overwriting authentication if already logged in
                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                    UserDetails userDetails = userService.loadUserByUsername(email);


                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("JWT validated for user: " + email);
                }
            }
        }

        filterChain.doFilter(request, response);
    } catch (Exception e) {
        // ❗ EXCEPTION HANDLING
        System.out.println("JWT error: " + e.getMessage());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
    }
}
