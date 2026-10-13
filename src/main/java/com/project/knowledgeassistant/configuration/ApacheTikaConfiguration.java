package com.project.knowledgeassistant.configuration;

import org.apache.tika.Tika;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApacheTikaConfiguration {


    @Bean
    public Tika getTika() {
        return new Tika();
    }

}
