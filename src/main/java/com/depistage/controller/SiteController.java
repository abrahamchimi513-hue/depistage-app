package com.depistage.controller;

import com.depistage.dto.SiteForm;
import com.depistage.exception.DepistageException;
import com.depistage.service.CampagneService;
import com.depistage.service.SiteService;
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
@RequestMapping("/sites")
@RequiredArgsConstructor
public class SiteController {

    private final SiteService service;
    private final CampagneService campagneService;

    @GetMapping
    public String liste(@RequestParam(defaultValue = "") String q,
                        @RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("page", service.rechercher(q, Paging.of(page, Paging.TAILLE, Sort.by("nom"))));
        model.addAttribute("q", q);
        model.addAttribute("baseUrl", "/sites");
        model.addAttribute("qs", Paging.qs("q", q));
        return "sites/liste";
    }

    @GetMapping("/nouveau")
    public String nouveau(Model model) {
        model.addAttribute("siteForm", new SiteForm());
        preparer(model, "/sites", "Nouveau site");
        return "sites/form";
    }

    @PostMapping
    public String creer(@Valid @ModelAttribute("siteForm") SiteForm form, BindingResult result,
                        Model model, RedirectAttributes ra) {
        if (!result.hasErrors()) {
            try {
                service.creer(form);
                ra.addFlashAttribute("succes", "Le site a été créé.");
                return "redirect:/sites";
            } catch (DepistageException e) {
                FormErrors.apply(result, e);
            }
        }
        preparer(model, "/sites", "Nouveau site");
        return "sites/form";
    }

    @GetMapping("/{id}/modifier")
    public String modifier(@PathVariable Long id, Model model) {
        model.addAttribute("siteForm", service.formulaire(id));
        preparer(model, "/sites/" + id, "Modifier le site");
        return "sites/form";
    }

    @PostMapping("/{id}")
    public String mettreAJour(@PathVariable Long id, @Valid @ModelAttribute("siteForm") SiteForm form,
                              BindingResult result, Model model, RedirectAttributes ra) {
        if (!result.hasErrors()) {
            try {
                service.modifier(id, form);
                ra.addFlashAttribute("succes", "Le site a été modifié.");
                return "redirect:/sites";
            } catch (DepistageException e) {
                FormErrors.apply(result, e);
            }
        }
        preparer(model, "/sites/" + id, "Modifier le site");
        return "sites/form";
    }

    @PostMapping("/{id}/supprimer")
    public String supprimer(@PathVariable Long id, RedirectAttributes ra) {
        service.supprimer(id);
        ra.addFlashAttribute("succes", "Le site a été supprimé.");
        return "redirect:/sites";
    }

    private void preparer(Model model, String action, String titre) {
        model.addAttribute("action", action);
        model.addAttribute("titre", titre);
        model.addAttribute("campagnes", campagneService.options());
    }
}
