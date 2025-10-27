package br.edu.moedaestudantil.controller;

import br.edu.moedaestudantil.model.Aluno;
import br.edu.moedaestudantil.model.Transacao;
import br.edu.moedaestudantil.service.AlunoService;
import br.edu.moedaestudantil.service.MoedaService;
import br.edu.moedaestudantil.repository.TransacaoRepository;
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
    
    public MoedaController(AlunoService alunoService, MoedaService moedaService, TransacaoRepository transacaoRepository) {
        this.alunoService = alunoService;
        this.moedaService = moedaService;
        this.transacaoRepository = transacaoRepository;
    }
    
    @GetMapping
    public String index(Model model) {
        List<Transacao> transacoes = transacaoRepository.findAllByOrderByDataHoraDesc();
        model.addAttribute("transacoes", transacoes);
        return "moeda/index";
    }
    
    @GetMapping("/transferir")
    public String formTransferir(Model model) {
        model.addAttribute("alunos", alunoService.findAll());
        return "moeda/transferir";
    }
    
    @PostMapping("/transferir")
    public String transferir(@RequestParam Long alunoOrigemId,
                           @RequestParam Long alunoDestinoId,
                           @RequestParam Integer quantidade,
                           @RequestParam(required = false) String descricao,
                           RedirectAttributes redirectAttributes) {
        try {
            moedaService.transferirMoedas(alunoOrigemId, alunoDestinoId, quantidade, descricao);
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
        return "moeda/adicionar";
    }
    
    @PostMapping("/adicionar/{id}")
    public String adicionar(@PathVariable Long id,
                          @RequestParam Integer quantidade,
                          @RequestParam(required = false) String descricao,
                          RedirectAttributes redirectAttributes) {
        try {
            moedaService.adicionarMoedas(id, quantidade, descricao);
            redirectAttributes.addFlashAttribute("mensagem", "Moedas adicionadas com sucesso!");
            redirectAttributes.addFlashAttribute("tipoMensagem", "success");
            return "redirect:/alunos/ver/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagem", "Erro: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensagem", "error");
            return "redirect:/moedas/adicionar/" + id;
        }
    }
    
    @GetMapping("/remover/{id}")
    public String formRemover(@PathVariable Long id, Model model) {
        Aluno aluno = alunoService.findById(id).orElseThrow();
        model.addAttribute("aluno", aluno);
        return "moeda/remover";
    }
    
    @PostMapping("/remover/{id}")
    public String remover(@PathVariable Long id,
                        @RequestParam Integer quantidade,
                        @RequestParam(required = false) String descricao,
                        RedirectAttributes redirectAttributes) {
        try {
            moedaService.removerMoedas(id, quantidade, descricao);
            redirectAttributes.addFlashAttribute("mensagem", "Moedas removidas com sucesso!");
            redirectAttributes.addFlashAttribute("tipoMensagem", "success");
            return "redirect:/alunos/ver/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagem", "Erro: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensagem", "error");
            return "redirect:/moedas/remover/" + id;
        }
    }
}

