package spring.javachat.models.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import spring.javachat.models.entity.BannedUser;
import spring.javachat.models.entity.User;

@Transactional
public interface BannedUserRepository extends JpaRepository<BannedUser, Integer> {
    BannedUser findBanById(int id);
    @Query(value = "select id, reason, user_id from banned_users where user_id = ?1", nativeQuery = true)
    BannedUser findBanByUserId(int user_id);
    BannedUser findBanByUser(User user);
}
