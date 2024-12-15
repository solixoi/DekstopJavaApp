package by.server.models.entities;

import by.server.models.DTO.ProductDTO;
import lombok.*;

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
    private BigDecimal costPrice = BigDecimal.ZERO;

    @Column(name = "planned_revenue", precision = 10, scale = 2)
    private BigDecimal plannedRevenue;

    @Column(name = "final_price", precision = 10, scale = 2)
    private BigDecimal finalPrice = BigDecimal.ZERO;

    @Column(name = "markup", precision = 10, scale = 2)
    private BigDecimal markup = BigDecimal.ZERO;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private ProductionExpenses productionExpenses;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private RealizationExpenses realizationExpenses;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<PriceHistory> priceHistory;
}
