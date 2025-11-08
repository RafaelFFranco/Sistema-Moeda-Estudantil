package br.edu.moedaestudantil.service;

import br.edu.moedaestudantil.model.Professor;
import br.edu.moedaestudantil.repository.ProfessorRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProfessorService {
    private final ProfessorRepository professorRepository;
    private final PasswordEncoder passwordEncoder;

    public ProfessorService(ProfessorRepository professorRepository, PasswordEncoder passwordEncoder) {
        this.professorRepository = professorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Professor> findAll() {
        return professorRepository.findAll();
    }

    public List<Professor> findByQuery(String q) {
        if (q == null || q.isBlank()) return findAll();
        return professorRepository.findByNomeContainingIgnoreCaseOrEmailContainingIgnoreCase(q, q);
    }

    public Optional<Professor> findById(Long id) {
        return professorRepository.findById(id);
    }

    public Optional<Professor> findByLogin(String login) {
        return professorRepository.findByLogin(login);
    }

    public Professor save(Professor professor) {
        // Gerenciar senha
        if (professor.getId() != null) {
            Optional<Professor> professorExistente = professorRepository.findById(professor.getId());
            if (professorExistente.isPresent()) {
                Professor professorAntigo = professorExistente.get();
                if (professor.getSenha() == null || professor.getSenha().isEmpty()) {
                    professor.setSenha(professorAntigo.getSenha());
                } else if (!isPasswordEncrypted(professor.getSenha())) {
                    professor.setSenha(passwordEncoder.encode(professor.getSenha()));
                }
            }
        } else {
            if (professor.getSenha() != null && !professor.getSenha().isEmpty() && !isPasswordEncrypted(professor.getSenha())) {
                professor.setSenha(passwordEncoder.encode(professor.getSenha()));
            }
        }

        // Se for um professor novo (id == null) e saldo não informado, inicializa com 1000 moedas.
        if (professor.getId() == null) {
            if (professor.getSaldoMoedas() == null) {
                professor.setSaldoMoedas(1000);
            }
        }
        // Se for um professor existente sem saldo (null), garantir ao menos 1000.
        else {
            if (professor.getSaldoMoedas() == null) {
                professor.setSaldoMoedas(1000);
            }
        }
        return professorRepository.save(professor);
    }

    private boolean isPasswordEncrypted(String password) {
        return password != null && (password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$"));
    }

    public void deleteById(Long id) {
        professorRepository.deleteById(id);
    }
}
