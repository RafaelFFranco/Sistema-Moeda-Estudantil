package br.edu.moedaestudantil.config;

import br.edu.moedaestudantil.model.Aluno;
import br.edu.moedaestudantil.model.EmpresaParceira;
import br.edu.moedaestudantil.model.Instituicao;
import br.edu.moedaestudantil.repository.AlunoRepository;
import br.edu.moedaestudantil.repository.EmpresaRepository;
import br.edu.moedaestudantil.repository.InstituicaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private InstituicaoRepository instituicaoRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private AlunoRepository alunoRepository;

    @Override
    public void run(String... args) throws Exception {
        // Verificar se já existem dados
        if (instituicaoRepository.count() == 0) {
            // Criar instituições
            Instituicao universidadeX = new Instituicao("Universidade X");
            Instituicao faculdadeY = new Instituicao("Faculdade Y");
            
            universidadeX = instituicaoRepository.save(universidadeX);
            faculdadeY = instituicaoRepository.save(faculdadeY);

            // Criar empresas parceiras
            EmpresaParceira cantina = new EmpresaParceira();
            cantina.setNome("Cantina Universitária");
            cantina.setEmail("cantina@uni.br");
            cantina.setLogin("cantina");
            cantina.setSenha("senha123");
            empresaRepository.save(cantina);

            EmpresaParceira livraria = new EmpresaParceira();
            livraria.setNome("Livraria Campus");
            livraria.setEmail("livraria@campus.br");
            livraria.setLogin("livraria");
            livraria.setSenha("senha123");
            empresaRepository.save(livraria);

            // Criar um aluno exemplo
            Aluno joao = new Aluno();
            joao.setNome("João da Silva");
            joao.setEmail("joao@exemplo.com");
            joao.setCpf("11122233344");
            joao.setRg("MG1234567");
            joao.setEndereco("Rua A, 123");
            joao.setCurso("Sistemas");
            joao.setLogin("joao");
            joao.setSenha("senha");
            joao.setSaldoMoedas(0);
            joao.setInstituicao(universidadeX);
            alunoRepository.save(joao);

            System.out.println("✅ Dados iniciais criados com sucesso!");
        }
    }
}
