package br.edu.moedaestudantil.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

@Entity
@Table(name = "vantagem")
public class Vantagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "Descrição é obrigatória")
    @Column(length = 1000)
    private String descricao;

    @NotNull(message = "Custo em moedas é obrigatório")
    @Min(value = 1, message = "Custo deve ser maior que zero")
    // custo em moedas necessárias para resgatar
    private Integer custoMoedas;

    @ManyToOne
    @JoinColumn(name = "empresa_id")
    private EmpresaParceira empresa;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getCustoMoedas() {
        return custoMoedas;
    }

    public void setCustoMoedas(Integer custoMoedas) {
        this.custoMoedas = custoMoedas;
    }

    public EmpresaParceira getEmpresa() {
        return empresa;
    }

    public void setEmpresa(EmpresaParceira empresa) {
        this.empresa = empresa;
    }
}
