package by.server.service;

import by.server.models.entities.User;
import by.server.repositories.UserRepository;

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

    public User findByUsernameOrEmailOrPassword(String usernameOrEmail, String password) {
        User isUser = userRepo.findByUsernameOrEmailWithPassword(usernameOrEmail, password);
        System.out.println(isUser);
        if(isUser == null){
            throw new RuntimeException("Invalid username or password");
        }
        return isUser;
    }

    public User findById(long id) {
        User user = userRepo.findById(id);
        if(user == null){
            throw new RuntimeException("User not found");
        }
        return user;
    }
}
