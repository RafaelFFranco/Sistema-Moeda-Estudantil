package br.edu.moedaestudantil.repository;

import br.edu.moedaestudantil.model.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {
    Optional<Aluno> findByEmail(String email);
    Optional<Aluno> findByCpf(String cpf);
    Optional<Aluno> findByLogin(String login);
    
    // Search by name or email (case-insensitive, contains)
    java.util.List<Aluno> findByNomeContainingIgnoreCaseOrEmailContainingIgnoreCase(String nome, String email);
    
    // Find all students belonging to a given institution (by institution id)
    java.util.List<Aluno> findByInstituicaoId(Long instituicaoId);
}
