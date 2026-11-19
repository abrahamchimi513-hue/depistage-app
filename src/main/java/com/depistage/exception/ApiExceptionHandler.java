package com.depistage.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Gestionnaire d'erreurs de l'API : toutes les erreurs ont le même JSON (voir ApiError).
 * Prioritaire sur celui des pages pour les contrôleurs du paquet controller.api.
 */
@RestControllerAdvice(basePackages = "com.depistage.controller.api")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(DepistageException.class)
    public ResponseEntity<ApiError> metier(DepistageException e, HttpServletRequest request) {
        Map<String, String> details = e.getField() == null ? Map.of() : Map.of(e.getField(), e.getMessage());
        return reponse(e.getStatus(), e.getStatus().name(), e.getMessage(), details, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> validation(MethodArgumentNotValidException e, HttpServletRequest request) {
        Map<String, String> details = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(fe -> details.putIfAbsent(fe.getField(), fe.getDefaultMessage()));
        e.getBindingResult().getGlobalErrors()
                .forEach(ge -> details.putIfAbsent(ge.getObjectName(), ge.getDefaultMessage()));
        return reponse(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Les données envoyées sont invalides.", details, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> corpsIllisible(HttpServletRequest request) {
        return reponse(HttpStatus.BAD_REQUEST, "BAD_REQUEST",
                "Le corps de la requête est absent ou n’est pas un JSON valide (vérifiez les dates et les valeurs des listes).",
                Map.of(), request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> parametreInvalide(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        return reponse(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "Le paramètre « " + e.getName() + " » a une valeur invalide.",
                Map.of(e.getName(), "Valeur invalide"), request);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiError> parametreManquant(MissingServletRequestParameterException e, HttpServletRequest request) {
        return reponse(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "Le paramètre « " + e.getParameterName() + " » est obligatoire.",
                Map.of(e.getParameterName(), "Paramètre obligatoire"), request);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> methodeInterdite(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        return reponse(HttpStatus.METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED",
                "Cette méthode HTTP n’est pas autorisée sur cette adresse.", Map.of(), request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> integrite(HttpServletRequest request) {
        return reponse(HttpStatus.CONFLICT, "CONFLICT", "L’opération entre en conflit avec des données existantes.",
                Map.of(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> autre(Exception e, HttpServletRequest request) {
        // Le détail technique reste dans les journaux, jamais dans la réponse
        log.error("Erreur interne sur {} {}", request.getMethod(), request.getRequestURI(), e);
        return reponse(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR",
                "Une erreur interne est survenue. Veuillez réessayer plus tard.", Map.of(), request);
    }

    private ResponseEntity<ApiError> reponse(HttpStatus status, String code, String message,
                                             Map<String, String> details, HttpServletRequest request) {
        ApiError erreur = new ApiError(Instant.now(), status.value(), code, message, request.getRequestURI(), details);
        return ResponseEntity.status(status).body(erreur);
    }
}
