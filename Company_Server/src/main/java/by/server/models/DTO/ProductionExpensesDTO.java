package by.server.models.DTO;

import by.server.models.entities.Product;
import by.server.models.entities.ProductionExpenses;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductionExpensesDTO {
    private ProductDTO product;
    private BigDecimal wagesCost;
    private BigDecimal materialCost;
    private BigDecimal overheadCost;
    private BigDecimal otherExpenses;

    public ProductionExpensesDTO(ProductionExpenses productionExpenses) {
        this.product = new ProductDTO(productionExpenses.getProduct());
        this.wagesCost = productionExpenses.getWagesCost();
        this.materialCost = productionExpenses.getMaterialCost();
        this.overheadCost = productionExpenses.getOverheadCost();
        this.otherExpenses = productionExpenses.getOtherExpenses();
    }
}
