package com.depistage.controller;

import com.depistage.dto.TypeForm;
import com.depistage.exception.DepistageException;
import com.depistage.service.TypeDepistageService;
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
@RequestMapping("/types")
@RequiredArgsConstructor
public class TypeController {

    private final TypeDepistageService service;

    @GetMapping
    public String liste(@RequestParam(defaultValue = "") String q,
                        @RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("page", service.rechercher(q, Paging.of(page, Paging.TAILLE, Sort.by("libelle"))));
        model.addAttribute("q", q);
        model.addAttribute("baseUrl", "/types");
        model.addAttribute("qs", Paging.qs("q", q));
        return "types/liste";
    }

    @GetMapping("/nouveau")
    public String nouveau(Model model) {
        model.addAttribute("typeForm", new TypeForm());
        preparer(model, "/types", "Nouveau type de dépistage");
        return "types/form";
    }

    @PostMapping
    public String creer(@Valid @ModelAttribute("typeForm") TypeForm form, BindingResult result,
                        Model model, RedirectAttributes ra) {
        if (!result.hasErrors()) {
            try {
                service.creer(form);
                ra.addFlashAttribute("succes", "Le type de dépistage a été créé.");
                return "redirect:/types";
            } catch (DepistageException e) {
                FormErrors.apply(result, e);
            }
        }
        preparer(model, "/types", "Nouveau type de dépistage");
        return "types/form";
    }

    @GetMapping("/{id}/modifier")
    public String modifier(@PathVariable Long id, Model model) {
        model.addAttribute("typeForm", service.formulaire(id));
        preparer(model, "/types/" + id, "Modifier le type de dépistage");
        return "types/form";
    }

    @PostMapping("/{id}")
    public String mettreAJour(@PathVariable Long id, @Valid @ModelAttribute("typeForm") TypeForm form,
                              BindingResult result, Model model, RedirectAttributes ra) {
        if (!result.hasErrors()) {
            try {
                service.modifier(id, form);
                ra.addFlashAttribute("succes", "Le type de dépistage a été modifié.");
                return "redirect:/types";
            } catch (DepistageException e) {
                FormErrors.apply(result, e);
            }
        }
        preparer(model, "/types/" + id, "Modifier le type de dépistage");
        return "types/form";
    }

    @PostMapping("/{id}/supprimer")
    public String supprimer(@PathVariable Long id, RedirectAttributes ra) {
        service.supprimer(id);
        ra.addFlashAttribute("succes", "Le type de dépistage a été supprimé.");
        return "redirect:/types";
    }

    private void preparer(Model model, String action, String titre) {
        model.addAttribute("action", action);
        model.addAttribute("titre", titre);
    }
}
