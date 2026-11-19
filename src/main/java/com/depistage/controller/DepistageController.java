package com.depistage.controller;

import com.depistage.dto.DepistageForm;
import com.depistage.enums.Resultat;
import com.depistage.exception.DepistageException;
import com.depistage.service.AgentService;
import com.depistage.service.CampagneService;
import com.depistage.service.DepistageService;
import com.depistage.service.ParticipantService;
import com.depistage.service.SiteService;
import com.depistage.service.TypeDepistageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/depistages")
@RequiredArgsConstructor
public class DepistageController {

    private final DepistageService service;
    private final CampagneService campagneService;
    private final TypeDepistageService typeService;
    private final SiteService siteService;
    private final AgentService agentService;
    private final ParticipantService participantService;

    @GetMapping
    public String liste(@RequestParam(required = false) Long campagneId,
                        @RequestParam(required = false) Long typeId,
                        @RequestParam(required = false) Resultat resultat,
                        @RequestParam(defaultValue = "0") int page, Model model) {
        // Pas de page 0 forcée : un filtre qui change renvoie à la page 0 car le formulaire n'envoie pas « page »
        var tri = Sort.by(Sort.Direction.DESC, "dateDepistage").and(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("page", service.rechercher(campagneId, typeId, resultat, Paging.of(page, Paging.TAILLE, tri)));
        model.addAttribute("campagneId", campagneId);
        model.addAttribute("typeId", typeId);
        model.addAttribute("resultat", resultat);
        model.addAttribute("campagnes", campagneService.options());
        model.addAttribute("types", typeService.options());
        model.addAttribute("resultats", Resultat.values());
        model.addAttribute("baseUrl", "/depistages");
        model.addAttribute("qs", Paging.qs("campagneId", campagneId, "typeId", typeId, "resultat", resultat));
        return "depistages/liste";
    }

    @GetMapping("/nouveau")
    public String nouveau(Model model) {
        model.addAttribute("depistageForm", new DepistageForm());
        preparer(model);
        return "depistages/form";
    }

    @PostMapping
    public String creer(@Valid @ModelAttribute("depistageForm") DepistageForm form, BindingResult result,
                        Model model, RedirectAttributes ra) {
        if (!result.hasErrors()) {
            try {
                service.creer(form);
                ra.addFlashAttribute("succes", "Le dépistage a été enregistré.");
                return "redirect:/depistages"; // Post-Redirect-Get : un rafraîchissement ne renvoie rien
            } catch (DepistageException e) {
                FormErrors.apply(result, e);
            }
        }
        preparer(model);
        return "depistages/form";
    }

    private void preparer(Model model) {
        model.addAttribute("titre", "Enregistrer un dépistage");
        model.addAttribute("sites", siteService.optionsCampagnesEnCours());
        model.addAttribute("agents", agentService.optionsActifs());
        model.addAttribute("types", typeService.optionsCampagnesEnCours());
        model.addAttribute("participants", participantService.options());
        model.addAttribute("resultats", Resultat.values());
    }
}
