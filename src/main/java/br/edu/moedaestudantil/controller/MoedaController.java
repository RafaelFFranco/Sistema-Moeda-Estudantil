package br.edu.moedaestudantil.controller;

import br.edu.moedaestudantil.model.Aluno;
import br.edu.moedaestudantil.model.Transacao;
import br.edu.moedaestudantil.model.Vantagem;
import br.edu.moedaestudantil.repository.TransacaoRepository;
import br.edu.moedaestudantil.service.AlunoService;
import br.edu.moedaestudantil.service.MoedaService;
import br.edu.moedaestudantil.service.ProfessorService;
import br.edu.moedaestudantil.service.EmailService;
import br.edu.moedaestudantil.service.VantagemService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Controller
@RequestMapping("/moedas") // mantenha o mapping atual do seu controller
public class MoedaController {

    private final MoedaService moedaService;
    private final VantagemService vantagemService; 
    private final ProfessorService professorService;
    private final TransacaoRepository transacaoRepository;
    private final AlunoService alunoService;
    private final EmailService emailService;

    // { changed code } adicione VantagemService ao construtor
    public MoedaController(MoedaService moedaService, VantagemService vantagemService, ProfessorService professorService,
                          TransacaoRepository transacaoRepository, AlunoService alunoService, EmailService emailService) {
        this.moedaService = moedaService;
        this.vantagemService = vantagemService;
        this.professorService = professorService;
        this.transacaoRepository = transacaoRepository;
        this.alunoService = alunoService;
        this.emailService = emailService;
    }

    // Adiciona a lista de vantagens ao model para as views deste controller
    @ModelAttribute("vantagens")
    public java.util.List<Vantagem> populateVantagens() {
        return vantagemService.findAll();
    }

    @GetMapping
    public String index(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean isProfessor = false;
        boolean isAluno = false;
        if (auth != null && auth.getAuthorities() != null) {
            isProfessor = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_PROFESSOR") || a.getAuthority().equals("PROFESSOR") );
            isAluno = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ALUNO") || a.getAuthority().equals("ALUNO") );
        }

        List<Transacao> transacoes = java.util.Collections.emptyList();

        if (isAluno && auth != null) {
            try {
                String username = auth.getName();
                Aluno aluno = alunoService.findByLogin(username).orElse(null);
                if (aluno != null) {
                    transacoes = transacaoRepository.findByAlunoOrigemIdOrAlunoDestinoIdOrderByDataHoraDesc(aluno.getId(), aluno.getId());
                }
            } catch (Exception e) {
                transacoes = transacaoRepository.findAllByOrderByDataHoraDesc();
            }
        } else if (isProfessor && auth != null) {
            try {
                String username = auth.getName();
                var professor = professorService.findByLogin(username).orElse(null);
                if (professor != null && professor.getInstituicao() != null) {
                    Long instId = professor.getInstituicao().getId();
                    transacoes = transacaoRepository.findAllByOrderByDataHoraDesc();
                    final Long fid = instId;
                    transacoes = transacoes.stream()
                            .filter(t -> t.getInstituicao() != null && fid.equals(t.getInstituicao().getId()))
                            .filter(t -> t.getTipo() != Transacao.TipoTransacao.REMOCAO && t.getTipo() != Transacao.TipoTransacao.RESGATE)
                            .toList();
                } else {
                    transacoes = transacaoRepository.findAllByOrderByDataHoraDesc();
                }
            } catch (Exception e) {
                transacoes = transacaoRepository.findAllByOrderByDataHoraDesc();
            }
        } else {
            transacoes = transacaoRepository.findAllByOrderByDataHoraDesc();
        }

        model.addAttribute("transacoes", transacoes);
        return "moeda/index";
    }

