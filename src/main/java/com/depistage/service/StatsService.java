package com.depistage.service;

import com.depistage.dto.CampagneStatsResponse;
import com.depistage.dto.StatsResponse;
import com.depistage.dto.StatsResponse.Avancement;
import com.depistage.dto.StatsResponse.Repartition;
import com.depistage.entity.Campagne;
import com.depistage.enums.Resultat;
import com.depistage.enums.StatutCampagne;
import com.depistage.exception.ResourceNotFoundException;
import com.depistage.mapper.CampagneMapper;
import com.depistage.mapper.DepistageMapper;
import com.depistage.repository.CampagneRepository;
import com.depistage.repository.DepistageRepository;
import com.depistage.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Tous les chiffres viennent de requêtes d'agrégation : aucune boucle sur la table des dépistages. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsService {

    private final DepistageRepository depistageRepo;
    private final CampagneRepository campagneRepo;
    private final ParticipantRepository participantRepo;
    private final DepistageMapper depistageMapper;

    public StatsResponse globales() {
        long depistages = depistageRepo.count();
        long positifs = depistageRepo.countByResultat(Resultat.POSITIF);

        List<Repartition> parType = new ArrayList<>();
        for (Object[] l : depistageRepo.countParType()) {
            parType.add(repartition((String) l[0], (Long) l[1], depistages));
        }
        List<Repartition> parSite = new ArrayList<>();
        for (Object[] l : depistageRepo.countParSite()) {
            parSite.add(repartition((String) l[0], (Long) l[1], depistages));
        }
        // Tous les résultats apparaissent, même à zéro
        Map<Resultat, Long> comptes = new EnumMap<>(Resultat.class);
        for (Object[] l : depistageRepo.countParResultat()) {
            comptes.put((Resultat) l[0], (Long) l[1]);
        }
        List<Repartition> parResultat = new ArrayList<>();
        for (Resultat r : Resultat.values()) {
            parResultat.add(repartition(r.getLibelle(), comptes.getOrDefault(r, 0L), depistages));
        }

        Map<Long, Long> realises = new HashMap<>();
        for (Object[] l : depistageRepo.countParCampagne()) {
            realises.put((Long) l[0], (Long) l[1]);
        }
        List<Avancement> avancements = new ArrayList<>();
        for (Campagne c : campagneRepo.findAll(Sort.by("nom"))) {
            long fait = realises.getOrDefault(c.getId(), 0L);
            avancements.add(new Avancement(c.getId(), c.getNom(), c.getStatut().getLibelle(), fait,
                    c.getObjectif(), CampagneMapper.pourcentage(fait, c.getObjectif()), fait >= c.getObjectif()));
        }

        var casAOrienter = depistageRepo
                .findByResultatIn(List.of(Resultat.POSITIF, Resultat.A_CONFIRMER),
                        PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "dateDepistage").and(Sort.by(Sort.Direction.DESC, "id"))))
                .map(depistageMapper::toResponse).getContent();

        return new StatsResponse(campagneRepo.countByStatut(StatutCampagne.EN_COURS), participantRepo.count(),
                depistages, positifs, taux(positifs, depistages), parType, parResultat, parSite, avancements, casAOrienter);
    }

    public CampagneStatsResponse pourCampagne(Long campagneId) {
        Campagne c = campagneRepo.findById(campagneId).orElseThrow(
                () -> new ResourceNotFoundException("Campagne introuvable (id " + campagneId + ")."));
        Map<Resultat, Long> comptes = new EnumMap<>(Resultat.class);
        for (Object[] l : depistageRepo.countParResultatPourCampagne(campagneId)) {
            comptes.put((Resultat) l[0], (Long) l[1]);
        }
        long negatifs = comptes.getOrDefault(Resultat.NEGATIF, 0L);
        long positifs = comptes.getOrDefault(Resultat.POSITIF, 0L);
        long aConfirmer = comptes.getOrDefault(Resultat.A_CONFIRMER, 0L);
        long realises = negatifs + positifs + aConfirmer;
        return new CampagneStatsResponse(realises, c.getObjectif(),
                CampagneMapper.pourcentage(realises, c.getObjectif()), negatifs, positifs, aConfirmer,
                taux(positifs, realises));
    }

    private static Repartition repartition(String label, long total, long ensemble) {
        int pct = ensemble == 0 ? 0 : (int) Math.round(total * 100.0 / ensemble);
        return new Repartition(label, total, pct);
    }

    /** Taux en % arrondi à une décimale ; 0 quand il n'y a aucun dépistage (jamais de NaN). */
    private static double taux(long positifs, long total) {
        return total == 0 ? 0.0 : Math.round(positifs * 1000.0 / total) / 10.0;
    }
}
