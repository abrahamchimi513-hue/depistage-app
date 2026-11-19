package com.depistage.service;

import com.depistage.dto.CampagneForm;
import com.depistage.dto.CampagneResponse;
import com.depistage.dto.OptionResponse;
import com.depistage.entity.Campagne;
import com.depistage.entity.TypeDepistage;
import com.depistage.exception.DeletionRefusedException;
import com.depistage.exception.DuplicateException;
import com.depistage.exception.ResourceNotFoundException;
import com.depistage.mapper.CampagneMapper;
import com.depistage.repository.CampagneRepository;
import com.depistage.repository.DepistageRepository;
import com.depistage.repository.SiteRepository;
import com.depistage.repository.TypeDepistageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CampagneService {

    private final CampagneRepository repo;
    private final TypeDepistageRepository typeRepo;
    private final SiteRepository siteRepo;
    private final DepistageRepository depistageRepo;
    private final CampagneMapper mapper;

    public Page<CampagneResponse> rechercher(String q, Pageable pageable) {
        // Une seule requête d'agrégation pour toutes les campagnes de la page (pas de N+1)
        Map<Long, Long> realises = new HashMap<>();
        for (Object[] ligne : depistageRepo.countParCampagne()) {
            realises.put((Long) ligne[0], (Long) ligne[1]);
        }
        return repo.search(Texte.norm(q), pageable)
                .map(c -> mapper.toResponse(c, realises.getOrDefault(c.getId(), 0L)));
    }

    public CampagneResponse trouver(Long id) {
        return mapper.toResponse(charger(id), depistageRepo.countByCampagne(id));
    }

    public CampagneForm formulaire(Long id) {
        return mapper.toForm(charger(id));
    }

    @Transactional
    public CampagneResponse creer(CampagneForm form) {
        if (repo.existsByNomIgnoreCase(form.getNom().trim())) {
            throw new DuplicateException("nom", "Une campagne porte déjà ce nom.");
        }
        Campagne c = new Campagne();
        mapper.apply(form, c);
        c.setTypes(resoudreTypes(form));
        return mapper.toResponse(repo.save(c), 0);
    }

    @Transactional
    public CampagneResponse modifier(Long id, CampagneForm form) {
        Campagne c = charger(id);
        if (repo.existsByNomIgnoreCaseAndIdNot(form.getNom().trim(), id)) {
            throw new DuplicateException("nom", "Une campagne porte déjà ce nom.");
        }
        mapper.apply(form, c);
        c.setTypes(resoudreTypes(form));
        return mapper.toResponse(c, depistageRepo.countByCampagne(id));
    }

    @Transactional
    public void supprimer(Long id) {
        charger(id);
        if (siteRepo.existsByCampagneId(id)) {
            throw new DeletionRefusedException(
                    "Impossible de supprimer cette campagne : des sites lui sont rattachés.");
        }
        // Les liens de la table de jointure disparaissent avec la campagne
        repo.deleteById(id);
    }

    public List<OptionResponse> options() {
        return repo.findAll().stream()
                .sorted((a, b) -> a.getNom().compareToIgnoreCase(b.getNom()))
                .map(c -> new OptionResponse(c.getId(), c.getNom())).toList();
    }

    private HashSet<TypeDepistage> resoudreTypes(CampagneForm form) {
        List<Long> ids = form.getTypeIds().stream().distinct().toList();
        List<TypeDepistage> trouves = typeRepo.findAllById(ids);
        if (trouves.size() != ids.size()) {
            throw new ResourceNotFoundException("typeIds", "Un des types de dépistage choisis n’existe pas.");
        }
        return new HashSet<>(trouves);
    }

    private Campagne charger(Long id) {
        return repo.findWithTypes(id).orElseThrow(
                () -> new ResourceNotFoundException("Campagne introuvable (id " + id + ")."));
    }
}
