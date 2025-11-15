package br.edu.moedaestudantil.repository;

import br.edu.moedaestudantil.model.Vantagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;

public interface VantagemRepository extends JpaRepository<Vantagem, Long> {
    // buscar vantagens por id da empresa parceira
    Collection<Vantagem> findByEmpresaParceira_Id(Long empresaParceiraId);

    // busca vantagem por id já trazendo a empresaParceira (evita LazyInitializationException)
    @Query("select v from Vantagem v left join fetch v.empresaParceira where v.id = :id")
    Optional<Vantagem> findByIdWithEmpresaParceira(@Param("id") Long id);
}
