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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_TELEMETRIA")
    private Integer id;

    @NotNull
    @Column(name = "NM_ENDPOINT")
    private String nomeEndpoint;

    @CreationTimestamp
    @Column(name = "DT_ATUALIZACAO")
    private LocalDateTime dataCriacao;

    @NotNull
    @Column(name ="NU_MAX_MILIS")
    private long maximoMilissegundos;

    @NotNull
    @Column(name ="NU_TOTAL_MILIS")
    private long totalMilissegundos;

    @NotNull
    @Column(name ="NU_MEDIA_MILIS")
    private long mediaMilissegundos;

    @NotNull
    @Column(name = "NU_CHAMADAS")
    private long totalChamadas;

    @NotNull
    @Column(name = "NU_SUCESSO_CHAMADAS")
    private long sucessoChamadas;

}
