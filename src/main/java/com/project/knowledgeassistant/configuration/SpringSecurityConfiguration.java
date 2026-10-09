package com.project.knowledgeassistant.configuration;

import com.project.knowledgeassistant.Filter.JWTFilter;
import org.hibernate.StatelessSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SpringSecurityConfiguration {

    @Autowired
    private JWTFilter jwtFilter;

    @Bean
    public SecurityFilterChain  securityFilter (HttpSecurity http) throws Exception {

         return http.csrf((req) -> req.disable())
                 .authorizeHttpRequests((requests) -> requests.requestMatchers("/user/**", "/" ,"/api/rag/search/**" ,"/api/test/gemini" , "/api/test/ask" ,
                         "/api/documents/upload/two"
                         , "/api/embedding/test/**").permitAll().anyRequest().authenticated())
                 .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                 .sessionManagement((a)->a.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                 .build() ;

    }

    @Bean
    public BCryptPasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }


}
