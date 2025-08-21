package br.djair.caixa.model.simulacao;

import br.djair.caixa.model.enums.TipoParcelaEnum;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Entity
@Data
@NoArgsConstructor
@Table(name = "SIMULACAO_PARCELA")
public class SimulacaoParcela {
    public SimulacaoParcela(Integer numeroParcela, TipoParcelaEnum tipoParcela,BigDecimal valorAmortizacao,BigDecimal valorJuros,BigDecimal valorPrestacao) {
        this.numeroParcela = numeroParcela;
        this.tipoParcela = tipoParcela;
        this.valorAmortizacao = valorAmortizacao;
        this.valorJuros = valorJuros;
        this.valorPrestacao = valorPrestacao;
    }
    @Id
    @Column(name = "ID_PARCELA" )
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_SIMULACAO", nullable = false)
    @JsonBackReference // evitar dar caquinha e estourar serializacao de json
    private Simulacao simulacao;


    @Column(name = "NU_PARCELA")
    private Integer numeroParcela;


    @Column(name = "IC_TIPO")
    @Enumerated(EnumType.STRING)
    private TipoParcelaEnum tipoParcela;


    @Column(name = "VR_AMORTIZACAO")
    private BigDecimal valorAmortizacao;


    @Column(name = "VR_JUROS")
    private BigDecimal valorJuros;


    @Column(name = "VR_PRESTACAO")
    private BigDecimal valorPrestacao;
}
