package br.edu.moedaestudantil.controller;

import br.edu.moedaestudantil.model.Aluno;
import br.edu.moedaestudantil.model.Transacao;
import br.edu.moedaestudantil.service.AlunoService;
import br.edu.moedaestudantil.service.MoedaService;
import br.edu.moedaestudantil.repository.TransacaoRepository;
import br.edu.moedaestudantil.model.Vantagem;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/moedas")
public class MoedaController {
    
    private final AlunoService alunoService;
    private final MoedaService moedaService;
    private final TransacaoRepository transacaoRepository;
    private final br.edu.moedaestudantil.service.VantagemService vantagemService;
    private final br.edu.moedaestudantil.service.ProfessorService professorService;

    public MoedaController(AlunoService alunoService, MoedaService moedaService, TransacaoRepository transacaoRepository, br.edu.moedaestudantil.service.VantagemService vantagemService, br.edu.moedaestudantil.service.ProfessorService professorService) {
        this.alunoService = alunoService;
        this.moedaService = moedaService;
        this.transacaoRepository = transacaoRepository;
        this.vantagemService = vantagemService;
        this.professorService = professorService;
    }

    
    @GetMapping
    public String index(Model model) {
        List<Transacao> transacoes = transacaoRepository.findAllByOrderByDataHoraDesc();
        model.addAttribute("transacoes", transacoes);
        return "moeda/index";
    }

    @GetMapping("/trocar")
    public String formTrocar(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Aluno aluno = alunoService.findByLogin(username).orElseThrow(() -> new RuntimeException("Aluno não encontrado"));
        model.addAttribute("aluno", aluno);
        model.addAttribute("vantagens", vantagemService.findAll());
        return "moeda/trocar";
    }

    @PostMapping("/trocar")
    public String trocar(@RequestParam Long vantagemId, RedirectAttributes redirectAttributes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            Aluno aluno = alunoService.findByLogin(username).orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

            Vantagem v = vantagemService.findById(vantagemId);
            if (v == null) throw new RuntimeException("Vantagem não encontrada");

            Integer custo = v.getCustoMoedas() != null ? v.getCustoMoedas() : 0;
            moedaService.removerMoedas(aluno.getId(), custo, "Resgate: " + v.getNome());

            redirectAttributes.addFlashAttribute("mensagem", "Resgate realizado: " + v.getNome());
            redirectAttributes.addFlashAttribute("tipoMensagem", "success");
            return "redirect:/alunos/ver/" + aluno.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagem", "Erro: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensagem", "error");
            return "redirect:/moedas/trocar";
        }
    }
    
    @GetMapping("/transferir")
    public String formTransferir(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isProfessor = auth != null && auth.getAuthorities() != null && auth.getAuthorities().stream().anyMatch(a -> "ROLE_PROFESSOR".equals(a.getAuthority()));
        if (!isProfessor) {
            // Somente professores podem transferir para alunos pelo fluxo definido
            return "redirect:/";
        }

        String username = auth.getName();
        br.edu.moedaestudantil.model.Professor professor = professorService.findByLogin(username).orElse(null);

        // Filtra alunos para mostrar somente os que pertencem à mesma instituição do professor autenticado
        java.util.List<Aluno> alunos;
        if (professor == null || professor.getInstituicao() == null) {
            alunos = java.util.Collections.emptyList();
        } else {
            Long instituicaoId = professor.getInstituicao().getId();
            alunos = alunoService.findByInstituicaoId(instituicaoId);
        }

        model.addAttribute("alunos", alunos);
        model.addAttribute("isProfessor", true);
        model.addAttribute("professor", professor);
        return "moeda/transferir";
    }
    
    @PostMapping("/transferir")
    public String transferir(@RequestParam Long alunoDestinoId,
                           @RequestParam Integer quantidade,
                           @RequestParam(required = false) String descricao,
                           RedirectAttributes redirectAttributes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getAuthorities() == null || auth.getAuthorities().stream().noneMatch(a -> "ROLE_PROFESSOR".equals(a.getAuthority()))) {
                throw new RuntimeException("Apenas professores podem realizar transferências para alunos.");
            }

            String username = auth.getName();
            br.edu.moedaestudantil.model.Professor professor = professorService.findByLogin(username).orElseThrow(() -> new RuntimeException("Professor autenticado não encontrado"));

            // Executa transferência professor -> aluno usando professor autenticado
            moedaService.transferirDeProfessorParaAluno(professor.getId(), alunoDestinoId, quantidade, descricao);

            redirectAttributes.addFlashAttribute("mensagem", "Transferência realizada com sucesso!");
            redirectAttributes.addFlashAttribute("tipoMensagem", "success");
            return "redirect:/moedas";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagem", "Erro: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensagem", "error");
            return "redirect:/moedas/transferir";
        }
    }
    
    @GetMapping("/adicionar/{id}")
    public String formAdicionar(@PathVariable Long id, Model model) {
        Aluno aluno = alunoService.findById(id).orElseThrow();
        model.addAttribute("aluno", aluno);
        // Função de adicionar moedas removida — não é permitida
        return "redirect:/alunos/ver/" + id;
    }
    
    @PostMapping("/adicionar/{id}")
    public String adicionar(@PathVariable Long id,
                          @RequestParam Integer quantidade,
                          @RequestParam(required = false) String descricao,
                          RedirectAttributes redirectAttributes) {
        // Removido: não é permitido adicionar moedas manualmente
        redirectAttributes.addFlashAttribute("mensagem", "Operação removida: adicionar moedas não é permitida.");
        redirectAttributes.addFlashAttribute("tipoMensagem", "error");
        return "redirect:/alunos/ver/" + id;
    }
    
    @GetMapping("/remover/{id}")
    public String formRemover(@PathVariable Long id, Model model) {
        // Interface de remoção manual desabilitada. Redireciona para a view do aluno.
        return "redirect:/alunos/ver/" + id;
    }
    
    @PostMapping("/remover/{id}")
    public String remover(@PathVariable Long id,
                        @RequestParam Integer quantidade,
                        @RequestParam(required = false) String descricao,
                        RedirectAttributes redirectAttributes) {
        // Remoção manual desabilitada — operação não permitida via UI.
        redirectAttributes.addFlashAttribute("mensagem", "Operação removida: remover moedas não é permitida via interface.");
        redirectAttributes.addFlashAttribute("tipoMensagem", "error");
        return "redirect:/alunos/ver/" + id;
    }
}

