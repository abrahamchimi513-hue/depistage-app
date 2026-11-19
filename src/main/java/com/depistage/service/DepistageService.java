package com.depistage.service;

import com.depistage.dto.DepistageForm;
import com.depistage.dto.DepistageResponse;
import com.depistage.entity.Agent;
import com.depistage.entity.Campagne;
import com.depistage.entity.Depistage;
import com.depistage.entity.Participant;
import com.depistage.entity.Site;
import com.depistage.entity.TypeDepistage;
import com.depistage.enums.Resultat;
import com.depistage.enums.StatutCampagne;
import com.depistage.exception.BusinessRuleException;
import com.depistage.exception.DuplicateException;
import com.depistage.exception.ResourceNotFoundException;
import com.depistage.mapper.DepistageMapper;
import com.depistage.repository.AgentRepository;
import com.depistage.repository.DepistageRepository;
import com.depistage.repository.ParticipantRepository;
import com.depistage.repository.SiteRepository;
import com.depistage.repository.TypeDepistageRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepistageService {

    private final DepistageRepository repo;
    private final ParticipantRepository participantRepo;
    private final AgentRepository agentRepo;
    private final SiteRepository siteRepo;
    private final TypeDepistageRepository typeRepo;
    private final DepistageMapper mapper;

    /** Liste paginée, filtrable par campagne, type et résultat (filtres facultatifs). */
    public Page<DepistageResponse> rechercher(Long campagneId, Long typeId, Resultat resultat, Pageable pageable) {
        Specification<Depistage> spec = (root, query, cb) -> {
            List<Predicate> filtres = new ArrayList<>();
            if (campagneId != null) {
                filtres.add(cb.equal(root.get("site").get("campagne").get("id"), campagneId));
            }
            if (typeId != null) {
                filtres.add(cb.equal(root.get("type").get("id"), typeId));
            }
            if (resultat != null) {
                filtres.add(cb.equal(root.get("resultat"), resultat));
            }
            return cb.and(filtres.toArray(new Predicate[0]));
        };
        return repo.findAll(spec, pageable).map(mapper::toResponse);
    }

    /** Supprime un dépistage (saisie erronée). Un dépistage n'est référencé par aucune autre table. */
    @Transactional
    public void supprimer(Long id) {
        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("Dépistage introuvable (id " + id + ").");
        }
        repo.deleteById(id);
    }

    public DepistageResponse trouver(Long id) {
        return mapper.toResponse(repo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Dépistage introuvable (id " + id + ").")));
    }

    /**
     * Enregistre un dépistage après vérification de toutes les règles métier.
     * Ces règles sont ici (et non dans un contrôleur) pour que les pages et l'API les appliquent toutes les deux.
     */
    @Transactional
    public DepistageResponse creer(DepistageForm form) {
        Participant participant = participantRepo.findById(form.getParticipantId()).orElseThrow(
                () -> new ResourceNotFoundException("participantId", "Le participant choisi n’existe pas."));
        Agent agent = agentRepo.findById(form.getAgentId()).orElseThrow(
                () -> new ResourceNotFoundException("agentId", "L’agent choisi n’existe pas."));
        Site site = siteRepo.findWithCampagne(form.getSiteId()).orElseThrow(
                () -> new ResourceNotFoundException("siteId", "Le site choisi n’existe pas."));
        TypeDepistage type = typeRepo.findById(form.getTypeId()).orElseThrow(
                () -> new ResourceNotFoundException("typeId", "Le type de dépistage choisi n’existe pas."));
        Campagne campagne = site.getCampagne();

        if (!agent.isActif()) {
            throw new BusinessRuleException("agentId", "Cet agent est inactif : il ne peut plus enregistrer de dépistage.");
        }
        if (campagne.getStatut() != StatutCampagne.EN_COURS) {
            throw new BusinessRuleException("siteId", "La campagne « " + campagne.getNom()
                    + " » n’est pas en cours : aucun dépistage ne peut y être enregistré.");
        }
        boolean typePropose = campagne.getTypes().stream().anyMatch(t -> t.getId().equals(type.getId()));
        if (!typePropose) {
            throw new BusinessRuleException("typeId", "Ce type de dépistage n’est pas proposé par la campagne « "
                    + campagne.getNom() + " ».");
        }
        LocalDate date = form.getDateDepistage();
        if (date.isAfter(LocalDate.now())) {
            throw new BusinessRuleException("dateDepistage", "La date du dépistage ne peut pas être dans le futur.");
        }
        if (date.isBefore(campagne.getDateDebut()) || date.isAfter(campagne.getDateFin())) {
            throw new BusinessRuleException("dateDepistage", "La date doit être comprise entre le "
                    + campagne.getDateDebut() + " et le " + campagne.getDateFin() + " (période de la campagne).");
        }
        if (repo.existsDoublon(participant.getId(), type.getId(), campagne.getId())) {
            throw new DuplicateException("typeId", "Ce participant a déjà subi ce dépistage dans cette campagne.");
        }

        Depistage d = new Depistage();
        mapper.apply(form, d);
        d.setParticipant(participant);
        d.setAgent(agent);
        d.setSite(site);
        d.setType(type);
        return mapper.toResponse(repo.save(d));
    }
}
