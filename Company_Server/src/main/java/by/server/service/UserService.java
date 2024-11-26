package by.server.service;

import by.server.models.entities.User;
import by.server.repositories.UserRepository;
import lombok.SneakyThrows;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class UserService {

    private UserRepository userRepo = new UserRepository();

    public void save(User user) {
        if (userRepo.findByEmail(user.getEmail()) != null){
            throw new RuntimeException("Email already exists");
        }
        if (userRepo.findByName(user.getUsername()) != null){
            throw new RuntimeException("Username already exists");
        };
        userRepo.save(user);
    }
}
