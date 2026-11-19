package com.depistage.service;

import com.depistage.dto.AgentForm;
import com.depistage.dto.AgentResponse;
import com.depistage.dto.OptionResponse;
import com.depistage.entity.Agent;
import com.depistage.exception.DeletionRefusedException;
import com.depistage.exception.DuplicateException;
import com.depistage.exception.ResourceNotFoundException;
import com.depistage.mapper.AgentMapper;
import com.depistage.repository.AgentRepository;
import com.depistage.repository.DepistageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AgentService {

    private final AgentRepository repo;
    private final DepistageRepository depistageRepo;
    private final AgentMapper mapper;

    public Page<AgentResponse> rechercher(String q, Pageable pageable) {
        return repo.search(Texte.norm(q), pageable).map(mapper::toResponse);
    }

    public AgentForm formulaire(Long id) {
        return mapper.toForm(charger(id));
    }

    @Transactional
    public AgentResponse creer(AgentForm form) {
        if (repo.existsByTelephone(form.getTelephone().trim())) {
            throw new DuplicateException("telephone", "Ce numéro de téléphone est déjà utilisé par un autre agent.");
        }
        Agent a = new Agent();
        mapper.apply(form, a);
        return mapper.toResponse(repo.save(a));
    }

    @Transactional
    public AgentResponse modifier(Long id, AgentForm form) {
        Agent a = charger(id);
        if (repo.existsByTelephoneAndIdNot(form.getTelephone().trim(), id)) {
            throw new DuplicateException("telephone", "Ce numéro de téléphone est déjà utilisé par un autre agent.");
        }
        mapper.apply(form, a);
        return mapper.toResponse(a);
    }

    @Transactional
    public void supprimer(Long id) {
        charger(id);
        if (depistageRepo.existsByAgentId(id)) {
            throw new DeletionRefusedException(
                    "Impossible de supprimer cet agent : il a enregistré des dépistages. Rendez-le inactif à la place.");
        }
        repo.deleteById(id);
    }

    public List<OptionResponse> optionsActifs() {
        return repo.findByActifTrueOrderByNom().stream()
                .map(a -> new OptionResponse(a.getId(), a.getNom() + " " + a.getPrenom()
                        + " (" + a.getFonction().getLibelle() + ")"))
                .toList();
    }

    private Agent charger(Long id) {
        return repo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Agent introuvable (id " + id + ")."));
    }
}
