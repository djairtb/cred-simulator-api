    package br.djair.caixa.model.produto;


    import io.quarkus.hibernate.orm.PersistenceUnit;
    import jakarta.persistence.*;
    import lombok.Data;
    import org.hibernate.annotations.CacheConcurrencyStrategy;
    import org.hibernate.annotations.Cache;
    import java.math.BigDecimal;

    //Entidade especifica do banco sql server no pacote produto

    @Entity
    @Data
    @Cacheable
    @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
    @Table(name = "PRODUTO")
    public class Produto {
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
