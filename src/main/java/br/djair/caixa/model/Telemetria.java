package br.djair.caixa.model;


import br.djair.caixa.model.enums.StatusTelemetriaEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;


import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "TELEMETRIA")
public class Telemetria {

    @Id
    @Column(name = "ID_TELEMETRIA")
    private Integer id;

    @CreationTimestamp
    @Column(name = "DT_SIMULACAO", updatable = false)
    private LocalDateTime dataCriacao;

    @NotNull
    @Column(name = "NM_ENDPOINT")
    private String nomeEndpoint;

    @NotNull
    @Column(name ="NU_MILISSEGUNDOS")
    private Integer milissegundos;

    @NotNull
    @Column(name = "TS_INICIO")
    private LocalDateTime inicio;

    @NotNull
    @Column(name = "TS_FIM")
    private LocalDateTime fim;

    @NotNull
    @Column(name = "IC_STATUS")
    @Enumerated(EnumType.STRING)
    private StatusTelemetriaEnum status;



}
