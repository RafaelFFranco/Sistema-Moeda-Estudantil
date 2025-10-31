package br.edu.moedaestudantil.repository;

import br.edu.moedaestudantil.model.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProfessorRepository extends JpaRepository<Professor, Long> {
    Optional<Professor> findByEmail(String email);
    Optional<Professor> findByCpf(String cpf);
    Optional<Professor> findByLogin(String login);
    java.util.List<Professor> findByNomeContainingIgnoreCaseOrEmailContainingIgnoreCase(String nome, String email);
}
