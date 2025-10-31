package br.edu.moedaestudantil.service;

import br.edu.moedaestudantil.model.Aluno;
import br.edu.moedaestudantil.model.Professor;
import br.edu.moedaestudantil.model.EmpresaParceira;
import br.edu.moedaestudantil.repository.AlunoRepository;
import br.edu.moedaestudantil.repository.ProfessorRepository;
import br.edu.moedaestudantil.repository.EmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Try professor
        Professor prof = professorRepository.findByLogin(username).orElse(null);
        if (prof != null) {
            return buildUser(prof.getLogin(), prof.getSenha(), "ROLE_PROFESSOR");
        }

        Aluno aluno = alunoRepository.findByLogin(username).orElse(null);
        if (aluno != null) {
            return buildUser(aluno.getLogin(), aluno.getSenha(), "ROLE_ALUNO");
        }

        EmpresaParceira emp = empresaRepository.findByLogin(username).orElse(null);
        if (emp != null) {
            return buildUser(emp.getLogin(), emp.getSenha(), "ROLE_EMPRESA");
        }

        throw new UsernameNotFoundException("Usuário não encontrado: " + username);
    }

    private UserDetails buildUser(String username, String password, String role) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role));
        return new User(username, password, authorities);
    }
}
