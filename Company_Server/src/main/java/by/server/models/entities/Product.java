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

    @Column(name = "cost_price", precision = 10, scale = 2)
    private BigDecimal costPrice;

    @Column(name = "planned_price", precision = 10, scale = 2)
    private BigDecimal plannedPrice;

    @Column(name = "final_price", precision = 10, scale = 2)
    private BigDecimal finalPrice;

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
