package by.server.service;

import by.server.models.entities.Log;
import by.server.models.entities.User;
import by.server.repositories.LogRepository;

public class LogService {
    private final LogRepository logRepository = new LogRepository();

    public void save(User user, String message) {
        Log log = new Log(user, message);
        if (user.getUserId() != null) {
            logRepository.update(log);
        } else {
            logRepository.save(log);
        }
    }

}
