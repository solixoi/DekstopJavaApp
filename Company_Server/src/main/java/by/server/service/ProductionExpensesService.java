package by.server.service;

import by.server.models.entities.ProductionExpenses;
import by.server.models.entities.User;
import by.server.repositories.ProductionExpensesRepository;
import by.server.repositories.UserRepository;

public class ProductionExpensesService {
    private ProductionExpensesRepository prExpensesRepo = new ProductionExpensesRepository();

    public void save(ProductionExpenses productionExpenses) {
        prExpensesRepo.save(productionExpenses);
    }
}
