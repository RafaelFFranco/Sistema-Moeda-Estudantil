package br.edu.moedaestudantil.controller;

import br.edu.moedaestudantil.model.Aluno;
import br.edu.moedaestudantil.model.Instituicao;
import br.edu.moedaestudantil.repository.InstituicaoRepository;
import br.edu.moedaestudantil.service.AlunoService;
import br.edu.moedaestudantil.repository.TransacaoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/alunos")
public class AlunoController {

    private final AlunoService alunoService;
    private final InstituicaoRepository instituicaoRepository;
    private final TransacaoRepository transacaoRepository;

    public AlunoController(AlunoService alunoService, InstituicaoRepository instituicaoRepository, TransacaoRepository transacaoRepository) {
        this.alunoService = alunoService;
        this.instituicaoRepository = instituicaoRepository;
        this.transacaoRepository = transacaoRepository;
    }

    @GetMapping
    @PreAuthorize("!hasRole('ALUNO')")
    public String list(@RequestParam(value = "q", required = false) String q, Model model) {
        model.addAttribute("alunos", alunoService.findByQuery(q));
        model.addAttribute("q", q);
        return "aluno/list";
    }

    @GetMapping("/novo")
    @PreAuthorize("!hasRole('ALUNO')")
    public String createForm(Model model) {
        model.addAttribute("aluno", new Aluno());
        model.addAttribute("instituicoes", instituicaoRepository.findAll());
        return "aluno/form";
    }

    @PostMapping("/salvar")
    @PreAuthorize("!hasRole('ALUNO')")
    public String save(@Valid @ModelAttribute("aluno") Aluno aluno, 
                      @RequestParam("instituicaoId") Long instituicaoId,
                      BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("instituicoes", instituicaoRepository.findAll());
            return "aluno/form";
        }
        
        // Set the institution from the selected ID
        if (instituicaoId != null) {
            Instituicao instituicao = instituicaoRepository.findById(instituicaoId)
                .orElseThrow(() -> new IllegalArgumentException("Instituição inválida"));
            aluno.setInstituicao(instituicao);
        }
        
        alunoService.save(aluno);
        return "redirect:/alunos";
    }

    @GetMapping("/editar/{id}")
    @PreAuthorize("!hasRole('ALUNO')")
    public String edit(@PathVariable Long id, Model model) {
        Aluno aluno = alunoService.findById(id).orElseThrow(() -> new IllegalArgumentException("Aluno inválido"));
        model.addAttribute("aluno", aluno);
        model.addAttribute("instituicoes", instituicaoRepository.findAll());
        return "aluno/form";
    }

    @GetMapping("/ver/{id}")
    @PreAuthorize("!hasRole('ALUNO')")
    public String view(@PathVariable Long id, Model model) {
        Aluno aluno = alunoService.findById(id).orElseThrow(() -> new IllegalArgumentException("Aluno inválido"));
        model.addAttribute("aluno", aluno);
        // Include transaction history for this student (viewable by non-aluno roles)
        model.addAttribute("transacoes", transacaoRepository.findByAlunoOrigemIdOrAlunoDestinoIdOrderByDataHoraDesc(aluno.getId(), aluno.getId()));
        return "aluno/view";
    }

    @GetMapping("/deletar/{id}")
    @PreAuthorize("!hasRole('ALUNO')")
    public String delete(@PathVariable Long id) {
        alunoService.deleteById(id);
        return "redirect:/alunos";
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('ALUNO')")
    public String myProfile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Aluno aluno = alunoService.findByLogin(username).orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado"));
        model.addAttribute("aluno", aluno);
        // Add transaction history for the logged-in student
        model.addAttribute("transacoes", transacaoRepository.findByAlunoOrigemIdOrAlunoDestinoIdOrderByDataHoraDesc(aluno.getId(), aluno.getId()));
        model.addAttribute("isAluno", true);
        return "aluno/view";
    }
}
