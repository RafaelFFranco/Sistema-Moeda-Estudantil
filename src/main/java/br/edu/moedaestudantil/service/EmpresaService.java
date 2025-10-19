package br.edu.moedaestudantil.service;

import br.edu.moedaestudantil.model.EmpresaParceira;
import br.edu.moedaestudantil.repository.EmpresaRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class EmpresaService {
    private final EmpresaRepository empresaRepository;

    public EmpresaService(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository;
    }

    public List<EmpresaParceira> findAll() { 
        return empresaRepository.findAll(); 
    }

    public Optional<EmpresaParceira> findById(Long id) { 
        return empresaRepository.findById(id); 
    }

    public EmpresaParceira save(EmpresaParceira empresa) { 
        return empresaRepository.save(empresa); 
    }

    public void deleteById(Long id) { 
        empresaRepository.deleteById(id); 
    }
}
