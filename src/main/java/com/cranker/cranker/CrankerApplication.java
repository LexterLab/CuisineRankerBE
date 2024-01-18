package com.cranker.cranker;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(
        title = "Cuisine Ranker API",
        description = "Cuisine Ranker Spring Documentation",
        version = "v1.0",
        contact = @Contact(
                name = "Alexander Parpulansky",
                email = "alexanderparpulansky@gmail.com"
        )
),
        externalDocs = @ExternalDocumentation(
                description = "Cuisine Ranker",
                url = "https://github.com/aparpEdu/CuisineRankerBE"
        )
)
public class CrankerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrankerApplication.class, args);
    }

}
