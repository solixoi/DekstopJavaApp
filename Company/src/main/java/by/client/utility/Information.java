package by.client.utility;

import by.client.models.entities.Product;
import by.client.models.entities.ProductionExpenses;
import by.client.models.entities.RealizationExpenses;
import by.client.models.entities.User;
import lombok.Getter;

@Getter
public class Information {
    private static final Information INSTANCE = new Information();

    public static Information getInstance() {
        return INSTANCE;
    }

    private Information() {
        super();
    }

    private static Long countProducts;
    private static Long countEditProducts;

    public Long getCountEditProducts() {
        return countEditProducts;
    }

    public Long getCountProducts() {
        return countProducts;
    }

    public void setProductCounts(Long countProducts, Long countProductsPreviousDay) {
        Information.countProducts = countProducts;
        Information.countEditProducts = countProductsPreviousDay;
    }

    private static Product product;

    public void setProduct(Product product) {
        Information.product = product;
    }

    public Product getProduct() {
        return product;
    }

    public static User user;

    public void setUser(User user) {
        Information.user = user;
    }

    public User getUser() {
        return user;
    }
}
