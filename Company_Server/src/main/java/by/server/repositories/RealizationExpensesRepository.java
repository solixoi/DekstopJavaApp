package by.server.repositories;

import by.server.models.entities.RealizationExpenses;

public class RealizationExpensesRepository extends GenericRepository<RealizationExpenses, Long> {
    public RealizationExpensesRepository() {
        super(RealizationExpenses.class);
    }
}
