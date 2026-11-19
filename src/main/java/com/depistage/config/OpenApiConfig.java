package com.depistage.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI().info(new Info()
                .title("API de gestion des campagnes de dépistage")
                .version("1.0.0")
                .description("""
                        Consultez les campagnes de dépistage (diabète, hypertension, paludisme), leurs sites \
                        et leurs statistiques, listez les dépistages avec des filtres, et enregistrez de nouveaux \
                        dépistages.

                        Toutes les erreurs ont le même format JSON (voir le schéma ApiError) : horodatage, code, \
                        message et détails par champ. Les données de démonstration sont entièrement fictives.""")
                .contact(new Contact().name("Association de santé — projet scolaire")));
    }
}
