package br.djair.caixa.model.simulacao;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;


import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "TELEMETRIA")
@NoArgsConstructor
public class Telemetria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_TELEMETRIA")
    private Integer id;

    @NotNull
    @Column(name = "NM_ENDPOINT")
    private String nomeEndpoint;

    @CreationTimestamp
    @Column(name = "DT_METRICA")
    private LocalDateTime dataCriacao;

    @NotNull
    @Column(name ="NU_MAX_MILIS")
    private long maximoMilissegundos;

    @NotNull
    @Column(name ="NU_MIN_MILIS")
    private long minimoMilissegundos;

    @NotNull
    @Column(name ="NU_TOTAL_MILIS")
    private long totalMilissegundos;

    @NotNull
    @Column(name ="NU_MEDIA_MILIS")
    private long mediaMilissegundos;

    @NotNull
    @Column(name = "NU_CHAMADAS")
    private Integer totalChamadas;

    @NotNull
    @Column(name = "NU_SUCESSO_CHAMADAS")
    private long sucessoChamadas;

}
