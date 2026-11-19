package com.depistage.service;

import com.depistage.dto.OptionResponse;
import com.depistage.dto.SiteForm;
import com.depistage.dto.SiteResponse;
import com.depistage.entity.Campagne;
import com.depistage.entity.Site;
import com.depistage.enums.StatutCampagne;
import com.depistage.exception.DeletionRefusedException;
import com.depistage.exception.ResourceNotFoundException;
import com.depistage.mapper.SiteMapper;
import com.depistage.repository.CampagneRepository;
import com.depistage.repository.DepistageRepository;
import com.depistage.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SiteService {

    private final SiteRepository repo;
    private final CampagneRepository campagneRepo;
    private final DepistageRepository depistageRepo;
    private final SiteMapper mapper;

    public Page<SiteResponse> rechercher(String q, Pageable pageable) {
        return repo.search(Texte.norm(q), pageable).map(mapper::toResponse);
    }

    public List<SiteResponse> listerParCampagne(Long campagneId) {
        if (!campagneRepo.existsById(campagneId)) {
            throw new ResourceNotFoundException("Campagne introuvable (id " + campagneId + ").");
        }
        return repo.findByCampagneIdOrderByNom(campagneId).stream().map(mapper::toResponse).toList();
    }

    public SiteForm formulaire(Long id) {
        return mapper.toForm(charger(id));
    }

    @Transactional
    public SiteResponse creer(SiteForm form) {
        Site s = new Site();
        mapper.apply(form, s);
        s.setCampagne(resoudreCampagne(form.getCampagneId()));
        return mapper.toResponse(repo.save(s));
    }

    @Transactional
    public SiteResponse modifier(Long id, SiteForm form) {
        Site s = charger(id);
        mapper.apply(form, s);
        s.setCampagne(resoudreCampagne(form.getCampagneId()));
        return mapper.toResponse(s);
    }

    @Transactional
    public void supprimer(Long id) {
        charger(id);
        if (depistageRepo.existsBySiteId(id)) {
            throw new DeletionRefusedException(
                    "Impossible de supprimer ce site : des dépistages y ont déjà été enregistrés.");
        }
        repo.deleteById(id);
    }

    /** Sites des campagnes en cours : ce sont les seuls où l'on peut enregistrer un dépistage. */
    public List<OptionResponse> optionsCampagnesEnCours() {
        return repo.findByCampagneStatut(StatutCampagne.EN_COURS).stream()
                .map(s -> new OptionResponse(s.getId(), s.getNom() + " — " + s.getCampagne().getNom()))
                .toList();
    }

    private Campagne resoudreCampagne(Long campagneId) {
        return campagneRepo.findById(campagneId).orElseThrow(
                () -> new ResourceNotFoundException("campagneId", "La campagne choisie n’existe pas."));
    }

    private Site charger(Long id) {
        return repo.findWithCampagne(id).orElseThrow(
                () -> new ResourceNotFoundException("Site introuvable (id " + id + ")."));
    }
}
