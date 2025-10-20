package br.edu.moedaestudantil.repository;

import br.edu.moedaestudantil.model.EmpresaParceira;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EmpresaRepository extends JpaRepository<EmpresaParceira, Long> {
    Optional<EmpresaParceira> findByEmail(String email);

    java.util.List<EmpresaParceira> findByNomeContainingIgnoreCaseOrEmailContainingIgnoreCase(String nome, String email);
}
