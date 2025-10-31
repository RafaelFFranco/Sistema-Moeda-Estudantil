package br.edu.moedaestudantil.controller;

import br.edu.moedaestudantil.model.Instituicao;
import br.edu.moedaestudantil.model.Professor;
import br.edu.moedaestudantil.repository.InstituicaoRepository;
import br.edu.moedaestudantil.service.ProfessorService;
import org.springframework.stereotype.Controller;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/professores")
@PreAuthorize("!hasRole('ALUNO')")
public class ProfessorController {

    private final ProfessorService professorService;
    private final InstituicaoRepository instituicaoRepository;

    public ProfessorController(ProfessorService professorService, InstituicaoRepository instituicaoRepository) {
        this.professorService = professorService;
        this.instituicaoRepository = instituicaoRepository;
    }

    @GetMapping
    public String list(@RequestParam(value = "q", required = false) String q, Model model) {
        model.addAttribute("professores", professorService.findByQuery(q));
        model.addAttribute("q", q);
        return "professor/list";
    }

    @GetMapping("/novo")
    public String createForm(Model model) {
        model.addAttribute("professor", new Professor());
        model.addAttribute("instituicoes", instituicaoRepository.findAll());
        return "professor/form";
    }

    @PostMapping("/salvar")
    public String save(@Valid @ModelAttribute("professor") Professor professor,
                       @RequestParam("instituicaoId") Long instituicaoId,
                       BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("instituicoes", instituicaoRepository.findAll());
            return "professor/form";
        }

        if (instituicaoId != null) {
            Instituicao instituicao = instituicaoRepository.findById(instituicaoId)
                    .orElseThrow(() -> new IllegalArgumentException("Instituição inválida"));
            professor.setInstituicao(instituicao);
        }

        professorService.save(professor);
        return "redirect:/professores";
    }

    @GetMapping("/editar/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Professor professor = professorService.findById(id).orElseThrow(() -> new IllegalArgumentException("Professor inválido"));
        model.addAttribute("professor", professor);
        model.addAttribute("instituicoes", instituicaoRepository.findAll());
        return "professor/form";
    }

    @GetMapping("/ver/{id}")
    public String view(@PathVariable Long id, Model model) {
        Professor professor = professorService.findById(id).orElseThrow(() -> new IllegalArgumentException("Professor inválido"));
        model.addAttribute("professor", professor);
        return "professor/view";
    }

    @GetMapping("/deletar/{id}")
    public String delete(@PathVariable Long id) {
        professorService.deleteById(id);
        return "redirect:/professores";
    }
}
