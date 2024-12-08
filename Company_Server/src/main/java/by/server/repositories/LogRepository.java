package by.server.repositories;

import by.server.models.entities.Log;
import by.server.models.entities.User;

public class LogRepository extends GenericRepository<Log, Long> {
    public LogRepository() {
        super(Log.class);
    }
}
