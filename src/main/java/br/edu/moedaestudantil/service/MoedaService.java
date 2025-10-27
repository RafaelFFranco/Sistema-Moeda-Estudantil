package br.edu.moedaestudantil.service;

import br.edu.moedaestudantil.model.Aluno;
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
    
    public MoedaService(AlunoRepository alunoRepository, TransacaoRepository transacaoRepository) {
        this.alunoRepository = alunoRepository;
        this.transacaoRepository = transacaoRepository;
    }
    
    @Transactional
    public void transferirMoedas(Long alunoOrigemId, Long alunoDestinoId, Integer quantidade, String descricao) {
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
    public void adicionarMoedas(Long alunoId, Integer quantidade, String descricao) {
        Aluno aluno = alunoRepository.findById(alunoId)
            .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));
        
        if (quantidade <= 0) {
            throw new RuntimeException("Quantidade deve ser maior que zero");
        }
        
        aluno.setSaldoMoedas(aluno.getSaldoMoedas() + quantidade);
        alunoRepository.save(aluno);
        
        // Registra transação
        Transacao transacao = new Transacao();
        transacao.setAlunoDestino(aluno);
        transacao.setQuantidadeMoedas(quantidade);
        transacao.setTipo(Transacao.TipoTransacao.ADICAO);
        transacao.setDescricao(descricao != null ? descricao : "Adição manual de moedas");
        transacao.setDataHora(LocalDateTime.now());
        transacao.setInstituicao(aluno.getInstituicao());
        
        transacaoRepository.save(transacao);
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

