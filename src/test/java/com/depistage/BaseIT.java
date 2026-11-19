package com.depistage;

import com.depistage.dto.DepistageForm;
import com.depistage.entity.Agent;
import com.depistage.entity.Campagne;
import com.depistage.entity.Participant;
import com.depistage.entity.Site;
import com.depistage.entity.TypeDepistage;
import com.depistage.enums.Resultat;
import com.depistage.repository.AgentRepository;
import com.depistage.repository.CampagneRepository;
import com.depistage.repository.ParticipantRepository;
import com.depistage.repository.SiteRepository;
import com.depistage.repository.TypeDepistageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Base des tests : l'application complète démarre avec le profil dev (H2) et les données de démonstration.
 * Chaque test s'exécute dans une transaction annulée à la fin : la base reste intacte d'un test à l'autre.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev") // les tests utilisent toujours H2, même si la plateforme définit SPRING_PROFILES_ACTIVE=prod pendant la construction
@Transactional
public abstract class BaseIT {

    @Autowired protected MockMvc mvc;
    @Autowired protected TypeDepistageRepository typeRepo;
    @Autowired protected CampagneRepository campagneRepo;
    @Autowired protected SiteRepository siteRepo;
    @Autowired protected AgentRepository agentRepo;
    @Autowired protected ParticipantRepository participantRepo;

    protected Long type(String code) {
        return typeRepo.findAll().stream().filter(t -> t.getCode().equals(code)).map(TypeDepistage::getId)
                .findFirst().orElseThrow();
    }

    protected Long campagne(String nom) {
        return campagneRepo.findAll().stream().filter(c -> c.getNom().equals(nom)).map(Campagne::getId)
                .findFirst().orElseThrow();
    }

    protected Long site(String nom) {
        return siteRepo.findAll().stream().filter(s -> s.getNom().equals(nom)).map(Site::getId)
                .findFirst().orElseThrow();
    }

    protected Long agent(String telephone) {
        return agentRepo.findAll().stream().filter(a -> a.getTelephone().equals(telephone)).map(Agent::getId)
                .findFirst().orElseThrow();
    }

    protected Long participant(String nom) {
        return participantRepo.findAll().stream().filter(p -> p.getNom().equals(nom)).map(Participant::getId)
                .findFirst().orElseThrow();
    }

    /**
     * Un dépistage VALIDE par défaut : campagne en cours « Dépistage Bonamoussadi », agent actif,
     * hypertension (proposée), participant « Fokam » qui n'a pas encore ce type, date du jour.
     * Chaque test modifie UN seul élément pour provoquer UNE seule erreur.
     */
    protected DepistageForm formulaireValide() {
        DepistageForm f = new DepistageForm();
        f.setParticipantId(participant("Fokam"));
        f.setAgentId(agent("670000001"));
        f.setSiteId(site("École Publique de Bonamoussadi"));
        f.setTypeId(type("HYPERTENSION"));
        f.setDateDepistage(LocalDate.now());
        f.setResultat(Resultat.NEGATIF);
        return f;
    }
}
