package br.edu.moedaestudantil.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
public class Vantagem implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String descricao;

    @ManyToOne(fetch = FetchType.LAZY)
    private EmpresaParceira empresaParceira;

    @Column(name = "custo_moedas", nullable = false)
    private Integer custoMoedas = 0;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Compatibilidade com templates que usam "titulo"
    public String getTitulo() {
        return this.nome;
    }

    public void setTitulo(String titulo) {
        this.nome = titulo;
    }

    // Compatibilidade com templates que usam "valor"
    public Integer getValor() {
        return this.custoMoedas;
    }

    public void setValor(Integer valor) {
        this.custoMoedas = valor == null ? 0 : valor;
    }

    // Mantém acesso padrão também
    public String getNome() { return this.nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return this.descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public Integer getCustoMoedas() { return custoMoedas; }
    public void setCustoMoedas(Integer custoMoedas) { this.custoMoedas = custoMoedas == null ? 0 : custoMoedas; }

    public EmpresaParceira getEmpresaParceira() { return empresaParceira; }
    public void setEmpresaParceira(EmpresaParceira empresaParceira) { this.empresaParceira = empresaParceira; }
}
