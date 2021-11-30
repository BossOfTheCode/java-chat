package spring.javachat.models.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import spring.javachat.models.entity.User;


@Transactional
public interface UserRepository extends JpaRepository<User, Integer> {
    User findUserById(int id);
    User findUserByLogin(String login);
    void deleteUserById(int id);
}
