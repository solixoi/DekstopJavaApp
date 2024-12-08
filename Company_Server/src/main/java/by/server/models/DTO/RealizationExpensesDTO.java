package by.server.models.DTO;

import by.server.models.entities.RealizationExpenses;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RealizationExpensesDTO {
    private ProductDTO product;
    private BigDecimal marketingCost;
    private BigDecimal distributionCost;
    private BigDecimal transportationCost;
    private BigDecimal otherExpenses;

    public RealizationExpensesDTO(RealizationExpenses realizationExpenses) {
        this.product = new ProductDTO(realizationExpenses.getProduct());
        this.marketingCost = realizationExpenses.getMarketingCost();
        this.distributionCost = realizationExpenses.getDistributionCost();
        this.transportationCost = realizationExpenses.getTransportationCost();
        this.otherExpenses = realizationExpenses.getOtherExpenses();
    }
}
