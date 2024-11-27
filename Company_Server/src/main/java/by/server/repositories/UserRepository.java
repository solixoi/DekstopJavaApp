package by.server.repositories;

import by.server.models.entities.User;
import by.server.utility.JPAUtil;
import jakarta.persistence.EntityManager;

import java.util.List;

public class UserRepository extends GenericRepository<User, Long> {
    public UserRepository() {
        super(User.class);
    }

    public User findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT u FROM User u WHERE u.userId = :id", User.class)
                    .setParameter("id", id)
                    .getSingleResult();
        }catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
        finally {
            em.close();
        }
    }

    public User findByEmail(String email) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
        }catch (Exception e) {
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
            return em.createQuery("SELECT u FROM User u WHERE (u.username = :usernameOrEmail or u.email = :usernameOrEmail) and u.password = :password", User.class)
                    .setParameter("usernameOrEmail", usernameOrEmail)
                    .setParameter("password", password)
                    .getSingleResult();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        } finally {
            em.close();
        }
    }
}
