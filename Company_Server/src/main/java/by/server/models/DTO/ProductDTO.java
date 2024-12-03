package by.server.models.DTO;

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
    private String productName;
    private BigDecimal costPrice;
    private BigDecimal plannedRevenue;
    private BigDecimal finalPrice;
    private BigDecimal markup;
    private UserDTO createdBy;
}
