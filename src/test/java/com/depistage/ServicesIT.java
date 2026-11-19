package com.depistage;

import com.depistage.dto.CampagneForm;
import com.depistage.dto.DepistageForm;
import com.depistage.dto.StatsResponse;
import com.depistage.enums.Resultat;
import com.depistage.enums.StatutCampagne;
import com.depistage.exception.BusinessRuleException;
import com.depistage.exception.DeletionRefusedException;
import com.depistage.exception.DuplicateException;
import com.depistage.repository.DepistageRepository;
import com.depistage.service.CampagneService;
import com.depistage.service.DepistageService;
import com.depistage.service.SiteService;
import com.depistage.service.StatsService;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ServicesIT extends BaseIT {

    @Autowired DepistageService depistageService;
    @Autowired SiteService siteService;
    @Autowired CampagneService campagneService;
    @Autowired StatsService statsService;
    @Autowired DepistageRepository depistageRepo;
    @Autowired Validator validator;

    @Test
    void depistageValideEstEnregistre() {
        var cree = depistageService.creer(formulaireValide());
        assertThat(cree.id()).isNotNull();
        assertThat(cree.campagneNom()).isEqualTo("Dépistage Bonamoussadi");
    }

    @Test
    void doublonMemeParticipantMemeTypeMemeCampagneEstRefuse() {
        depistageService.creer(formulaireValide());
        assertThatThrownBy(() -> depistageService.creer(formulaireValide()))
                .isInstanceOf(DuplicateException.class);
    }

    @Test
    void depistageSurCampagneTermineeEstRefuse() {
        DepistageForm f = formulaireValide();
        f.setSiteId(site("Marché de Deido")); // campagne TERMINEE
        f.setDateDepistage(LocalDate.now().minusDays(100));
        assertThatThrownBy(() -> depistageService.creer(f))
                .isInstanceOf(BusinessRuleException.class).hasMessageContaining("pas en cours");
    }

    @Test
    void typeNonProposeParLaCampagneEstRefuse() {
        DepistageForm f = formulaireValide();
        f.setTypeId(type("PALUDISME")); // proposé seulement par la campagne terminée
        assertThatThrownBy(() -> depistageService.creer(f))
                .isInstanceOf(BusinessRuleException.class).hasMessageContaining("pas proposé");
    }

    @Test
    void dateDansLeFuturEstRefuseParLeService() {
        DepistageForm f = formulaireValide();
        f.setDateDepistage(LocalDate.now().plusDays(1)); // dans la période de la campagne, mais dans le futur
        assertThatThrownBy(() -> depistageService.creer(f))
                .isInstanceOf(BusinessRuleException.class).hasMessageContaining("futur");
    }

    @Test
    void agentInactifEstRefuse() {
        DepistageForm f = formulaireValide();
        f.setAgentId(agent("670000004"));
        assertThatThrownBy(() -> depistageService.creer(f))
                .isInstanceOf(BusinessRuleException.class).hasMessageContaining("inactif");
    }

    @Test
    void dateHorsPeriodeDeLaCampagneEstRefusee() {
        DepistageForm f = formulaireValide();
        f.setDateDepistage(LocalDate.now().minusDays(45)); // la campagne commence il y a 30 jours
        assertThatThrownBy(() -> depistageService.creer(f))
                .isInstanceOf(BusinessRuleException.class).hasMessageContaining("comprise entre");
    }

    @Test
    void orientationManquanteSurResultatPositifEstRefusee() {
        DepistageForm f = formulaireValide();
        f.setResultat(Resultat.POSITIF);
        var erreurs = validator.validate(f);
        assertThat(erreurs).anySatisfy(v -> assertThat(v.getPropertyPath().toString()).isEqualTo("orientation"));

        f.setOrientation("Hôpital Laquintinie");
        assertThat(validator.validate(f)).isEmpty();
    }

    @Test
    void suppressionDunSiteAvecDepistagesEstRefusee() {
        Long id = site("École Publique de Bonamoussadi");
        assertThatThrownBy(() -> siteService.supprimer(id)).isInstanceOf(DeletionRefusedException.class);
        assertThat(siteRepo.existsById(id)).isTrue();
    }

    @Test
    void suppressionDuneCampagneAvecSitesEstRefusee() {
        Long id = campagne("Dépistage Bonamoussadi");
        assertThatThrownBy(() -> campagneService.supprimer(id)).isInstanceOf(DeletionRefusedException.class);
    }

    @Test
    void nomDeCampagneEnDoubleEstRefuse() {
        CampagneForm f = new CampagneForm();
        f.setNom("Dépistage Bonamoussadi");
        f.setVille("Douala");
        f.setDateDebut(LocalDate.now());
        f.setDateFin(LocalDate.now().plusDays(10));
        f.setObjectif(10);
        f.setStatut(StatutCampagne.PLANIFIEE);
        f.setTypeIds(List.of(type("DIABETE")));
        assertThatThrownBy(() -> campagneService.creer(f)).isInstanceOf(DuplicateException.class);
    }

    @Test
    void statistiquesSurBaseVideDonnentDesZerosSansErreur() {
        depistageRepo.deleteAll();
        StatsResponse s = statsService.globales();
        assertThat(s.depistages()).isZero();
        assertThat(s.positifs()).isZero();
        assertThat(s.tauxPositivite()).isZero(); // jamais NaN
        assertThat(s.casAOrienter()).isEmpty();
        assertThat(s.avancements()).allSatisfy(a -> assertThat(a.pourcentage()).isZero());
    }

    @Test
    void avancementEstPlafonneA100() {
        assertThat(com.depistage.mapper.CampagneMapper.pourcentage(50, 10)).isEqualTo(100);
        assertThat(com.depistage.mapper.CampagneMapper.pourcentage(5, 0)).isZero();
    }
}
