package by.client.models.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PriceHistory {
    private Product product;
    private BigDecimal oldPrice;
    private BigDecimal newPrice;
    private Timestamp changeDate;
}
