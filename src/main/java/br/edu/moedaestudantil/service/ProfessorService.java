package br.edu.moedaestudantil.service;

import br.edu.moedaestudantil.model.Professor;
import br.edu.moedaestudantil.repository.ProfessorRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProfessorService {
    private final ProfessorRepository professorRepository;

    public ProfessorService(ProfessorRepository professorRepository) {
        this.professorRepository = professorRepository;
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

    public void deleteById(Long id) {
        professorRepository.deleteById(id);
    }
}
