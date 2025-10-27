package br.edu.moedaestudantil.controller;

import br.edu.moedaestudantil.service.AlunoService;
import br.edu.moedaestudantil.service.EmpresaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final AlunoService alunoService;
    private final EmpresaService empresaService;

    public HomeController(AlunoService alunoService, EmpresaService empresaService) {
        this.alunoService = alunoService;
        this.empresaService = empresaService;
    }

    @GetMapping("/")
    public String home(Model model) {
        // Conta total de alunos
        long totalAlunos = alunoService.findAll().size();
        
        // Conta total de empresas
        long totalEmpresas = empresaService.findAll().size();
        
        // Calcula moedas em circulação (soma dos saldos dos alunos)
        long moedasCirculacao = alunoService.findAll().stream()
            .mapToLong(aluno -> aluno.getSaldoMoedas() != null ? aluno.getSaldoMoedas() : 0L)
            .sum();
        
        model.addAttribute("totalAlunos", totalAlunos);
        model.addAttribute("totalEmpresas", totalEmpresas);
        model.addAttribute("moedasCirculacao", moedasCirculacao);
        
        return "index";
    }
}
