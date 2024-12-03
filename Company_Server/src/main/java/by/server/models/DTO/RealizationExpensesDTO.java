package by.server.models.DTO;

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
}
