package com.depistage.controller;

import com.depistage.dto.CampagneForm;
import com.depistage.enums.StatutCampagne;
import com.depistage.exception.DepistageException;
import com.depistage.service.CampagneService;
import com.depistage.service.SiteService;
import com.depistage.service.StatsService;
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
@RequestMapping("/campagnes")
@RequiredArgsConstructor
public class CampagneController {

    private final CampagneService service;
    private final TypeDepistageService typeService;
    private final SiteService siteService;
    private final StatsService statsService;

    @GetMapping
    public String liste(@RequestParam(defaultValue = "") String q,
                        @RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("page", service.rechercher(q, Paging.of(page, Paging.TAILLE, Sort.by("nom"))));
        model.addAttribute("q", q);
        model.addAttribute("baseUrl", "/campagnes");
        model.addAttribute("qs", Paging.qs("q", q));
        return "campagnes/liste";
    }

    @GetMapping("/{id}")
    public String fiche(@PathVariable Long id, Model model) {
        model.addAttribute("campagne", service.trouver(id));
        model.addAttribute("sites", siteService.listerParCampagne(id));
        model.addAttribute("stats", statsService.pourCampagne(id));
        return "campagnes/fiche";
    }

    @GetMapping("/nouvelle")
    public String nouvelle(Model model) {
        model.addAttribute("campagneForm", new CampagneForm());
        preparer(model, "/campagnes", "Nouvelle campagne");
        return "campagnes/form";
    }

    @PostMapping
    public String creer(@Valid @ModelAttribute("campagneForm") CampagneForm form, BindingResult result,
                        Model model, RedirectAttributes ra) {
        if (!result.hasErrors()) {
            try {
                service.creer(form);
                ra.addFlashAttribute("succes", "La campagne a été créée.");
                return "redirect:/campagnes";
            } catch (DepistageException e) {
                FormErrors.apply(result, e);
            }
        }
        preparer(model, "/campagnes", "Nouvelle campagne");
        return "campagnes/form";
    }

    @GetMapping("/{id}/modifier")
    public String modifier(@PathVariable Long id, Model model) {
        model.addAttribute("campagneForm", service.formulaire(id));
        preparer(model, "/campagnes/" + id, "Modifier la campagne");
        return "campagnes/form";
    }

    @PostMapping("/{id}")
    public String mettreAJour(@PathVariable Long id, @Valid @ModelAttribute("campagneForm") CampagneForm form,
                              BindingResult result, Model model, RedirectAttributes ra) {
        if (!result.hasErrors()) {
            try {
                service.modifier(id, form);
                ra.addFlashAttribute("succes", "La campagne a été modifiée.");
                return "redirect:/campagnes/" + id;
            } catch (DepistageException e) {
                FormErrors.apply(result, e);
            }
        }
        preparer(model, "/campagnes/" + id, "Modifier la campagne");
        return "campagnes/form";
    }

    @PostMapping("/{id}/supprimer")
    public String supprimer(@PathVariable Long id, RedirectAttributes ra) {
        service.supprimer(id); // un refus (DeletionRefusedException) est géré par le gestionnaire global
        ra.addFlashAttribute("succes", "La campagne a été supprimée.");
        return "redirect:/campagnes";
    }

    private void preparer(Model model, String action, String titre) {
        model.addAttribute("action", action);
        model.addAttribute("titre", titre);
        model.addAttribute("types", typeService.options());
        model.addAttribute("statuts", StatutCampagne.values());
    }
}
