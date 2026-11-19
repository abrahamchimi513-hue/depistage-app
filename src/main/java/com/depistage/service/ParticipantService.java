package com.depistage.service;

import com.depistage.dto.OptionResponse;
import com.depistage.dto.ParticipantForm;
import com.depistage.dto.ParticipantResponse;
import com.depistage.entity.Participant;
import com.depistage.exception.DeletionRefusedException;
import com.depistage.exception.ResourceNotFoundException;
import com.depistage.mapper.ParticipantMapper;
import com.depistage.repository.DepistageRepository;
import com.depistage.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParticipantService {

    private final ParticipantRepository repo;
    private final DepistageRepository depistageRepo;
    private final ParticipantMapper mapper;

    public Page<ParticipantResponse> rechercher(String q, Pageable pageable) {
        return repo.search(Texte.norm(q), pageable).map(mapper::toResponse);
    }

    public ParticipantForm formulaire(Long id) {
        return mapper.toForm(charger(id));
    }

    @Transactional
    public ParticipantResponse creer(ParticipantForm form) {
        Participant p = new Participant();
        mapper.apply(form, p);
        return mapper.toResponse(repo.save(p));
    }

    @Transactional
    public ParticipantResponse modifier(Long id, ParticipantForm form) {
        Participant p = charger(id);
        mapper.apply(form, p);
        return mapper.toResponse(p);
    }

    @Transactional
    public void supprimer(Long id) {
        charger(id);
        if (depistageRepo.existsByParticipantId(id)) {
            throw new DeletionRefusedException(
                    "Impossible de supprimer ce participant : des dépistages lui sont associés.");
        }
        repo.deleteById(id);
    }

    public List<OptionResponse> options() {
        return repo.findAllByOrderByNomAscPrenomAsc().stream()
                .map(p -> new OptionResponse(p.getId(), p.getNom() + " " + p.getPrenom() + " (" + p.getQuartier() + ")"))
                .toList();
    }

    private Participant charger(Long id) {
        return repo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Participant introuvable (id " + id + ")."));
    }
}
