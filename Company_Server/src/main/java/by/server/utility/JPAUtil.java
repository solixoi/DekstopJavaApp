package by.server.utility;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class JPAUtil {

    private static EntityManagerFactory entityManagerFactory;

    public static EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null) {
            try {
                Thread.currentThread().setContextClassLoader(new ClassLoader() {
                    @Override
                    public Enumeration<URL> getResources(String name) throws IOException {
                        if (name.equals("META-INF/persistence.xml")) {
                            URL resource = getClass().getClassLoader().getResource("persistence.xml");
                            if (resource != null) {
                                return Collections.enumeration(Collections.singletonList(resource));
                            }
                        }
                        return super.getResources(name);
                    }
                });
                entityManagerFactory = Persistence.createEntityManagerFactory("persistence-unit-name");
            } catch (Exception e) {
                System.out.println("Исключение при создании EntityManagerFactory: " + e.getMessage());
            }
        }
        return entityManagerFactory;
    }

    public static EntityManager getEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }

    // Закрытие EntityManagerFactory
    public static void shutdown() {
        if (entityManagerFactory != null) {
            entityManagerFactory.close();
        }
    }
}
