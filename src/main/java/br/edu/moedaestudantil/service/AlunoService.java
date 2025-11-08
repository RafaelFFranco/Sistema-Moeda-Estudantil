package br.edu.moedaestudantil.service;

import br.edu.moedaestudantil.model.Aluno;
import br.edu.moedaestudantil.repository.AlunoRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AlunoService {
    private final AlunoRepository alunoRepository;
    private final PasswordEncoder passwordEncoder;

    public AlunoService(AlunoRepository alunoRepository, PasswordEncoder passwordEncoder) {
        this.alunoRepository = alunoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Aluno> findAll() { 
        return alunoRepository.findAll(); 
    }

    public List<Aluno> findByInstituicaoId(Long instituicaoId) {
        if (instituicaoId == null) return java.util.Collections.emptyList();
        return alunoRepository.findByInstituicaoId(instituicaoId);
    }

    public List<Aluno> findByQuery(String q) {
        if (q == null || q.isBlank()) return findAll();
        return alunoRepository.findByNomeContainingIgnoreCaseOrEmailContainingIgnoreCase(q, q);
    }

    public Optional<Aluno> findById(Long id) { 
        return alunoRepository.findById(id); 
    }

    public Optional<Aluno> findByLogin(String login) {
        return alunoRepository.findByLogin(login);
    }

    public Aluno save(Aluno aluno) {
        // Se é um aluno existente, verificar se a senha foi alterada
        if (aluno.getId() != null) {
            Optional<Aluno> alunoExistente = alunoRepository.findById(aluno.getId());
            if (alunoExistente.isPresent()) {
                Aluno alunoAntigo = alunoExistente.get();
                // Se a senha está vazia ou nula, manter a senha antiga
                if (aluno.getSenha() == null || aluno.getSenha().isEmpty()) {
                    aluno.setSenha(alunoAntigo.getSenha());
                } else if (isPasswordEncrypted(aluno.getSenha())) {
                    // Senha já está criptografada (não foi alterada no formulário ou já está no formato correto)
                    // Não fazer nada, manter como está
                } else {
                    // Senha é texto puro - foi alterada e precisa criptografar
                    aluno.setSenha(passwordEncoder.encode(aluno.getSenha()));
                }
            }
        } else {
            // Novo aluno - sempre criptografar a senha se não estiver já criptografada
            if (aluno.getSenha() != null && !aluno.getSenha().isEmpty() && !isPasswordEncrypted(aluno.getSenha())) {
                aluno.setSenha(passwordEncoder.encode(aluno.getSenha()));
            }
        }
        return alunoRepository.save(aluno); 
    }

    private boolean isPasswordEncrypted(String password) {
        // BCrypt passwords start with $2a$, $2b$, or $2y$
        return password != null && (password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$"));
    }

    public void deleteById(Long id) { 
        alunoRepository.deleteById(id); 
    }
}
