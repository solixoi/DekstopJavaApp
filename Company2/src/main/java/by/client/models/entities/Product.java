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
    private String productName;
    private BigDecimal costPrice;
    private BigDecimal plannedPrice;
    private BigDecimal finalPrice;
    private User createdBy;
}
