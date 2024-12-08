package by.server.repositories;

import by.server.models.entities.PriceHistory;

public class PriceHistoryRepository extends GenericRepository<PriceHistory, Long> {
    public PriceHistoryRepository() {
        super(PriceHistory.class);
    }
}
