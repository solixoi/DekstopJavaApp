package by.server.service;

import by.server.models.entities.Ban;
import by.server.repositories.BanRepository;

public class BanService {
    private final BanRepository banRepository = new BanRepository();

    public void save(Ban ban) {
        banRepository.save(ban);
    }

    public void update(Ban ban) {
        banRepository.update(ban);
    }
}
