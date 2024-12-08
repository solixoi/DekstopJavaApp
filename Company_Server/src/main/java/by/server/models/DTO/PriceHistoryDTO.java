package by.server.models.DTO;

import by.server.models.entities.PriceHistory;
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

    public PriceHistoryDTO(PriceHistory priceHistory) {
        this.product = new ProductDTO(priceHistory.getProduct());
        this.oldPrice = priceHistory.getOldPrice();
        this.newPrice = priceHistory.getNewPrice();
        this.changeDate = priceHistory.getChangeDate();
    }
}
