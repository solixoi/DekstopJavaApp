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
public class RealizationExpenses {
    private Product product;
    private BigDecimal marketingCost;
    private BigDecimal distributionCost;
    private BigDecimal transportationCost;
    private BigDecimal otherExpenses;

    public RealizationExpenses(BigDecimal marketingCost, BigDecimal distributionCost, BigDecimal transportationCost, BigDecimal otherExpenses) {
        this.marketingCost = marketingCost;
        this.distributionCost = distributionCost;
        this.transportationCost = transportationCost;
        this.otherExpenses = otherExpenses;
    }
}
