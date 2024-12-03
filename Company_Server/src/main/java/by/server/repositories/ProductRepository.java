package by.server.repositories;

import by.server.models.entities.Product;
import by.server.models.entities.User;
import by.server.utility.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ProductRepository extends GenericRepository<Product, Long> {
    public ProductRepository() {
        super(Product.class);
    }

    public Product calculate_total_price(long product_id) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        Product result = null;
        try {
            transaction.begin();
            Product product = em.find(Product.class, product_id);
            if (product == null) {
                throw new RuntimeException("Product not found with id: " + product_id);
            }
            BigDecimal markup = ((product.getPlannedRevenue().divide(product.getCostPrice(),4, RoundingMode.HALF_UP)).subtract(new BigDecimal("-1"))).multiply(new BigDecimal("100"));
            markup = markup.setScale(2, RoundingMode.HALF_UP);
            product.setMarkup(markup);
            BigDecimal final_price = product.getCostPrice().multiply((new BigDecimal("1").add(product.getMarkup().divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP))));
            final_price = final_price.setScale(2, RoundingMode.HALF_UP);
            product.setFinalPrice(final_price);
            System.out.println("Markup: " + markup);
            System.out.println("Final Price: " + final_price);

            em.merge(product);
            result = product;
        }catch (Exception e) {
            transaction.rollback();
            System.out.println(e.getMessage());
        } finally {
            em.close();
        }
        return result;
    }
}
