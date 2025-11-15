package br.edu.moedaestudantil.service;

import br.edu.moedaestudantil.model.Vantagem;
import br.edu.moedaestudantil.repository.VantagemRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VantagemService {

    private final VantagemRepository vantagemRepository;

    public VantagemService(VantagemRepository vantagemRepository) {
        this.vantagemRepository = vantagemRepository;
    }

    public List<Vantagem> findAll() {
        return vantagemRepository.findAll();
    }

    public Optional<Vantagem> findById(Long id) {
        return vantagemRepository.findById(id);
    }

    // retorna vantagem com empresaParceira carregada (quando necessário em controllers)
    public Optional<Vantagem> findByIdWithEmpresaParceira(Long id) {
        try {
            return vantagemRepository.findByIdWithEmpresaParceira(id);
        } catch (Exception e) {
            return vantagemRepository.findById(id);
        }
    }

    public Vantagem save(Vantagem v) {
        return vantagemRepository.save(v);
    }

    public void deleteById(Long id) {
        vantagemRepository.deleteById(id);
    }

    // busca por id da empresa cobrindo ambos os possíveis relacionamentos
    public List<Vantagem> findByEmpresaId(Long empresaId) {
        if (empresaId == null) return List.of();
        List<Vantagem> result = new ArrayList<>();
        try { result.addAll(vantagemRepository.findByEmpresaParceira_Id(empresaId)); } catch (Exception ignored) {}
        return result.stream().distinct().collect(Collectors.toList());
    }
}
