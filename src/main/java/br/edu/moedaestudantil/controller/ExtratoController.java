package br.edu.moedaestudantil.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ExtratoController {

    // Extrato foi considerado redundante com a página de Moedas; redireciona para /moedas
    @GetMapping("/extrato")
    public String extrato() {
        return "redirect:/moedas";
    }
}
