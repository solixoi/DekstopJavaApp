package by.server.repositories;

import by.server.models.entities.ProductionExpenses;
import by.server.utility.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class ProductionExpensesRepository extends GenericRepository<ProductionExpenses, Long> {
    public ProductionExpensesRepository() {
        super(ProductionExpenses.class);
    }


    public ProductionExpenses findByProductId(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("select p from ProductionExpenses p where p.product.productId = :id", ProductionExpenses.class)
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
