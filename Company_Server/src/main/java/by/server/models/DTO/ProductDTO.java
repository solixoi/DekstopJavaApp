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
    private BigDecimal plannedPrice;
    private BigDecimal finalPrice;
    private UserDTO createdBy;
}
