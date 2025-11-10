package br.edu.moedaestudantil.repository;

import br.edu.moedaestudantil.model.Vantagem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface VantagemRepository extends JpaRepository<Vantagem, Long> {
    // buscar vantagens por id da empresa parceira
    Collection<Vantagem> findByEmpresaParceira_Id(Long empresaParceiraId);
}
