package br.edu.moedaestudantil.controller;

import br.edu.moedaestudantil.service.EmpresaService;
import br.edu.moedaestudantil.service.VantagemService;
import jakarta.validation.Valid;
import br.edu.moedaestudantil.model.EmpresaParceira;
import br.edu.moedaestudantil.model.Vantagem;

import java.lang.reflect.Method;
import java.security.Principal;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping({"/empresa", "/empresas"}) // aceita ambos para evitar 404s vindos dos templates
public class EmpresaController {

    private final EmpresaService empresaService;
    private final VantagemService vantagemService;

    public EmpresaController(EmpresaService empresaService, VantagemService vantagemService) {
        this.empresaService = empresaService;
        this.vantagemService = vantagemService;
    }

    // lista de empresas (compatível com /empresa/list)
    @GetMapping({ "", "/list" })
    public String list(Model model) {
        model.addAttribute("empresas", empresaService.findAll());
        return "empresa/list";
    }

    // dashboard de uma empresa por id (ex.: /empresa/5)
    @GetMapping("/{id}")
    public String dashboard(@PathVariable Long id, Model model) {
        EmpresaParceira e = empresaService.findById(id).orElse(null);
        model.addAttribute("empresa", e);
        // se tiver um método para buscar vantagens por empresa, use-o; caso contrário use vantagemService.findAll() e filtre
        model.addAttribute("vantagens", vantagemService.findByEmpresaId(id));
        return "dashboard-empresa";
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

    /**
     * Tenta popular a empresa a partir do model (se já presente), do usuário autenticado (Principal)
     * ou, como último recurso, pega a primeira empresa cadastrada (ambiente de desenvolvimento).
     */
    @ModelAttribute("empresa")
    public EmpresaParceira populateEmpresa(Model model, Principal principal) {
        Object existing = model.getAttribute("empresa");
        if (existing instanceof EmpresaParceira) {
            return (EmpresaParceira) existing;
        }

        EmpresaParceira byPrincipal = tryFindEmpresaByPrincipal(principal);
        if (byPrincipal != null) {
            return byPrincipal;
        }

        try {
            List<EmpresaParceira> all = empresaService.findAll();
            if (all != null && !all.isEmpty()) return all.get(0);
        } catch (Exception ignored) { }

        return null;
    }

    private EmpresaParceira tryFindEmpresaByPrincipal(Principal principal) {
        if (principal == null) return null;
        String username = principal.getName();
        // tenta métodos comuns no service de forma segura por reflection
        String[] methodNames = {"findByEmail", "findByUsuario", "findByLogin", "findByUsername", "findByUser", "findByUserEmail", "findByUsernameOrEmail"};
        for (String name : methodNames) {
            try {
                Method m = empresaService.getClass().getMethod(name, String.class);
                Object res = m.invoke(empresaService, username);
                EmpresaParceira e = extractEmpresaFromResult(res);
                if (e != null) return e;
            } catch (NoSuchMethodException ignored) {
                // método não existe, tenta próximo
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    private EmpresaParceira extractEmpresaFromResult(Object res) {
        if (res == null) return null;
        if (res instanceof EmpresaParceira) return (EmpresaParceira) res;
        if (res instanceof java.util.Optional) {
            try {
                Object val = ((java.util.Optional<?>) res).orElse(null);
                if (val instanceof EmpresaParceira) return (EmpresaParceira) val;
            } catch (Exception ignored) { }
        }
        return null;
    }

    /**
     * Exponha as vantagens da empresa atual como "minhasVantagens" para as views.
     */
    @ModelAttribute("minhasVantagens")
    public List<Vantagem> populateMinhasVantagens(@ModelAttribute("empresa") EmpresaParceira empresa) {
        if (empresa == null || empresa.getId() == null) return Collections.emptyList();
        try {
            return vantagemService.findByEmpresaId(empresa.getId());
        } catch (Exception ignored) {
            return Collections.emptyList();
        }
    }

    /**
     * Painel/dashboard da empresa — garante que os atributos necessários estejam no model.
     * Mapeamentos adaptáveis: /empresa/painel e /empresa/dashboard
     */
    @GetMapping({"/painel", "/dashboard"})
    public String painelEmpresa(Model model, Principal principal) {
        // ModelAttributes já populam "empresa" e "minhasVantagens".
        // adiciona totalEmpresas se a view usar esse atributo
        if (model.getAttribute("totalEmpresas") == null) {
            try {
                long count = 0;
                try { count = empresaService.count(); } catch (Exception ex) {
                    List<EmpresaParceira> all = empresaService.findAll();
                    count = (all == null) ? 0 : all.size();
                }
                model.addAttribute("totalEmpresas", count);
            } catch (Exception ignored) { model.addAttribute("totalEmpresas", 0); }
        }
        return "dashboard-empresa";
    }

    // --- INÍCIO: métodos adicionados para exibir/cadastrar vantagem a partir do painel da empresa ---
    /**
     * Mostrar formulário de cadastro de vantagem no contexto da empresa.
     */
    @GetMapping("/{id}/vantagem/new")
    public String newVantagemForm(@PathVariable("id") Long empresaId, Model model, Principal principal) {
        EmpresaParceira empresa = empresaService.findById(empresaId).get();
        if (empresa == null) {
            return "redirect:/empresa/list";
        }
        // expõe empresa e objeto Vantagem para o template já existente em /templates/vantagem/form.html
        model.addAttribute("empresa", empresa);
        model.addAttribute("empresaId", empresaId);
        model.addAttribute("vantagem", new Vantagem());
        return "vantagem/form";
    }

    /**
     * Recebe submissão do formulário de vantagem e associa à empresa, salvando.
     */
    @PostMapping("/{id}/vantagem")
    public String createVantagemFromEmpresa(@PathVariable("id") Long empresaId, @ModelAttribute Vantagem vantagem) {
        EmpresaParceira empresa = empresaService.findById(empresaId).get();
        if (empresa != null) {
            // tenta setar relacionamento de forma compatível com possíveis nomes do setter
            try {
                Method m = Vantagem.class.getMethod("setEmpresaParceira", EmpresaParceira.class);
                m.invoke(vantagem, empresa);
            } catch (NoSuchMethodException e1) {
                try {
                    Method m2 = Vantagem.class.getMethod("setEmpresa", EmpresaParceira.class);
                    m2.invoke(vantagem, empresa);
                } catch (Exception ignored) { }
            } catch (Exception ignored) { }
        }
        vantagemService.save(vantagem);
        // redireciona de volta para o painel/visualização da empresa
        return "redirect:/empresa/painel";
    }
    // --- FIM: métodos adicionados ---
}
