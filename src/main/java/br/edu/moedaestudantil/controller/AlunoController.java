package br.edu.moedaestudantil.controller;

import br.edu.moedaestudantil.model.Aluno;
import br.edu.moedaestudantil.model.Instituicao;
import br.edu.moedaestudantil.repository.InstituicaoRepository;
import br.edu.moedaestudantil.service.AlunoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/alunos")
public class AlunoController {

    private final AlunoService alunoService;
    private final InstituicaoRepository instituicaoRepository;

    public AlunoController(AlunoService alunoService, InstituicaoRepository instituicaoRepository) {
        this.alunoService = alunoService;
        this.instituicaoRepository = instituicaoRepository;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("alunos", alunoService.findAll());
        return "aluno/list";
    }

    @GetMapping("/novo")
    public String createForm(Model model) {
        model.addAttribute("aluno", new Aluno());
        model.addAttribute("instituicoes", instituicaoRepository.findAll());
        return "aluno/form";
    }

    @PostMapping("/salvar")
    public String save(@Valid @ModelAttribute("aluno") Aluno aluno, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("instituicoes", instituicaoRepository.findAll());
            return "aluno/form";
        }
        alunoService.save(aluno);
        return "redirect:/alunos";
    }

    @GetMapping("/editar/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Aluno aluno = alunoService.findById(id).orElseThrow(() -> new IllegalArgumentException("Aluno inválido"));
        model.addAttribute("aluno", aluno);
        model.addAttribute("instituicoes", instituicaoRepository.findAll());
        return "aluno/form";
    }

    @GetMapping("/ver/{id}")
    public String view(@PathVariable Long id, Model model) {
        Aluno aluno = alunoService.findById(id).orElseThrow(() -> new IllegalArgumentException("Aluno inválido"));
        model.addAttribute("aluno", aluno);
        return "aluno/view";
    }

    @GetMapping("/deletar/{id}")
    public String delete(@PathVariable Long id) {
        alunoService.deleteById(id);
        return "redirect:/alunos";
    }
}
