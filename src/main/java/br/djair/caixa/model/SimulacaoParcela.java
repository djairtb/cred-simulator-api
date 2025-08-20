package br.djair.caixa.model;

import br.djair.caixa.model.enums.TipoParcelaEnum;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;


@Entity
@Data
@Table(name = "SIMULACAO")
public class SimulacaoParcela {
    @Id
    @Column(name = "ID_PARCELA" )
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_SIMULACAO", nullable = false)
    @JsonBackReference // evitar dar caquinha e estourar serializacao de json
    private Simulacao simulacao;

    @NotNull
    @Column(name = "NU_PARCELA")
    private Integer numeroParcela;

    @NotNull
    @Column(name = "IC_TIPO")
    @Enumerated(EnumType.STRING)
    private TipoParcelaEnum tipoParcela;

    @NotNull
    @Column(name = "VR_AMORTIZACAO")
    private BigDecimal valorAmortizacao;

    @NotNull
    @Column(name = "VR_JUROS")
    private BigDecimal valorJuros;

    @NotNull
    @Column(name = "VR_PRESTACAO")
    private BigDecimal valorPrestacao;
}
