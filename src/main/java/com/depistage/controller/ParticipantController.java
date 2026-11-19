package com.depistage.controller;

import com.depistage.dto.ParticipantForm;
import com.depistage.enums.Sexe;
import com.depistage.exception.DepistageException;
import com.depistage.service.ParticipantService;
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
@RequestMapping("/participants")
@RequiredArgsConstructor
public class ParticipantController {

    /** Pages courtes pour que la pagination soit visible même avec peu de données. */
    private static final int TAILLE = 5;

    private final ParticipantService service;

    @GetMapping
    public String liste(@RequestParam(defaultValue = "") String q,
                        @RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("page", service.rechercher(q, Paging.of(page, TAILLE, Sort.by("nom", "prenom", "id"))));
        model.addAttribute("q", q);
        model.addAttribute("baseUrl", "/participants");
        model.addAttribute("qs", Paging.qs("q", q));
        return "participants/liste";
    }

    @GetMapping("/nouveau")
    public String nouveau(Model model) {
        model.addAttribute("participantForm", new ParticipantForm());
        preparer(model, "/participants", "Nouveau participant");
        return "participants/form";
    }

    @PostMapping
    public String creer(@Valid @ModelAttribute("participantForm") ParticipantForm form, BindingResult result,
                        Model model, RedirectAttributes ra) {
        if (!result.hasErrors()) {
            try {
                service.creer(form);
                ra.addFlashAttribute("succes", "Le participant a été créé.");
                return "redirect:/participants";
            } catch (DepistageException e) {
                FormErrors.apply(result, e);
            }
        }
        preparer(model, "/participants", "Nouveau participant");
        return "participants/form";
    }

    @GetMapping("/{id}/modifier")
    public String modifier(@PathVariable Long id, Model model) {
        model.addAttribute("participantForm", service.formulaire(id));
        preparer(model, "/participants/" + id, "Modifier le participant");
        return "participants/form";
    }

    @PostMapping("/{id}")
    public String mettreAJour(@PathVariable Long id, @Valid @ModelAttribute("participantForm") ParticipantForm form,
                              BindingResult result, Model model, RedirectAttributes ra) {
        if (!result.hasErrors()) {
            try {
                service.modifier(id, form);
                ra.addFlashAttribute("succes", "Le participant a été modifié.");
                return "redirect:/participants";
            } catch (DepistageException e) {
                FormErrors.apply(result, e);
            }
        }
        preparer(model, "/participants/" + id, "Modifier le participant");
        return "participants/form";
    }

    @PostMapping("/{id}/supprimer")
    public String supprimer(@PathVariable Long id, RedirectAttributes ra) {
        service.supprimer(id);
        ra.addFlashAttribute("succes", "Le participant a été supprimé.");
        return "redirect:/participants";
    }

    private void preparer(Model model, String action, String titre) {
        model.addAttribute("action", action);
        model.addAttribute("titre", titre);
        model.addAttribute("sexes", Sexe.values());
    }
}
