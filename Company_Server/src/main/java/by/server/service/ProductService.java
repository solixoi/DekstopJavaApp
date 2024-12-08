package by.server.service;

import by.server.models.entities.Product;
import by.server.models.entities.RealizationExpenses;
import by.server.repositories.ProductRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class ProductService {
    private final ProductRepository productRepo = new ProductRepository();

    public void save(Product product) {
        productRepo.save(product);
    }

    public void update(Product product) {
        productRepo.update(product);
    }

    public void delete(Product product) {
        productRepo.delete(product);
    }

    public Product calculateTotalPrice(long productId) {
        Product product = null;
        product = productRepo.calculate_total_price(productId);
        if (product == null) {
            throw new RuntimeException("Something went wrong with calculate total price!");
        }
        return product;
    }

    public Product findById(long productId) {
        Product product = productRepo.findById(productId);
        if (product == null) {
            throw new RuntimeException("Don't find product with id!");
        }
        return product;
    }

    public List<Product> findAll() {
        return productRepo.findAll();
    }
}
