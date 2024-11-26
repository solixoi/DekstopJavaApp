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
}
