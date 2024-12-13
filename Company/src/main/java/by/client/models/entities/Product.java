package by.client.models.entities;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Data
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

    public Product(Long id, User user, String productName, BigDecimal plannedRevenue) {
        this.productId = id;
        this.createdBy = user;
        this.productName = productName;
        this.plannedRevenue = plannedRevenue;
    }

    public Product(User user, String productName, BigDecimal plannedRevenue) {
        this.createdBy = user;
        this.productName = productName;
        this.plannedRevenue = plannedRevenue;
    }
}
