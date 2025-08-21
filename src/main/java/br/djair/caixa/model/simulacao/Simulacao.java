package br.djair.caixa.model.simulacao;

import br.djair.caixa.model.produto.Produto;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "SIMULACAO")
@Data
@NoArgsConstructor
public class Simulacao {

    public Simulacao(Integer idProduto, Integer prazo, BigDecimal valorDesejado,List <SimulacaoParcela> parcelas) {
        this.idProduto = idProduto;
        this.prazo = prazo;
        this.valorDesejado = valorDesejado;
        this.parcelas = parcelas;
        for (SimulacaoParcela parcela : this.parcelas) {
            parcela.setSimulacao(this);
        }
    }

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


    @OneToMany(mappedBy = "simulacao", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // sinaliza que é o pai para n dar erro de serializacao de json
    private List<SimulacaoParcela> parcelas = new ArrayList<>();

}
