package br.edu.moedaestudantil.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transacao")
public class Transacao {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "aluno_origem_id")
    private Aluno alunoOrigem;
    
    @ManyToOne
    @JoinColumn(name = "aluno_destino_id")
    private Aluno alunoDestino;
    
    private Integer quantidadeMoedas;
    
    @Enumerated(EnumType.STRING)
    private TipoTransacao tipo;
    
    private String descricao;
    
    private LocalDateTime dataHora;
    
    @ManyToOne
    @JoinColumn(name = "instituicao_id")
    private Instituicao instituicao;
    
    public Transacao() {
        this.dataHora = LocalDateTime.now();
    }
    
    public enum TipoTransacao {
        TRANSFERENCIA("Transferência entre alunos"),
        ADICAO("Adição manual"),
        REMOCAO("Remoção manual"),
        RESGATE("Resgate em empresa");
        
        private final String descricao;
        
        TipoTransacao(String descricao) {
            this.descricao = descricao;
        }
        
        public String getDescricao() {
            return descricao;
        }
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Aluno getAlunoOrigem() {
        return alunoOrigem;
    }
    
    public void setAlunoOrigem(Aluno alunoOrigem) {
        this.alunoOrigem = alunoOrigem;
    }
    
    public Aluno getAlunoDestino() {
        return alunoDestino;
    }
    
    public void setAlunoDestino(Aluno alunoDestino) {
        this.alunoDestino = alunoDestino;
    }
    
    public Integer getQuantidadeMoedas() {
        return quantidadeMoedas;
    }
    
    public void setQuantidadeMoedas(Integer quantidadeMoedas) {
        this.quantidadeMoedas = quantidadeMoedas;
    }
    
    public TipoTransacao getTipo() {
        return tipo;
    }
    
    public void setTipo(TipoTransacao tipo) {
        this.tipo = tipo;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    public LocalDateTime getDataHora() {
        return dataHora;
    }
    
    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }
    
    public Instituicao getInstituicao() {
        return instituicao;
    }
    
    public void setInstituicao(Instituicao instituicao) {
        this.instituicao = instituicao;
    }
}

