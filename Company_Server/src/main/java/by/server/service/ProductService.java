package by.server.service;

import by.server.models.entities.Product;
import by.server.models.entities.RealizationExpenses;
import by.server.repositories.ProductRepository;

public class ProductService {
    private ProductRepository productRepo = new ProductRepository();

    public void save(Product product) {
        productRepo.save(product);
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
}
