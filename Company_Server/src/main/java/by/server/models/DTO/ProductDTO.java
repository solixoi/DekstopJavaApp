package by.server.models.DTO;

import by.server.models.entities.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long productId;
    private String productName;
    private BigDecimal costPrice;
    private BigDecimal plannedRevenue;
    private BigDecimal finalPrice;
    private BigDecimal markup;
    private UserDTO createdBy;

    public ProductDTO(Product product) {
        this.productId = product.getProductId();
        this.productName = product.getProductName();
        this.costPrice = product.getCostPrice();
        this.plannedRevenue = product.getPlannedRevenue();
        this.finalPrice = product.getFinalPrice();
        this.markup = product.getMarkup();
        this.createdBy = new UserDTO(product.getCreatedBy());
    }
}
