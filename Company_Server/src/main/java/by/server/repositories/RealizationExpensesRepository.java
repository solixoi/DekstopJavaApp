package by.server.repositories;

import by.server.models.entities.ProductionExpenses;
import by.server.models.entities.RealizationExpenses;
import by.server.utility.JPAUtil;
import jakarta.persistence.EntityManager;

public class RealizationExpensesRepository extends GenericRepository<RealizationExpenses, Long> {
    public RealizationExpensesRepository() {
        super(RealizationExpenses.class);
    }

    public RealizationExpenses findByProductId(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("select p from RealizationExpenses p where p.product.productId = :id", RealizationExpenses.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        } finally {
            em.close();
        }
    }
}
