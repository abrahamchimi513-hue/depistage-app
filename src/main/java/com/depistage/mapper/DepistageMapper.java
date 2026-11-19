package com.depistage.mapper;

import com.depistage.dto.DepistageForm;
import com.depistage.dto.DepistageResponse;
import com.depistage.entity.Depistage;
import org.springframework.stereotype.Component;

@Component
public class DepistageMapper {

    public DepistageResponse toResponse(Depistage d) {
        return new DepistageResponse(
                d.getId(),
                d.getParticipant().getId(),
                d.getParticipant().getNom() + " " + d.getParticipant().getPrenom(),
                d.getAgent().getId(),
                d.getAgent().getNom() + " " + d.getAgent().getPrenom(),
                d.getSite().getId(),
                d.getSite().getNom(),
                d.getSite().getCampagne().getId(),
                d.getSite().getCampagne().getNom(),
                d.getType().getId(),
                d.getType().getLibelle(),
                d.getDateDepistage(),
                d.getResultat(),
                d.getValeurMesuree(),
                d.getOrientation(),
                d.getObservation());
    }

    public DepistageForm toForm(Depistage d) {
        DepistageForm f = new DepistageForm();
        f.setParticipantId(d.getParticipant().getId());
        f.setAgentId(d.getAgent().getId());
        f.setSiteId(d.getSite().getId());
        f.setTypeId(d.getType().getId());
        f.setDateDepistage(d.getDateDepistage());
        f.setResultat(d.getResultat());
        f.setValeurMesuree(d.getValeurMesuree());
        f.setOrientation(d.getOrientation());
        f.setObservation(d.getObservation());
        return f;
    }

    /** Copie les champs simples ; les quatre relations sont résolues par le service. */
    public void apply(DepistageForm f, Depistage d) {
        d.setDateDepistage(f.getDateDepistage());
        d.setResultat(f.getResultat());
        d.setValeurMesuree(f.getValeurMesuree());
        d.setOrientation(f.getOrientation() == null || f.getOrientation().isBlank() ? null : f.getOrientation().trim());
        d.setObservation(f.getObservation() == null || f.getObservation().isBlank() ? null : f.getObservation().trim());
    }
}
