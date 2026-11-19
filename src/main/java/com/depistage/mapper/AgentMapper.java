package com.depistage.mapper;

import com.depistage.dto.AgentForm;
import com.depistage.dto.AgentResponse;
import com.depistage.entity.Agent;
import org.springframework.stereotype.Component;

@Component
public class AgentMapper {

    public AgentResponse toResponse(Agent a) {
        return new AgentResponse(a.getId(), a.getNom(), a.getPrenom(), a.getTelephone(), a.getFonction(), a.isActif());
    }

    public AgentForm toForm(Agent a) {
        AgentForm f = new AgentForm();
        f.setNom(a.getNom());
        f.setPrenom(a.getPrenom());
        f.setTelephone(a.getTelephone());
        f.setFonction(a.getFonction());
        f.setActif(a.isActif());
        return f;
    }

    public void apply(AgentForm f, Agent a) {
        a.setNom(f.getNom().trim());
        a.setPrenom(f.getPrenom().trim());
        a.setTelephone(f.getTelephone().trim());
        a.setFonction(f.getFonction());
        a.setActif(f.isActif());
    }
}
