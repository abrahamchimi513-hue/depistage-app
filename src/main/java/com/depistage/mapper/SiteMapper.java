package com.depistage.mapper;

import com.depistage.dto.SiteForm;
import com.depistage.dto.SiteResponse;
import com.depistage.entity.Site;
import org.springframework.stereotype.Component;

@Component
public class SiteMapper {

    public SiteResponse toResponse(Site s) {
        return new SiteResponse(s.getId(), s.getNom(), s.getQuartier(), s.getAdresse(),
                s.getCapaciteJournaliere(), s.getCampagne().getId(), s.getCampagne().getNom());
    }

    public SiteForm toForm(Site s) {
        SiteForm f = new SiteForm();
        f.setNom(s.getNom());
        f.setQuartier(s.getQuartier());
        f.setAdresse(s.getAdresse());
        f.setCapaciteJournaliere(s.getCapaciteJournaliere());
        f.setCampagneId(s.getCampagne().getId());
        return f;
    }

    /** Copie les champs simples ; la campagne est résolue par le service. */
    public void apply(SiteForm f, Site s) {
        s.setNom(f.getNom().trim());
        s.setQuartier(f.getQuartier().trim());
        s.setAdresse(f.getAdresse() == null || f.getAdresse().isBlank() ? null : f.getAdresse().trim());
        s.setCapaciteJournaliere(f.getCapaciteJournaliere());
    }
}
