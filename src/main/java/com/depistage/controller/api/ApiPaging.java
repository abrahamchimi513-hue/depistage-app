package com.depistage.controller.api;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

final class ApiPaging {

    private ApiPaging() {
    }

    /** Page à partir de 0, taille bornée entre 1 et 100 pour éviter les abus. */
    static Pageable of(int page, int size, Sort sort) {
        return PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100), sort);
    }
}
