package br.djair.caixa.model.produto;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

//Entidade especifica do banco sql server no pacote produto

@Entity
@Table(name = "PRODUTO")
public class ProdutoModel {
    @Id
    @Column(name = "CO_PRODUTO")
    private Integer id;

    @Column(name = "NO_PRODUTO")
    private String nome;

    @Column(name = "PC_TAXA_JUROS")
    private BigDecimal taxaJuros;

    @Column(name = "NU_MINIMO_MESES")
    private Integer minimoMeses;

    @Column(name = "NU_MAXIMO_MESES")
    private Integer maximoMeses;

    @Column(name = "VR_MINIMO")
    private BigDecimal valorMinimo;

    @Column(name = "VR_MAXIMO")
    private BigDecimal valorMaximo;
}
