package by.server.service;

import by.server.models.entities.PriceHistory;
import by.server.repositories.PriceHistoryRepository;

public class PriceHistoryService {
    private final PriceHistoryRepository priceHistoryRepo = new PriceHistoryRepository();

    public void save(PriceHistory priceHistory) {
        priceHistoryRepo.save(priceHistory);
    }
}
