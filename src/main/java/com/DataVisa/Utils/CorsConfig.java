package com.DataVisa.Utils;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {     
    @Override
    public void addCorsMappings(CorsRegistry registry) {         
        registry.addMapping("/**")                 
        .allowedOrigins("https://datavisa-frontend-3ixvbxpsp-rafael-aguiars-projects.vercel.app/login/acesso") // Domínio do React                
        .allowedMethods("GET", "POST", "PUT", "DELETE") 
        .allowCredentials(true); 
    } 
}
