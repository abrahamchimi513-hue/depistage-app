package com.depistage.controller;

import com.depistage.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final StatsService statsService;

    @GetMapping("/")
    public String tableauDeBord(Model model) {
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("stats", statsService.globales());
        return "index";
    }
}
