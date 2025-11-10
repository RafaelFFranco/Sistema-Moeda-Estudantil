package br.edu.moedaestudantil.repository;

import br.edu.moedaestudantil.model.Vantagem;
import br.edu.moedaestudantil.model.EmpresaParceira;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VantagemRepository extends JpaRepository<Vantagem, Long> {
    List<Vantagem> findByEmpresa(EmpresaParceira empresa);
    List<Vantagem> findAllByOrderByNomeAsc();
}
