package by.server.service;

import by.server.models.entities.Product;
import by.server.models.entities.ProductionExpenses;
import by.server.models.entities.User;
import by.server.repositories.ProductionExpensesRepository;
import by.server.repositories.UserRepository;

public class ProductionExpensesService {
    private final ProductionExpensesRepository prExpensesRepo = new ProductionExpensesRepository();

    public void save(ProductionExpenses productionExpenses) {
        prExpensesRepo.save(productionExpenses);
    }

    public void update(ProductionExpenses productionExpenses) {
        prExpensesRepo.update(productionExpenses);
    }

    public Long findProductId(Long productId){
        ProductionExpenses productionExpenses = prExpensesRepo.findByProductId(productId);
        return productionExpenses.getProductionId();
    }
}
