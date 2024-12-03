package by.server.service;

import by.server.models.entities.RealizationExpenses;
import by.server.models.entities.User;
import by.server.repositories.RealizationExpensesRepository;
import by.server.repositories.UserRepository;

public class RealizationExpensesService {
    private RealizationExpensesRepository realExpensesRepo = new RealizationExpensesRepository();

    public void save(RealizationExpenses realizationExpenses) {
        realExpensesRepo.save(realizationExpenses);
    }
}