    // { changed code } Consolidado: apenas um handler GET para a página de trocar
    @GetMapping("/trocar")
    public String formTrocar(Model model) {
        // ...existing code que prepara dados de moeda (mantener) ...

        // adiciona lista de vantagens à view
        model.addAttribute("vantagens", vantagemService.findAll());

        // flags de permissão simples para a view (não substitui validação servidor-side)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isEmpresa = false;
        boolean isAluno = false;
        if (auth != null && auth.getAuthorities() != null) {
            isEmpresa = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_EMPRESA") || a.getAuthority().equals("EMPRESA") || a.getAuthority().equals("ROLE_COMPANY"));
            isAluno = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ALUNO") || a.getAuthority().equals("ALUNO") || a.getAuthority().equals("ROLE_STUDENT"));
        }
        model.addAttribute("isEmpresa", isEmpresa);
        model.addAttribute("isAluno", isAluno);

        // Se o usuário autenticado for um aluno, carregue o objeto Aluno para que a view mostre o saldo
        if (isAluno && auth != null) {
            try {
                String username = auth.getName();
                br.edu.moedaestudantil.model.Aluno aluno = alunoService.findByLogin(username).orElse(null);
                if (aluno != null) {
                    model.addAttribute("aluno", aluno);
                }
            } catch (Exception e) {
                // Não interrompe a renderização; caso de erro, view continuará exibindo 0
            }
        }

        return "moeda/trocar";
    }

    @PostMapping("/trocar")
    public String trocar(@RequestParam Long vantagemId, RedirectAttributes redirectAttributes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            Aluno aluno = alunoService.findByLogin(username).orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

            Vantagem v = vantagemService.findByIdWithEmpresaParceira(vantagemId).orElseThrow(() -> new RuntimeException("Vantagem não encontrada"));

            Integer custo = v.getCustoMoedas() != null ? v.getCustoMoedas() : 0;
            // efetua a dedução de saldo e grava transação
            moedaService.removerMoedas(aluno.getId(), custo, "Resgate: " + v.getNome());

            // gerar código de cupom simples (6 caracteres alfanuméricos)
            String codigoCupom = java.util.UUID.randomUUID().toString().replaceAll("[-]", "").substring(0, 8).toUpperCase();
            java.time.LocalDate validade = java.time.LocalDate.now().plusDays(30);
            java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");

            // montar mensagem de email no formato solicitado
            String nomeAluno = aluno.getNome() != null ? aluno.getNome() : username;
            String nomeVantagem = v.getNome() != null ? v.getNome() : "a vantagem";
            String nomeEmpresa = "a empresa";
            if (v.getEmpresaParceira() != null && v.getEmpresaParceira().getNome() != null) {
                nomeEmpresa = v.getEmpresaParceira().getNome();
            }

            // Gera token QR compartilhado entre email e UI (cupom|UUID)
            String qrContent = codigoCupom + "|" + java.util.UUID.randomUUID().toString();
            String qrDataUri = null;
            try {
                qrDataUri = emailService.generateQrDataUri(qrContent, 300, 300);
            } catch (Exception e) {
                // não bloqueia o fluxo
            }

            try {
                emailService.sendCupomEmail(aluno.getEmail(), nomeAluno, nomeVantagem, nomeEmpresa, codigoCupom, validade.format(fmt), v.getImageUrl(), qrContent);
            } catch (Exception e) {
                // não interrompe o fluxo principal
            }

            // Mensagem para UI e cupom como flash attribute
            redirectAttributes.addFlashAttribute("mensagem", "Resgate realizado: " + v.getNome());
            redirectAttributes.addFlashAttribute("tipoMensagem", "success");
            redirectAttributes.addFlashAttribute("cupom", codigoCupom);
            redirectAttributes.addFlashAttribute("cupomValidade", validade.format(fmt));
            if (qrDataUri != null) {
                redirectAttributes.addFlashAttribute("cupomQrData", qrDataUri);
            }

            // Se o usuário autenticado for um aluno, redirecionamos para /alunos/me
            boolean isAlunoRedirect = false;
            if (auth != null && auth.getAuthorities() != null) {
                isAlunoRedirect = auth.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ALUNO") || a.getAuthority().equals("ALUNO") || a.getAuthority().equals("ROLE_STUDENT"));
            }

            if (isAlunoRedirect) {
                return "redirect:/alunos/me";
            }

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

