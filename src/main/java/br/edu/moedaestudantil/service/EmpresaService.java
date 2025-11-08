package br.edu.moedaestudantil.service;

import br.edu.moedaestudantil.model.EmpresaParceira;
import br.edu.moedaestudantil.repository.EmpresaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class EmpresaService {
    private final EmpresaRepository empresaRepository;
    private final PasswordEncoder passwordEncoder;

    public EmpresaService(EmpresaRepository empresaRepository, PasswordEncoder passwordEncoder) {
        this.empresaRepository = empresaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<EmpresaParceira> findAll() { 
        return empresaRepository.findAll(); 
    }

    public List<EmpresaParceira> findByQuery(String q) {
        if (q == null || q.isBlank()) return findAll();
        return empresaRepository.findByNomeContainingIgnoreCaseOrEmailContainingIgnoreCase(q, q);
    }

    public Optional<EmpresaParceira> findById(Long id) { 
        return empresaRepository.findById(id); 
    }

    public EmpresaParceira save(EmpresaParceira empresa) {
        // Gerenciar senha
        if (empresa.getId() != null) {
            Optional<EmpresaParceira> empresaExistente = empresaRepository.findById(empresa.getId());
            if (empresaExistente.isPresent()) {
                EmpresaParceira empresaAntiga = empresaExistente.get();
                if (empresa.getSenha() == null || empresa.getSenha().isEmpty()) {
                    empresa.setSenha(empresaAntiga.getSenha());
                } else if (!isPasswordEncrypted(empresa.getSenha())) {
                    empresa.setSenha(passwordEncoder.encode(empresa.getSenha()));
                }
            }
        } else {
            if (empresa.getSenha() != null && !empresa.getSenha().isEmpty() && !isPasswordEncrypted(empresa.getSenha())) {
                empresa.setSenha(passwordEncoder.encode(empresa.getSenha()));
            }
        }
        return empresaRepository.save(empresa); 
    }

    private boolean isPasswordEncrypted(String password) {
        return password != null && (password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$"));
    }

    public void deleteById(Long id) { 
        empresaRepository.deleteById(id); 
    }
}
