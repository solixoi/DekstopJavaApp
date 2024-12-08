package by.server.repositories;

import by.server.models.entities.Product;
import by.server.models.entities.User;
import by.server.utility.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.List;

import static by.server.utility.PasswordHashingUtil.verifyPassword;

public class UserRepository extends GenericRepository<User, Long> {
    public UserRepository() {
        super(User.class);
    }

    public User findByEmail(String email) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
        finally {
            em.close();
        }
    }

    public User findByName(String name) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT u FROM User u WHERE u.username = :name", User.class)
                    .setParameter("name", name)
                    .getSingleResult();
        }catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
        finally {
            em.close();
        }
    }

    public User findByUsernameOrEmailWithPassword(String usernameOrEmail, String password) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            User user = em.createQuery("SELECT u FROM User u WHERE u.username = :usernameOrEmail or u.email = :usernameOrEmail", User.class)
                    .setParameter("usernameOrEmail", usernameOrEmail)
                    .getSingleResult();
            if (user != null && verifyPassword(password, user.getPassword())) {
                return user;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        } finally {
            em.close();
        }
        return null;
    }

}
