package br.edu.moedaestudantil.controller;

import br.edu.moedaestudantil.service.AlunoService;
import br.edu.moedaestudantil.service.EmpresaService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import br.edu.moedaestudantil.model.Aluno;
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
        // Redireciona para painel específico conforme role
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities() != null) {
            boolean isAluno = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ALUNO".equals(a.getAuthority()));
            boolean isProfessor = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_PROFESSOR".equals(a.getAuthority()));
            boolean isEmpresa = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_EMPRESA".equals(a.getAuthority()));

            if (isAluno) {
                // Fornece dados simplificados para o painel do aluno
                String username = auth.getName();
                Aluno aluno = alunoService.findByLogin(username).orElse(null);
                model.addAttribute("aluno", aluno);
                return "dashboard-aluno";
            }

            if (isProfessor) {
                // Dados gerais para professor
                long totalAlunos = alunoService.findAll().size();
                model.addAttribute("totalAlunos", totalAlunos);
                return "dashboard-professor";
            }

            if (isEmpresa) {
                long totalEmpresas = empresaService.findAll().size();
                model.addAttribute("totalEmpresas", totalEmpresas);
                return "dashboard-empresa";
            }
        }

        // Fallback: painel geral (admin-like)
        long totalAlunos = alunoService.findAll().size();
        long totalEmpresas = empresaService.findAll().size();
        long moedasCirculacao = alunoService.findAll().stream()
            .mapToLong(aluno -> aluno.getSaldoMoedas() != null ? aluno.getSaldoMoedas() : 0L)
            .sum();

        model.addAttribute("totalAlunos", totalAlunos);
        model.addAttribute("totalEmpresas", totalEmpresas);
        model.addAttribute("moedasCirculacao", moedasCirculacao);

        return "index";
    }
}
