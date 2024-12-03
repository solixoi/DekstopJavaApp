package by.server.models.DTO;

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
public class PriceHistoryDTO {
    private ProductDTO product;
    private BigDecimal oldPrice;
    private BigDecimal newPrice;
    private Timestamp changeDate;
}
