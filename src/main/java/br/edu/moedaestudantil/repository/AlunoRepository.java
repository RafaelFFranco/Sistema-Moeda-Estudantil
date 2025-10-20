package br.edu.moedaestudantil.repository;

import br.edu.moedaestudantil.model.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {
    Optional<Aluno> findByEmail(String email);
    Optional<Aluno> findByCpf(String cpf);
    
    // Search by name or email (case-insensitive, contains)
    java.util.List<Aluno> findByNomeContainingIgnoreCaseOrEmailContainingIgnoreCase(String nome, String email);
}
