package by.server.repositories;

import by.server.utility.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class GenericRepository<T, ID> {
    private final Class<T> entityType;

    public GenericRepository(Class<T> entityType) {
        this.entityType = entityType;
    }

    public void save(T entity) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(entity);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            System.out.println(e.getMessage());
        } finally {
            em.close();
        }
    }

    public T findById(ID id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(entityType, id);
        } finally {
            em.close();
        }
    }

    public List<T> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT e FROM " + entityType.getSimpleName() + " e", entityType).getResultList();
        } finally {
            em.close();
        }
    }

    public void delete(T entity) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.remove(em.contains(entity) ? entity : em.merge(entity));
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}
