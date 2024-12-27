package by.server.utility;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class JPAUtil {

    private static EntityManagerFactory entityManagerFactory;

    public static EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null) {
            try {
                LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
                Logger logger = loggerContext.getLogger("org.hibernate");
                logger.setLevel(Level.OFF);

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
                initializeDatabase();
            } catch (Exception e) {
                System.out.println("Исключение при создании EntityManagerFactory: " + e.getMessage());
            }
        }
        return entityManagerFactory;
    }

    public static EntityManager getEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }

    public static void shutdown() {
        if (entityManagerFactory != null) {
            entityManagerFactory.close();
        }
    }

    private static void initializeDatabase() {
        EntityManager entityManager = getEntityManager();

        try {
            InputStream inputStream = JPAUtil.class.getClassLoader().getResourceAsStream("PostgreSQL_scripts/triggers.sql");
            if (inputStream == null) {
                throw new RuntimeException("SQL script not found");
            }
            String sqlScript = new BufferedReader(new InputStreamReader(inputStream))
                    .lines()
                    .collect(Collectors.joining("\n"));

            entityManager.getTransaction().begin();
            entityManager.createNativeQuery(sqlScript).executeUpdate();
            entityManager.getTransaction().commit();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
        } finally {
            entityManager.close();
        }
    }

}
