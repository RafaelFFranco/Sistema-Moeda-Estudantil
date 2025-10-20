package br.edu.moedaestudantil.controller;

import br.edu.moedaestudantil.model.EmpresaParceira;
import br.edu.moedaestudantil.service.EmpresaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/empresas")
public class EmpresaController {

    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    @GetMapping
    public String list(@RequestParam(value = "q", required = false) String q, Model model) {
        model.addAttribute("empresas", empresaService.findByQuery(q));
        model.addAttribute("q", q);
        return "empresa/list";
    }

    @GetMapping("/nova")
    public String createForm(Model model) {
        model.addAttribute("empresa", new EmpresaParceira());
        return "empresa/form";
    }

    @PostMapping("/salvar")
    public String save(@Valid @ModelAttribute("empresa") EmpresaParceira empresa, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "empresa/form";
        }
        empresaService.save(empresa);
        return "redirect:/empresas";
    }

    @GetMapping("/editar/{id}")
    public String edit(@PathVariable Long id, Model model) {
        EmpresaParceira empresa = empresaService.findById(id).orElseThrow(() -> new IllegalArgumentException("Empresa inválida"));
        model.addAttribute("empresa", empresa);
        return "empresa/form";
    }

    @GetMapping("/ver/{id}")
    public String view(@PathVariable Long id, Model model) {
        EmpresaParceira empresa = empresaService.findById(id).orElseThrow(() -> new IllegalArgumentException("Empresa inválida"));
        model.addAttribute("empresa", empresa);
        return "empresa/view";
    }

    @GetMapping("/deletar/{id}")
    public String delete(@PathVariable Long id) {
        empresaService.deleteById(id);
        return "redirect:/empresas";
    }
}
