package com.depistage.mapper;

import com.depistage.dto.ParticipantForm;
import com.depistage.dto.ParticipantResponse;
import com.depistage.entity.Participant;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;

@Component
public class ParticipantMapper {

    public ParticipantResponse toResponse(Participant p) {
        int age = Period.between(p.getDateNaissance(), LocalDate.now()).getYears();
        return new ParticipantResponse(p.getId(), p.getNom(), p.getPrenom(), p.getSexe(),
                p.getDateNaissance(), age, p.getTelephone(), p.getQuartier());
    }

    public ParticipantForm toForm(Participant p) {
        ParticipantForm f = new ParticipantForm();
        f.setNom(p.getNom());
        f.setPrenom(p.getPrenom());
        f.setSexe(p.getSexe());
        f.setDateNaissance(p.getDateNaissance());
        f.setTelephone(p.getTelephone());
        f.setQuartier(p.getQuartier());
        return f;
    }

    public void apply(ParticipantForm f, Participant p) {
        p.setNom(f.getNom().trim());
        p.setPrenom(f.getPrenom().trim());
        p.setSexe(f.getSexe());
        p.setDateNaissance(f.getDateNaissance());
        p.setTelephone(f.getTelephone() == null || f.getTelephone().isBlank() ? null : f.getTelephone().trim());
        p.setQuartier(f.getQuartier().trim());
    }
}
