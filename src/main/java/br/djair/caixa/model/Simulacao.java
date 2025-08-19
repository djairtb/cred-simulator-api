package br.djair.caixa.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "SIMULACAO")
@Getter
@Setter
public class Simulacao {
    @Id
    @Column(name = "ID_SIMULACAO" )
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @CreationTimestamp
    @Column(name = "DT_SIMULACAO", updatable = false)
    private LocalDateTime dataCriacao;

    @NotNull
    @Column(name = "CO_PRODUTO")
    private Integer idProduto;

    @NotNull
    @Column(name = "NU_PRAZO")
    private Integer prazo;

    @NotNull
    @Column(name = "VR_DESEJADO")
    private BigDecimal valorDesejado;

    @NotNull
    @Column(name = "VR_TOTAL_PARCELAS")
    private BigDecimal valorTotalParcelas;

    @OneToMany(mappedBy = "simulacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SimulacaoParcela> parcelas = new ArrayList<>();
}
