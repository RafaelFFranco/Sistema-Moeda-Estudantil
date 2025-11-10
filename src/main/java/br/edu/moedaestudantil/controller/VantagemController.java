package br.edu.moedaestudantil.controller;

import br.edu.moedaestudantil.model.Vantagem;
import br.edu.moedaestudantil.model.EmpresaParceira;
import br.edu.moedaestudantil.model.Aluno;
import br.edu.moedaestudantil.service.VantagemService;
import br.edu.moedaestudantil.service.EmpresaService;
import br.edu.moedaestudantil.service.AlunoService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;

import java.util.List;

@Controller
@RequestMapping("/vantagens")
public class VantagemController {

    private final VantagemService vantagemService;
    private final EmpresaService empresaService;
    private final AlunoService alunoService;

    public VantagemController(VantagemService vantagemService, EmpresaService empresaService, AlunoService alunoService) {
        this.vantagemService = vantagemService;
        this.empresaService = empresaService;
        this.alunoService = alunoService;
    }

    // Listagem de vantagens para alunos (todas as vantagens disponíveis)
    @GetMapping("/listar")
    @PreAuthorize("hasRole('ALUNO')")
    public String listarParaAluno(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Aluno aluno = alunoService.findByLogin(username)
            .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));
        
        List<Vantagem> vantagens = vantagemService.findAll();
        model.addAttribute("vantagens", vantagens);
        model.addAttribute("aluno", aluno);
        return "vantagem/list-aluno";
    }

    // Listagem de vantagens da empresa autenticada
    @GetMapping("/minhas")
    @PreAuthorize("hasRole('EMPRESA')")
    public String listarMinhasVantagens(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        EmpresaParceira empresa = empresaService.findByLogin(username)
            .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));

        List<Vantagem> vantagens = vantagemService.findByEmpresa(empresa);
        model.addAttribute("vantagens", vantagens);
        model.addAttribute("empresa", empresa);
        return "vantagem/list-empresa";
    }

    // Formulário para criar nova vantagem (empresa)
    @GetMapping("/nova")
    @PreAuthorize("hasRole('EMPRESA')")
    public String formNovaVantagem(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        EmpresaParceira empresa = empresaService.findByLogin(username)
            .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));

        Vantagem vantagem = new Vantagem();
        vantagem.setEmpresa(empresa);
        model.addAttribute("vantagem", vantagem);
        return "vantagem/form";
    }

    // Salvar nova vantagem
    @PostMapping("/salvar")
    @PreAuthorize("hasRole('EMPRESA')")
    public String salvar(@Valid @ModelAttribute("vantagem") Vantagem vantagem,
                        BindingResult bindingResult,
                        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "vantagem/form";
        }

        // Garantir que a vantagem pertence à empresa autenticada
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        EmpresaParceira empresa = empresaService.findByLogin(username)
            .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));
        
        // Se estiver editando, verificar se a vantagem pertence à empresa autenticada
        if (vantagem.getId() != null) {
            Vantagem vantagemExistente = vantagemService.findById(vantagem.getId())
                .orElseThrow(() -> new IllegalArgumentException("Vantagem não encontrada"));
            
            if (!vantagemExistente.getEmpresa().getId().equals(empresa.getId())) {
                redirectAttributes.addFlashAttribute("mensagem", "Você não tem permissão para editar esta vantagem.");
                redirectAttributes.addFlashAttribute("tipoMensagem", "error");
                return "redirect:/vantagens/minhas";
            }
        }
        
        vantagem.setEmpresa(empresa);
        vantagemService.save(vantagem);

        String mensagem = vantagem.getId() != null ? "Vantagem atualizada com sucesso!" : "Vantagem cadastrada com sucesso!";
        redirectAttributes.addFlashAttribute("mensagem", mensagem);
        redirectAttributes.addFlashAttribute("tipoMensagem", "success");
        return "redirect:/vantagens/minhas";
    }

    // Formulário para editar vantagem
    @GetMapping("/editar/{id}")
    @PreAuthorize("hasRole('EMPRESA')")
    public String formEditarVantagem(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Vantagem vantagem = vantagemService.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Vantagem não encontrada"));

        // Verificar se a vantagem pertence à empresa autenticada
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        EmpresaParceira empresa = empresaService.findByLogin(username)
            .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));

        if (!vantagem.getEmpresa().getId().equals(empresa.getId())) {
            redirectAttributes.addFlashAttribute("mensagem", "Você não tem permissão para editar esta vantagem.");
            redirectAttributes.addFlashAttribute("tipoMensagem", "error");
            return "redirect:/vantagens/minhas";
        }

        model.addAttribute("vantagem", vantagem);
        return "vantagem/form";
    }

    // Deletar vantagem
    @GetMapping("/deletar/{id}")
    @PreAuthorize("hasRole('EMPRESA')")
    public String deletar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Vantagem vantagem = vantagemService.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Vantagem não encontrada"));

        // Verificar se a vantagem pertence à empresa autenticada
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        EmpresaParceira empresa = empresaService.findByLogin(username)
            .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));

        if (!vantagem.getEmpresa().getId().equals(empresa.getId())) {
            redirectAttributes.addFlashAttribute("mensagem", "Você não tem permissão para deletar esta vantagem.");
            redirectAttributes.addFlashAttribute("tipoMensagem", "error");
            return "redirect:/vantagens/minhas";
        }

        vantagemService.deleteById(id);
        redirectAttributes.addFlashAttribute("mensagem", "Vantagem deletada com sucesso!");
        redirectAttributes.addFlashAttribute("tipoMensagem", "success");
        return "redirect:/vantagens/minhas";
    }
}
