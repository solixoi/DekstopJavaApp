package by.server.repositories;

import by.server.models.entities.ProductionExpenses;

public class ProductionExpensesRepository extends GenericRepository<ProductionExpenses, Long> {
    public ProductionExpensesRepository() {
        super(ProductionExpenses.class);
    }
}
