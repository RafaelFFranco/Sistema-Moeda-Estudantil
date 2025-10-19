package br.edu.moedaestudantil.repository;

import br.edu.moedaestudantil.model.Instituicao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstituicaoRepository extends JpaRepository<Instituicao, Long> {
}
