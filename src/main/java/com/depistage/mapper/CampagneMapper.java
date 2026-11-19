package com.depistage.mapper;

import com.depistage.dto.CampagneForm;
import com.depistage.dto.CampagneResponse;
import com.depistage.entity.Campagne;
import com.depistage.entity.TypeDepistage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CampagneMapper {

    public CampagneResponse toResponse(Campagne c, long realises) {
        List<String> codes = c.getTypes().stream().map(TypeDepistage::getCode).sorted().toList();
        return new CampagneResponse(c.getId(), c.getNom(), c.getVille(), c.getDateDebut(), c.getDateFin(),
                c.getObjectif(), c.getStatut(), codes, realises, pourcentage(realises, c.getObjectif()));
    }

    /** Pourcentage plafonné à 100, 0 si l'objectif est nul (jamais de division par zéro). */
    public static int pourcentage(long realises, int objectif) {
        if (objectif <= 0) {
            return 0;
        }
        return (int) Math.min(100, realises * 100 / objectif);
    }

    public CampagneForm toForm(Campagne c) {
        CampagneForm f = new CampagneForm();
        f.setNom(c.getNom());
        f.setVille(c.getVille());
        f.setDateDebut(c.getDateDebut());
        f.setDateFin(c.getDateFin());
        f.setObjectif(c.getObjectif());
        f.setStatut(c.getStatut());
        f.setTypeIds(new ArrayList<>(c.getTypes().stream().map(TypeDepistage::getId).toList()));
        return f;
    }

    /** Copie les champs simples ; les relations (types) sont résolues par le service. */
    public void apply(CampagneForm f, Campagne c) {
        c.setNom(f.getNom().trim());
        c.setVille(f.getVille().trim());
        c.setDateDebut(f.getDateDebut());
        c.setDateFin(f.getDateFin());
        c.setObjectif(f.getObjectif());
        c.setStatut(f.getStatut());
    }
}
