package com.depistage;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

class ControllersIT extends BaseIT {

    @Test
    void tableauDeBordSAffiche() throws Exception {
        mvc.perform(get("/")).andExpect(status().isOk()).andExpect(view().name("index"))
                .andExpect(content().string(containsString("Tableau de bord")));
    }

    @Test
    void campagneInexistanteDonnePageIntrouvable404() throws Exception {
        mvc.perform(get("/campagnes/9999")).andExpect(status().isNotFound()).andExpect(view().name("error/404"))
                .andExpect(content().string(not(containsString("Exception"))));
    }

    @Test
    void rechercheParticipantsConserveLeMotDansLaPagination() throws Exception {
        mvc.perform(get("/participants").param("q", "ka")).andExpect(status().isOk())
                .andExpect(content().string(containsString("q=ka")));
    }

    @Test
    void formulaireInvalideSeReaffichePuisRefusePositifSansOrientation() throws Exception {
        mvc.perform(post("/depistages")
                        .param("participantId", participant("Fokam").toString())
                        .param("agentId", agent("670000001").toString())
                        .param("siteId", site("École Publique de Bonamoussadi").toString())
                        .param("typeId", type("HYPERTENSION").toString())
                        .param("dateDepistage", LocalDate.now().toString())
                        .param("resultat", "POSITIF"))
                .andExpect(status().isOk()) // pas de redirection : le formulaire est réaffiché
                .andExpect(view().name("depistages/form"))
                .andExpect(model().attributeHasFieldErrors("depistageForm", "orientation"))
                .andExpect(content().string(containsString("L’orientation est obligatoire")));
    }

    @Test
    void enregistrementReussiRedirigeAvecMessage() throws Exception {
        mvc.perform(post("/depistages")
                        .param("participantId", participant("Fokam").toString())
                        .param("agentId", agent("670000001").toString())
                        .param("siteId", site("École Publique de Bonamoussadi").toString())
                        .param("typeId", type("HYPERTENSION").toString())
                        .param("dateDepistage", LocalDate.now().toString())
                        .param("resultat", "NEGATIF"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/depistages"))
                .andExpect(flash().attributeExists("succes"));
    }

    @Test
    void suppressionDunSiteUtiliseRedirigeAvecUneExplication() throws Exception {
        Long id = site("École Publique de Bonamoussadi");
        mvc.perform(post("/sites/" + id + "/supprimer"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/sites"))
                .andExpect(flash().attributeExists("erreur"));
    }

    @Test
    void apiCampagnesRetourneUnJsonPagineSansBoucle() throws Exception {
        mvc.perform(get("/api/campagnes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content[0].nom").exists());
    }

    @Test
    void apiCampagneInexistanteRetourne404AuFormatUniforme() throws Exception {
        mvc.perform(get("/api/campagnes/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value("NOT_FOUND"))
                .andExpect(jsonPath("$.path").value("/api/campagnes/9999"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void apiDepistageInvalideRetourne400AvecDetailsParChamp() throws Exception {
        mvc.perform(post("/api/depistages").contentType("application/json").content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details.participantId").exists())
                .andExpect(jsonPath("$.details.resultat").exists());
    }

    @Test
    void apiDepistageSurCampagneTermineeRetourne409() throws Exception {
        String json = """
                {"participantId": %d, "agentId": %d, "siteId": %d, "typeId": %d,
                 "dateDepistage": "%s", "resultat": "NEGATIF"}"""
                .formatted(participant("Fokam"), agent("670000001"), site("Marché de Deido"),
                        type("HYPERTENSION"), LocalDate.now().minusDays(100));
        mvc.perform(post("/api/depistages").contentType("application/json").content(json))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("CONFLICT"));
    }

    @Test
    void apiSuppressionDunDepistageRetourne204PuisLeDepistageEstIntrouvable() throws Exception {
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/api/depistages/1"))
                .andExpect(status().isNoContent());
        mvc.perform(get("/api/depistages/1")).andExpect(status().isNotFound());
    }

    @Test
    void apiSuppressionDunDepistageInexistantRetourne404() throws Exception {
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/api/depistages/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }

    @Test
    void erreurInterneSimuleeAffichePageGeneriqueSansDetailTechnique() throws Exception {
        mvc.perform(get("/simuler-erreur"))
                .andExpect(status().isInternalServerError())
                .andExpect(view().name("error/500"))
                .andExpect(content().string(not(containsString("jdbc"))))
                .andExpect(content().string(not(containsString("IllegalState"))));
    }

    @Test
    void erreurInterneSimuleeDeLApiRetourneUnJsonGenerique() throws Exception {
        mvc.perform(get("/api/simuler-erreur"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"))
                .andExpect(content().string(not(containsString("jdbc"))))
                .andExpect(content().string(not(containsString("IllegalState"))));
    }

    @Test
    void apiDepistageValideRetourne201() throws Exception {
        String json = """
                {"participantId": %d, "agentId": %d, "siteId": %d, "typeId": %d,
                 "dateDepistage": "%s", "resultat": "NEGATIF"}"""
                .formatted(participant("Fokam"), agent("670000001"), site("École Publique de Bonamoussadi"),
                        type("HYPERTENSION"), LocalDate.now());
        mvc.perform(post("/api/depistages").contentType("application/json").content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }
}
