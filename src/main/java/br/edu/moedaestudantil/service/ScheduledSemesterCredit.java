package br.edu.moedaestudantil.service;

import br.edu.moedaestudantil.model.Professor;
import br.edu.moedaestudantil.repository.ProfessorRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScheduledSemesterCredit {

    private final ProfessorRepository professorRepository;

    public ScheduledSemesterCredit(ProfessorRepository professorRepository) {
        this.professorRepository = professorRepository;
    }

    // Credita 1000 moedas para cada professor no início de cada semestre.
    // Agendado para 1º de janeiro e 1º de julho às 00:00.
    @Scheduled(cron = "0 0 0 1 1 ?")
    public void creditOnJanFirst() {
        creditAllProfessors();
    }

    @Scheduled(cron = "0 0 0 1 7 ?")
    public void creditOnJulFirst() {
        creditAllProfessors();
    }

    // Método público que realiza o crédito (útil para testes/manualmente).
    public void creditAllProfessors() {
        List<Professor> professors = professorRepository.findAll();
        for (Professor p : professors) {
            Integer saldo = p.getSaldoMoedas();
            if (saldo == null) saldo = 0;
            p.setSaldoMoedas(saldo + 1000);
        }
        professorRepository.saveAll(professors);
    }
}
