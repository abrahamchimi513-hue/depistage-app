package com.depistage.service;

import com.depistage.dto.OptionResponse;
import com.depistage.dto.TypeForm;
import com.depistage.dto.TypeResponse;
import com.depistage.entity.TypeDepistage;
import com.depistage.enums.StatutCampagne;
import com.depistage.exception.DeletionRefusedException;
import com.depistage.exception.DuplicateException;
import com.depistage.exception.ResourceNotFoundException;
import com.depistage.mapper.TypeMapper;
import com.depistage.repository.CampagneRepository;
import com.depistage.repository.DepistageRepository;
import com.depistage.repository.TypeDepistageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TypeDepistageService {

    private final TypeDepistageRepository repo;
    private final CampagneRepository campagneRepo;
    private final DepistageRepository depistageRepo;
    private final TypeMapper mapper;

    public Page<TypeResponse> rechercher(String q, Pageable pageable) {
        return repo.search(Texte.norm(q), pageable).map(mapper::toResponse);
    }

    public TypeForm formulaire(Long id) {
        return mapper.toForm(charger(id));
    }

    @Transactional
    public TypeResponse creer(TypeForm form) {
        if (repo.existsByCode(form.getCode().trim())) {
            throw new DuplicateException("code", "Ce code est déjà utilisé par un autre type de dépistage.");
        }
        TypeDepistage t = new TypeDepistage();
        mapper.apply(form, t);
        return mapper.toResponse(repo.save(t));
    }

    @Transactional
    public TypeResponse modifier(Long id, TypeForm form) {
        TypeDepistage t = charger(id);
        if (repo.existsByCodeAndIdNot(form.getCode().trim(), id)) {
            throw new DuplicateException("code", "Ce code est déjà utilisé par un autre type de dépistage.");
        }
        mapper.apply(form, t);
        return mapper.toResponse(t);
    }

    @Transactional
    public void supprimer(Long id) {
        charger(id);
        if (depistageRepo.existsByTypeId(id)) {
            throw new DeletionRefusedException(
                    "Impossible de supprimer ce type : des dépistages l’utilisent déjà.");
        }
        if (campagneRepo.existsByTypesId(id)) {
            throw new DeletionRefusedException(
                    "Impossible de supprimer ce type : il est proposé par au moins une campagne.");
        }
        repo.deleteById(id);
    }

    public List<OptionResponse> options() {
        return repo.findAllByOrderByLibelle().stream()
                .map(t -> new OptionResponse(t.getId(), t.getLibelle())).toList();
    }

    /** Types proposés par au moins une campagne en cours (liste du formulaire de dépistage). */
    public List<OptionResponse> optionsCampagnesEnCours() {
        return repo.findProposesParCampagnesDeStatut(StatutCampagne.EN_COURS).stream()
                .map(t -> new OptionResponse(t.getId(), t.getLibelle())).toList();
    }

    private TypeDepistage charger(Long id) {
        return repo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Type de dépistage introuvable (id " + id + ")."));
    }
}
