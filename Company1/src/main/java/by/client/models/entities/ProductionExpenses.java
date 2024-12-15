package by.client.models.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductionExpenses {
    private Product product;
    private BigDecimal wagesCost;
    private BigDecimal materialCost;
    private BigDecimal overheadCost;
    private BigDecimal otherExpenses;

    public ProductionExpenses (BigDecimal wagesCost, BigDecimal materialCost, BigDecimal overheadCost, BigDecimal otherExpenses) {
        this.wagesCost = wagesCost;
        this.materialCost = materialCost;
        this.overheadCost = overheadCost;
        this.otherExpenses = otherExpenses;
    }
}
