package com.depistage.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * Gestionnaire d'erreurs des PAGES : renvoie du HTML (page d'erreur) ou une redirection avec message.
 * Il est séparé de celui de l'API (ApiExceptionHandler), qui renvoie du JSON : avec un seul gestionnaire,
 * l'API se mettrait à répondre en HTML.
 */
@ControllerAdvice(basePackages = "com.depistage.controller")
public class PageExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(PageExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public String introuvable(ResourceNotFoundException e, Model model, HttpServletResponse response) {
        response.setStatus(HttpStatus.NOT_FOUND.value());
        model.addAttribute("message", e.getMessage());
        return "error/404";
    }

    /** Doublon, règle violée ou suppression refusée : retour à la liste d'origine avec une explication. */
    @ExceptionHandler({DuplicateException.class, BusinessRuleException.class, DeletionRefusedException.class,
            DataIntegrityViolationException.class})
    public String conflit(Exception e, HttpServletRequest request) {
        String message = e instanceof DepistageException
                ? e.getMessage()
                : "L’opération entre en conflit avec des données existantes.";
        RequestContextUtils.getOutputFlashMap(request).put("erreur", message);
        return "redirect:" + listeDOrigine(request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public String parametreInvalide(Model model, HttpServletResponse response) {
        response.setStatus(HttpStatus.NOT_FOUND.value());
        model.addAttribute("message", "Cette adresse ne correspond à aucune ressource.");
        return "error/404";
    }

    @ExceptionHandler(Exception.class)
    public String autre(Exception e, Model model, HttpServletRequest request, HttpServletResponse response) {
        if (e instanceof ErrorResponse er && er.getStatusCode().is4xxClientError()) {
            response.setStatus(er.getStatusCode().value());
            model.addAttribute("message", "La requête n’a pas pu être traitée.");
            return er.getStatusCode().value() == 404 ? "error/404" : "error/erreur";
        }
        // Le détail technique reste dans les journaux, jamais dans la page
        log.error("Erreur interne sur {} {}", request.getMethod(), request.getRequestURI(), e);
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("message", "Une erreur est survenue. Veuillez réessayer plus tard.");
        return "error/500";
    }

    /** « /sites/5/supprimer » → « /sites ». */
    private String listeDOrigine(HttpServletRequest request) {
        String[] segments = request.getRequestURI().split("/");
        return segments.length > 1 && !segments[1].isBlank() ? "/" + segments[1] : "/";
    }
}
