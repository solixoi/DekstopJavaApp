package by.server.models.entities;

import by.server.models.DTO.ProductionExpensesDTO;
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
@Table(name = "production_expenses")
public class ProductionExpenses {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "production_id")
    private Long productionId;

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "wages_cost", precision = 10, scale = 2)
    private BigDecimal wagesCost;

    @Column(name = "material_cost", precision = 10, scale = 2)
    private BigDecimal materialCost;

    @Column(name = "overhead_cost", precision = 10, scale = 2)
    private BigDecimal overheadCost;

    @Column(name = "other_expenses", precision = 10, scale = 2)
    private BigDecimal otherExpenses;

    public ProductionExpenses(ProductionExpensesDTO productionExpensesDTO){
        this.product = new Product(productionExpensesDTO.getProduct());
        this.wagesCost = productionExpensesDTO.getWagesCost();
        this.materialCost = productionExpensesDTO.getMaterialCost();
        this.overheadCost = productionExpensesDTO.getOverheadCost();
        this.otherExpenses = productionExpensesDTO.getOtherExpenses();
    }
}
