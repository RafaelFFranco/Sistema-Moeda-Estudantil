package br.edu.moedaestudantil.config;

import br.edu.moedaestudantil.model.Aluno;
import br.edu.moedaestudantil.model.Professor;
import br.edu.moedaestudantil.model.EmpresaParceira;
import br.edu.moedaestudantil.model.Instituicao;
import br.edu.moedaestudantil.repository.AlunoRepository;
import br.edu.moedaestudantil.repository.ProfessorRepository;
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

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

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
            cantina.setSenha(passwordEncoder.encode("senha123"));
            empresaRepository.save(cantina);

            EmpresaParceira livraria = new EmpresaParceira();
            livraria.setNome("Livraria Campus");
            livraria.setEmail("livraria@campus.br");
            livraria.setLogin("livraria");
            livraria.setSenha(passwordEncoder.encode("senha123"));
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
            joao.setSenha(passwordEncoder.encode("senha"));
            joao.setSaldoMoedas(0);
            joao.setInstituicao(universidadeX);
            alunoRepository.save(joao);

            // Criar um professor exemplo
            Professor maria = new Professor();
            maria.setNome("Maria Oliveira");
            maria.setEmail("maria@exemplo.com");
            maria.setCpf("12345678900");
            maria.setDisciplina("Matemática");
            maria.setLogin("maria");
            maria.setSenha(passwordEncoder.encode("senha"));
            maria.setInstituicao(universidadeX);
            maria.setSaldoMoedas(1000);
            professorRepository.save(maria);
            // Garantir que quaisquer professores já existentes (ex.: em um banco persistente)
            // tenham pelo menos 1000 moedas. Isso cobre o caso da "Maria" já criada.
            java.util.List<br.edu.moedaestudantil.model.Professor> allProfessors = professorRepository.findAll();
            boolean changed = false;
            for (br.edu.moedaestudantil.model.Professor p : allProfessors) {
                if (p.getSaldoMoedas() == null || p.getSaldoMoedas() < 1000) {
                    p.setSaldoMoedas(1000);
                    changed = true;
                }
            }
            if (changed) {
                professorRepository.saveAll(allProfessors);
            }

            System.out.println("✅ Dados iniciais criados com sucesso!");
        }
    }
}
