package com.project.knowledgeassistant.Filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.project.knowledgeassistant.services.MyUserDetailsServiceImpl;
import com.project.knowledgeassistant.services.TokenBlackListService;
import com.project.knowledgeassistant.utilities.JWTUtility;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@Component
public class JWTFilter extends OncePerRequestFilter {

    @Autowired
    private JWTUtility jwtUtility;

    @Autowired
    private MyUserDetailsServiceImpl customUserDetailsService;
    
    @Autowired
    private TokenBlackListService tokenBlacklistService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authToken = request.getHeader("Authorization");
        String token = null ;
        String email = null ;

        if (authToken != null && authToken.startsWith("Bearer ")) {
            token = authToken.substring(7).trim();
            if (tokenBlacklistService.isBlackListed(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Token has been revoked. Please log in again.\"}");
                return; // Halt filter chain
            }
            try {
                email = jwtUtility.extractEmail(token);

                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails user = customUserDetailsService.loadUserByUsername(email);

                    if (jwtUtility.validateToken(token, user.getUsername())) {
                        UsernamePasswordAuthenticationToken mainToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(mainToken);
                    }
                }
            } catch (Exception e) {
                System.out.println("JWTFilter auth failed: " + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);


    }
    
    
}
