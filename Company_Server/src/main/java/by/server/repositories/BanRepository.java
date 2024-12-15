package by.server.repositories;

import by.server.models.entities.Ban;
import by.server.models.entities.Log;

public class BanRepository extends GenericRepository<Ban, Long> {
    public BanRepository() {
        super(Ban.class);
    }
}
