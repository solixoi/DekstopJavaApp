package by.server.models.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_name", length = 150)
    private String productName;

    @Column(name = "cost_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal costPrice = BigDecimal.ZERO;

    @Column(name = "planned_revenue", precision = 10, scale = 2)
    private BigDecimal plannedRevenue;

    @Column(name = "final_price", precision = 10, scale = 2)
    private BigDecimal finalPrice;

    @Column(name = "markup", precision = 10, scale = 2)
    private BigDecimal markup;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @OneToOne(mappedBy = "product")
    private ProductionExpenses productionExpenses;

    @OneToOne(mappedBy = "product")
    private RealizationExpenses realizationExpenses;

    @OneToMany(mappedBy = "product")
    private List<PriceHistory> priceHistory;
}
