package by.client.models.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private Long productId;
    private String productName;
    private BigDecimal costPrice;
    private BigDecimal plannedRevenue;
    private BigDecimal finalPrice;
    private BigDecimal markup;
    private User createdBy;

    public Product(String productName, BigDecimal costPrice, BigDecimal plannedRevenue, BigDecimal finalPrice, BigDecimal markup,  User createdBy) {
        this.productName = productName;
        this.costPrice = costPrice;
        this.plannedRevenue = plannedRevenue;
        this.finalPrice = finalPrice;
        this.markup = markup;
        this.createdBy = createdBy;
    }

    public Product(long id, String productName, BigDecimal plannedRevenue) {
        this.productId = id;
        this.productName = productName;
        this.plannedRevenue = plannedRevenue;
    }
}
