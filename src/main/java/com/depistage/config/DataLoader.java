package com.depistage.config;

import com.depistage.entity.Agent;
import com.depistage.entity.Campagne;
import com.depistage.entity.Depistage;
import com.depistage.entity.Participant;
import com.depistage.entity.Site;
import com.depistage.entity.TypeDepistage;
import com.depistage.enums.Fonction;
import com.depistage.enums.Resultat;
import com.depistage.enums.Sexe;
import com.depistage.enums.StatutCampagne;
import com.depistage.repository.AgentRepository;
import com.depistage.repository.CampagneRepository;
import com.depistage.repository.DepistageRepository;
import com.depistage.repository.ParticipantRepository;
import com.depistage.repository.SiteRepository;
import com.depistage.repository.TypeDepistageRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Données de démonstration, ENTIÈREMENT FICTIVES. Chargées seulement si la base est vide,
 * dans l'ordre des dépendances : types, campagnes, sites, agents, participants, dépistages.
 */
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    private final TypeDepistageRepository typeRepo;
    private final CampagneRepository campagneRepo;
    private final SiteRepository siteRepo;
    private final AgentRepository agentRepo;
    private final ParticipantRepository participantRepo;
    private final DepistageRepository depistageRepo;

    @Override
    @Transactional
    public void run(String... args) {
        if (typeRepo.count() > 0 || campagneRepo.count() > 0) {
            log.info("Base non vide : données de démonstration ignorées.");
            return;
        }
        LocalDate aujourdhui = LocalDate.now();

        // 3 types
        TypeDepistage diabete = type("DIABETE", "Dépistage du diabète", "Mesure de la glycémie à jeun");
        TypeDepistage hyper = type("HYPERTENSION", "Dépistage de l’hypertension", "Mesure de la tension artérielle");
        TypeDepistage palu = type("PALUDISME", "Dépistage du paludisme", "Test de diagnostic rapide");

        // 2 campagnes (dates relatives à aujourd'hui pour rester cohérentes quelle que soit la date de lancement)
        Campagne c1 = campagne("Dépistage Bonamoussadi", "Douala", aujourdhui.minusDays(30), aujourdhui.plusDays(30),
                30, StatutCampagne.EN_COURS, Set.of(diabete, hyper));
        Campagne c2 = campagne("Campagne Paludisme Deido", "Douala", aujourdhui.minusDays(120), aujourdhui.minusDays(60),
                10, StatutCampagne.TERMINEE, Set.of(hyper, palu));

        // 5 sites
        List<Site> sites = List.of(
                site("École Publique de Bonamoussadi", "Bonamoussadi", "Route de l’école", 40, c1),
                site("Marché de Bonamoussadi", "Bonamoussadi", "Entrée principale", 60, c1),
                site("Centre de Santé de Makepe", "Makepe", null, 30, c1),
                site("Marché de Deido", "Deido", "Près du carrefour", 50, c2),
                site("Centre de Santé d’Akwa", "Akwa", null, 25, c2));

        // 4 agents (le dernier est inactif)
        List<Agent> agents = List.of(
                agent("Essomba", "Claire", "670000001", Fonction.INFIRMIER, true),
                agent("Ndzana", "Hervé", "670000002", Fonction.TECHNICIEN_LABO, true),
                agent("Mballa", "Joël", "670000003", Fonction.MEDECIN, true),
                agent("Tabi", "Rose", "670000004", Fonction.BENEVOLE, false));

        // 12 participants (8 ont « ka » dans leur nom, pour tester la recherche paginée)
        String[][] identites = {
                {"Kamga", "Alice", "F"}, {"Kana", "Paul", "M"}, {"Nkake", "Joséphine", "F"}, {"Takam", "Marie", "F"},
                {"Fokam", "Pierre", "M"}, {"Kamdem", "Samuel", "M"}, {"Ngaka", "Berthe", "F"}, {"Mekaou", "Rodrigue", "M"},
                {"Ekotto", "Michel", "M"}, {"Tagne", "Estelle", "F"}, {"Biyiha", "Jean", "M"}, {"Mvondo", "Clarisse", "F"}};
        String[] quartiers = {"Bonamoussadi", "Akwa", "Deido", "Makepe", "Bonabéri"};
        List<Participant> participants = new ArrayList<>();
        for (int i = 0; i < identites.length; i++) {
            Participant p = new Participant();
            p.setNom(identites[i][0]);
            p.setPrenom(identites[i][1]);
            p.setSexe(identites[i][2].equals("F") ? Sexe.FEMININ : Sexe.MASCULIN);
            p.setDateNaissance(LocalDate.of(1955 + i * 4, 1 + i % 12, 5 + i));
            p.setTelephone(i % 3 == 0 ? null : String.format("699%06d", i + 1)); // toujours 9 chiffres
            p.setQuartier(quartiers[i % quartiers.length]);
            participants.add(p);
        }
        participantRepo.saveAll(participants);

        // 20 dépistages : 14 dans la campagne en cours, 6 dans la campagne terminée
        List<Depistage> depistages = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            boolean premiere = i < 14;
            Campagne campagne = premiere ? c1 : c2;
            Depistage d = new Depistage();
            d.setSite(premiere ? sites.get(i % 3) : sites.get(3 + i % 2));
            d.setType(premiere ? (i < 12 ? diabete : hyper) : palu);
            d.setParticipant(participants.get(i % 12));
            d.setAgent(agents.get(i % 3));
            d.setDateDepistage(premiere
                    ? campagne.getDateDebut().plusDays((i * 2) % 26)
                    : campagne.getDateDebut().plusDays(i));
            Resultat resultat = switch (i % 5) {
                case 2 -> Resultat.POSITIF;
                case 3 -> Resultat.A_CONFIRMER;
                default -> Resultat.NEGATIF;
            };
            d.setResultat(resultat);
            d.setValeurMesuree(BigDecimal.valueOf(0.80 + (i % 7) * 0.15).setScale(2, java.math.RoundingMode.HALF_UP));
            if (resultat != Resultat.NEGATIF) {
                d.setOrientation("Hôpital Laquintinie");
                d.setObservation("Contrôle conseillé sous une semaine");
            }
            depistages.add(d);
        }
        depistageRepo.saveAll(depistages);
        log.info("Données de démonstration chargées : 3 types, 2 campagnes, 5 sites, 4 agents, 12 participants, 20 dépistages.");
    }

    private TypeDepistage type(String code, String libelle, String description) {
        TypeDepistage t = new TypeDepistage();
        t.setCode(code);
        t.setLibelle(libelle);
        t.setDescription(description);
        return typeRepo.save(t);
    }

    private Campagne campagne(String nom, String ville, LocalDate debut, LocalDate fin, int objectif,
                              StatutCampagne statut, Set<TypeDepistage> types) {
        Campagne c = new Campagne();
        c.setNom(nom);
        c.setVille(ville);
        c.setDateDebut(debut);
        c.setDateFin(fin);
        c.setObjectif(objectif);
        c.setStatut(statut);
        c.setTypes(new HashSet<>(types));
        return campagneRepo.save(c);
    }

    private Site site(String nom, String quartier, String adresse, int capacite, Campagne campagne) {
        Site s = new Site();
        s.setNom(nom);
        s.setQuartier(quartier);
        s.setAdresse(adresse);
        s.setCapaciteJournaliere(capacite);
        s.setCampagne(campagne);
        return siteRepo.save(s);
    }

    private Agent agent(String nom, String prenom, String telephone, Fonction fonction, boolean actif) {
        Agent a = new Agent();
        a.setNom(nom);
        a.setPrenom(prenom);
        a.setTelephone(telephone);
        a.setFonction(fonction);
        a.setActif(actif);
        return agentRepo.save(a);
    }
}
