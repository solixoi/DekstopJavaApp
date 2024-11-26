package by.server.models.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "realization_expenses")
public class RealizationExpenses {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "realization_id")
    private Long realizationId;

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "marketing_cost", precision = 10, scale = 2)
    private BigDecimal marketingCost;

    @Column(name = "distribution_cost", precision = 10, scale = 2)
    private BigDecimal distributionCost;

    @Column(name = "transportation_cost", precision = 10, scale = 2)
    private BigDecimal transportationCost;

    @Column(name = "other_expenses", precision = 10, scale = 2)
    private BigDecimal otherExpenses;
}
