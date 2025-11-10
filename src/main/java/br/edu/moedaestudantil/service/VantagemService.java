package br.edu.moedaestudantil.service;

import br.edu.moedaestudantil.model.Vantagem;
import br.edu.moedaestudantil.model.EmpresaParceira;
import br.edu.moedaestudantil.repository.VantagemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VantagemService {
    private final VantagemRepository vantagemRepository;

    public VantagemService(VantagemRepository vantagemRepository) {
        this.vantagemRepository = vantagemRepository;
    }

    public List<Vantagem> findAll() {
        return vantagemRepository.findAllByOrderByNomeAsc();
    }

    public List<Vantagem> findByEmpresa(EmpresaParceira empresa) {
        return vantagemRepository.findByEmpresa(empresa);
    }

    public Optional<Vantagem> findById(Long id) {
        return vantagemRepository.findById(id);
    }

    public Vantagem save(Vantagem v) { 
        return vantagemRepository.save(v); 
    }

    public void deleteById(Long id) { 
        vantagemRepository.deleteById(id); 
    }
}
