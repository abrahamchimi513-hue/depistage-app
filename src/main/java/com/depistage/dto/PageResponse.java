package com.depistage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

import java.util.List;

@Schema(description = "Une page de résultats")
public record PageResponse<T>(
        @Schema(description = "Éléments de la page") List<T> content,
        @Schema(description = "Numéro de la page, à partir de 0", example = "0") int page,
        @Schema(description = "Nombre maximum d'éléments par page", example = "10") int size,
        @Schema(description = "Nombre total d'éléments, toutes pages confondues", example = "2") long totalElements,
        @Schema(description = "Nombre total de pages", example = "1") int totalPages) {

    public static <T> PageResponse<T> of(Page<T> page) {
        return new PageResponse<>(page.getContent(), page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages());
    }
}
