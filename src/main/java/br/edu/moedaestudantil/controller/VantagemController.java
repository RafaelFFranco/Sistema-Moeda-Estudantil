package br.edu.moedaestudantil.controller;

import br.edu.moedaestudantil.model.Vantagem;
import br.edu.moedaestudantil.service.VantagemService;
import br.edu.moedaestudantil.service.EmpresaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class VantagemController {

    private final VantagemService vantagemService;
    private final EmpresaService empresaService;

    public VantagemController(VantagemService vantagemService, EmpresaService empresaService) {
        this.vantagemService = vantagemService;
        this.empresaService = empresaService;
    }

    // lista pública (alunos)
    @GetMapping({"/vantagens", "/vantagem/list", "/vantagems"})
    public String list(Model model) {
        model.addAttribute("vantagens", vantagemService.findAll());
        return "vantagem/list";
    }

    // formulário genérico (sem empresa selecionada)
    @GetMapping({"/vantagem/form", "/vantagens/form"})
    public String form(Model model) {
        model.addAttribute("vantagem", new Vantagem());
        model.addAttribute("empresas", empresaService.findAll());
        return "vantagem/form";
    }

    // formulário dentro do contexto de uma empresa (ex.: botão da empresa)
    @GetMapping("/empresa/{empresaId}/vantagem/form")
    public String formForEmpresa(@PathVariable Long empresaId, Model model) {
        Vantagem v = new Vantagem();
        model.addAttribute("vantagem", v);
        model.addAttribute("empresaId", empresaId);
        model.addAttribute("empresas", empresaService.findAll());
        return "vantagem/form";
    }

    // salvar via formulário genérico
    private void normalizeCusto(Vantagem vantagem) {
        if (vantagem.getCustoMoedas() == null) {
            vantagem.setCustoMoedas(0);
        }
    }

    @PostMapping({"/vantagem/salvar", "/vantagens/salvar"})
    public String salvar(
            @ModelAttribute Vantagem vantagem,
            @RequestParam(value = "empresaParceiraId", required = false) Long empresaId
    ) {
        normalizeCusto(vantagem);
        if (empresaId != null) {
            empresaService.findById(empresaId).ifPresent(vantagem::setEmpresaParceira);
            vantagemService.save(vantagem);
            return "redirect:/empresa/" + empresaId; // volta para dashboard da empresa
        } else {
            vantagemService.save(vantagem);
            return "redirect:/vantagens";
        }
    }

    // salvar quando form for enviado a partir de /empresa/{id}/vantagem
    @PostMapping("/empresa/{empresaId}/vantagem")
    public String salvarParaEmpresa(@PathVariable Long empresaId, @ModelAttribute Vantagem vantagem) {
        normalizeCusto(vantagem);
        empresaService.findById(empresaId).ifPresent(vantagem::setEmpresaParceira);
        vantagemService.save(vantagem);
        return "redirect:/empresa/" + empresaId;
    }

    // { changed code } delete genérico -> redireciona para lista de vantagens
    @PostMapping("/vantagem/{id}/delete")
    public String delete(@PathVariable Long id) {
        vantagemService.deleteById(id);
        return "redirect:/vantagens";
    }

    // delete vindo do dashboard da empresa -> redireciona para dashboard da empresa
    @PostMapping("/empresa/{empresaId}/vantagem/{id}/delete")
    public String deleteFromEmpresa(@PathVariable Long empresaId, @PathVariable Long id) {
        vantagemService.deleteById(id);
        return "redirect:/empresa/" + empresaId;
    }

}