package br.edu.moedaestudantil.service;

import br.edu.moedaestudantil.model.EmpresaParceira;
import br.edu.moedaestudantil.repository.EmpresaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional
    public EmpresaParceira save(EmpresaParceira empresa) {
        // Novo cadastro - garantir que a senha seja criptografada
        if (empresa.getSenha() != null && !empresa.getSenha().isEmpty() && !isPasswordEncrypted(empresa.getSenha())) {
            empresa.setSenha(passwordEncoder.encode(empresa.getSenha()));
        }
        return empresaRepository.save(empresa); 
    }

        /* Antes da modificação

    @Transactional
    public EmpresaParceira update(EmpresaParceira dados) {
        EmpresaParceira empresa = empresaRepository.findById(dados.getId())
            .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada com ID: " + dados.getId()));

        empresa.setNome(dados.getNome());
        empresa.setEmail(dados.getEmail());
        empresa.setLogin(dados.getLogin());
        
        // Gerenciar senha: se não foi informada, mantém a atual; se foi informada, criptografa
        if (dados.getSenha() == null || dados.getSenha().isEmpty()) {
            // Mantém a senha atual
        } else if (!isPasswordEncrypted(dados.getSenha())) {
            empresa.setSenha(passwordEncoder.encode(dados.getSenha()));
        } else {
            empresa.setSenha(dados.getSenha());
        }

        return empresa;
    }
        */
       
   
   /* Depois 
   Justificativa: Nomes genéricos (dados, empresa) reduzem clareza. Renomear para descritivos (data, company) em inglês melhora legibilidade e evita confusões em métodos complexos. */
    @Transactional
public EmpresaParceira update(EmpresaParceira data) {
    EmpresaParceira company = empresaRepository.findById(data.getId())
        .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada com ID: " + data.getId()));

    company.setNome(data.getNome());
    company.setEmail(data.getEmail());
    company.setLogin(data.getLogin());
    
    // Gerenciar senha: se não foi informada, mantém a atual; se foi informada, criptografa
    if (data.getSenha() == null || data.getSenha().isEmpty()) {
        // Mantém a senha atual
    } else if (!isPasswordEncrypted(data.getSenha())) {
        company.setSenha(passwordEncoder.encode(data.getSenha()));
    } else {
        company.setSenha(data.getSenha());
    }

    return company;
}

    private boolean isPasswordEncrypted(String password) {
        return password != null && (password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$"));
    }

    public void deleteById(Long id) { 
        empresaRepository.deleteById(id); 
    }

    public long count() {
        return empresaRepository.count();
    }
}
