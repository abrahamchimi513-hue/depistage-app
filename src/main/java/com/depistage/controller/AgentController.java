package com.depistage.controller;

import com.depistage.dto.AgentForm;
import com.depistage.enums.Fonction;
import com.depistage.exception.DepistageException;
import com.depistage.service.AgentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/agents")
@RequiredArgsConstructor
public class AgentController {

    private final AgentService service;

    @GetMapping
    public String liste(@RequestParam(defaultValue = "") String q,
                        @RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("page", service.rechercher(q, Paging.of(page, Paging.TAILLE, Sort.by("nom", "prenom"))));
        model.addAttribute("q", q);
        model.addAttribute("baseUrl", "/agents");
        model.addAttribute("qs", Paging.qs("q", q));
        return "agents/liste";
    }

    @GetMapping("/nouveau")
    public String nouveau(Model model) {
        model.addAttribute("agentForm", new AgentForm());
        preparer(model, "/agents", "Nouvel agent");
        return "agents/form";
    }

    @PostMapping
    public String creer(@Valid @ModelAttribute("agentForm") AgentForm form, BindingResult result,
                        Model model, RedirectAttributes ra) {
        if (!result.hasErrors()) {
            try {
                service.creer(form);
                ra.addFlashAttribute("succes", "L’agent a été créé.");
                return "redirect:/agents";
            } catch (DepistageException e) {
                FormErrors.apply(result, e);
            }
        }
        preparer(model, "/agents", "Nouvel agent");
        return "agents/form";
    }

    @GetMapping("/{id}/modifier")
    public String modifier(@PathVariable Long id, Model model) {
        model.addAttribute("agentForm", service.formulaire(id));
        preparer(model, "/agents/" + id, "Modifier l’agent");
        return "agents/form";
    }

    @PostMapping("/{id}")
    public String mettreAJour(@PathVariable Long id, @Valid @ModelAttribute("agentForm") AgentForm form,
                              BindingResult result, Model model, RedirectAttributes ra) {
        if (!result.hasErrors()) {
            try {
                service.modifier(id, form);
                ra.addFlashAttribute("succes", "L’agent a été modifié.");
                return "redirect:/agents";
            } catch (DepistageException e) {
                FormErrors.apply(result, e);
            }
        }
        preparer(model, "/agents/" + id, "Modifier l’agent");
        return "agents/form";
    }

    @PostMapping("/{id}/supprimer")
    public String supprimer(@PathVariable Long id, RedirectAttributes ra) {
        service.supprimer(id);
        ra.addFlashAttribute("succes", "L’agent a été supprimé.");
        return "redirect:/agents";
    }

    private void preparer(Model model, String action, String titre) {
        model.addAttribute("action", action);
        model.addAttribute("titre", titre);
        model.addAttribute("fonctions", Fonction.values());
    }
}
