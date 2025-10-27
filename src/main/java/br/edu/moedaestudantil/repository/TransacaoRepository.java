package br.edu.moedaestudantil.repository;

import br.edu.moedaestudantil.model.Aluno;
import br.edu.moedaestudantil.model.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {
    
    List<Transacao> findByAlunoOrigemOrAlunoDestinoOrderByDataHoraDesc(Aluno alunoOrigem, Aluno alunoDestino);
    
    List<Transacao> findByAlunoOrigemIdOrAlunoDestinoIdOrderByDataHoraDesc(Long alunoOrigemId, Long alunoDestinoId);
    
    List<Transacao> findAllByOrderByDataHoraDesc();
}

