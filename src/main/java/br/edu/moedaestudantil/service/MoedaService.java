package br.edu.moedaestudantil.service;

import br.edu.moedaestudantil.model.Aluno;
import br.edu.moedaestudantil.model.Professor;
import br.edu.moedaestudantil.model.Transacao;
import br.edu.moedaestudantil.repository.AlunoRepository;
import br.edu.moedaestudantil.repository.TransacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class MoedaService {
    
    private final AlunoRepository alunoRepository;
    private final TransacaoRepository transacaoRepository;
    private final br.edu.moedaestudantil.repository.ProfessorRepository professorRepository;
    private final EmailService emailService;

    public MoedaService(AlunoRepository alunoRepository, TransacaoRepository transacaoRepository, br.edu.moedaestudantil.repository.ProfessorRepository professorRepository, EmailService emailService) {
        this.alunoRepository = alunoRepository;
        this.transacaoRepository = transacaoRepository;
        this.professorRepository = professorRepository;
        this.emailService = emailService;
    }
    
    @Transactional
    public void transferirMoedas(Long alunoOrigemId, Long alunoDestinoId, Integer quantidade, String descricao) {
        // Método mantido para compatibilidade (transferência aluno->aluno).
        Aluno alunoOrigem = alunoRepository.findById(alunoOrigemId)
            .orElseThrow(() -> new RuntimeException("Aluno origem não encontrado"));

        Aluno alunoDestino = alunoRepository.findById(alunoDestinoId)
            .orElseThrow(() -> new RuntimeException("Aluno destino não encontrado"));

        if (alunoOrigem.getSaldoMoedas() < quantidade) {
            throw new RuntimeException("Saldo insuficiente. Saldo atual: " + alunoOrigem.getSaldoMoedas());
        }

        if (quantidade <= 0) {
            throw new RuntimeException("Quantidade deve ser maior que zero");
        }

        // Deduz do aluno origem
        alunoOrigem.setSaldoMoedas(alunoOrigem.getSaldoMoedas() - quantidade);
        alunoRepository.save(alunoOrigem);

        // Adiciona ao aluno destino
        alunoDestino.setSaldoMoedas(alunoDestino.getSaldoMoedas() + quantidade);
        alunoRepository.save(alunoDestino);

        // Registra transação
        Transacao transacao = new Transacao();
        transacao.setAlunoOrigem(alunoOrigem);
        transacao.setAlunoDestino(alunoDestino);
        transacao.setQuantidadeMoedas(quantidade);
        transacao.setTipo(Transacao.TipoTransacao.TRANSFERENCIA);
        transacao.setDescricao(descricao != null ? descricao : "Transferência entre alunos");
        transacao.setDataHora(LocalDateTime.now());
        transacao.setInstituicao(alunoOrigem.getInstituicao());

        transacaoRepository.save(transacao);
    }

    @Transactional
    public void transferirDeProfessorParaAluno(Long professorId, Long alunoDestinoId, Integer quantidade, String descricao) {
        Professor professor = professorRepository.findById(professorId)
            .orElseThrow(() -> new RuntimeException("Professor não encontrado"));

        Aluno alunoDestino = alunoRepository.findById(alunoDestinoId)
            .orElseThrow(() -> new RuntimeException("Aluno destino não encontrado"));

        if (professor.getSaldoMoedas() == null || professor.getSaldoMoedas() < quantidade) {
            throw new RuntimeException("Saldo insuficiente do professor. Saldo atual: " + (professor.getSaldoMoedas() == null ? 0 : professor.getSaldoMoedas()));
        }

        if (quantidade <= 0) {
            throw new RuntimeException("Quantidade deve ser maior que zero");
        }

        // Deduz do professor
        professor.setSaldoMoedas(professor.getSaldoMoedas() - quantidade);
        professorRepository.save(professor);

        // Adiciona ao aluno destino
        alunoDestino.setSaldoMoedas(alunoDestino.getSaldoMoedas() + quantidade);
        alunoRepository.save(alunoDestino);

        // Registra transação (alunoOrigem fica null, descrição aponta para professor)
        Transacao transacao = new Transacao();
        transacao.setAlunoOrigem(null);
        transacao.setAlunoDestino(alunoDestino);
        transacao.setQuantidadeMoedas(quantidade);
        // Marca transação como transferência feita por professor
        transacao.setTipo(Transacao.TipoTransacao.PROFESSOR_PARA_ALUNO);
        transacao.setDescricao(descricao != null ? descricao : "Transferência do professor: " + professor.getNome());
        transacao.setDataHora(LocalDateTime.now());
        transacao.setInstituicao(professor.getInstituicao());


        transacaoRepository.save(transacao);

        String mensagem_aluno = "Olá, " + alunoDestino.getNome() + "! Você acaba de receber " + transacao.getQuantidadeMoedas() + " moedas do professor " + professor.getNome();
        String mensagem_professor = "Olá, " + professor.getNome() + "! Você enviou " + transacao.getQuantidadeMoedas() + " moedas para " + alunoDestino.getNome();
        emailService.sendEmail(professor.getEmail(), mensagem_professor);
        emailService.sendEmail(alunoDestino.getEmail(), mensagem_aluno);
    }
    
    @Transactional
    public void adicionarMoedas(Long alunoId, Integer quantidade, String descricao) {
        // Função de adicionar moedas foi removida do sistema — não implementada.
        throw new UnsupportedOperationException("Adicionar moedas não é permitido neste sistema.");
    }
    
    @Transactional
    public void removerMoedas(Long alunoId, Integer quantidade, String descricao) {
        Aluno aluno = alunoRepository.findById(alunoId)
            .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));
        
        if (aluno.getSaldoMoedas() < quantidade) {
            throw new RuntimeException("Saldo insuficiente. Saldo atual: " + aluno.getSaldoMoedas());
        }
        
        if (quantidade <= 0) {
            throw new RuntimeException("Quantidade deve ser maior que zero");
        }
        
        aluno.setSaldoMoedas(aluno.getSaldoMoedas() - quantidade);
        alunoRepository.save(aluno);
        
        // Registra transação
        Transacao transacao = new Transacao();
        transacao.setAlunoOrigem(aluno);
        transacao.setQuantidadeMoedas(quantidade);
        transacao.setTipo(Transacao.TipoTransacao.REMOCAO);
        transacao.setDescricao(descricao != null ? descricao : "Remoção manual de moedas");
        transacao.setDataHora(LocalDateTime.now());
        transacao.setInstituicao(aluno.getInstituicao());
        
        transacaoRepository.save(transacao);
    }
}

