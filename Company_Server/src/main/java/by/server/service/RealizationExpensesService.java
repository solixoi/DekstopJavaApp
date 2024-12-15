package by.server.service;

import by.server.models.entities.Product;
import by.server.models.entities.ProductionExpenses;
import by.server.models.entities.RealizationExpenses;
import by.server.models.entities.User;
import by.server.repositories.RealizationExpensesRepository;
import by.server.repositories.UserRepository;

public class RealizationExpensesService {
    private final RealizationExpensesRepository realExpensesRepo = new RealizationExpensesRepository();

    public void save(RealizationExpenses realizationExpenses) {
        realExpensesRepo.save(realizationExpenses);
    }

    public void update(RealizationExpenses realizationExpenses) {
        realExpensesRepo.update(realizationExpenses);
    }

    public Long findProductId(Long productId){
        RealizationExpenses realizationExpenses = realExpensesRepo.findByProductId(productId);
        return realizationExpenses.getRealizationId();
    }

    public RealizationExpenses findById(Long id){
        return realExpensesRepo.findById(id);
    }

    public void delete(RealizationExpenses realizationExpenses) {
        realExpensesRepo.delete(realizationExpenses);
    }
}
