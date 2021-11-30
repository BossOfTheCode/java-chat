package spring.javachat.models.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spring.javachat.models.entity.BannedUser;
import spring.javachat.models.entity.User;
import spring.javachat.models.repository.BannedUserRepository;

import java.util.List;

@Service
public class BannedUserService {
    private final BannedUserRepository bannedUserRepository;

    @Autowired
    public BannedUserService(BannedUserRepository bannedUserRepository) {
        this.bannedUserRepository = bannedUserRepository;
    }

    public List<BannedUser> findAll() { return bannedUserRepository.findAll(); }

    public BannedUser findBanById(int id) { return bannedUserRepository.findBanById(id); }

    public BannedUser findBanByUserId(int user_id) { return bannedUserRepository.findBanByUserId(user_id); }

    public BannedUser findBanByUser(User user) { return bannedUserRepository.findBanByUser(user); }

    public void ban(BannedUser bannedUser) {
        bannedUserRepository.save(bannedUser);
    }

    public void unBan(BannedUser bannedUser) {
        if (findBanById(bannedUser.getId()) != null)
            bannedUserRepository.delete(bannedUser);
    }
}
