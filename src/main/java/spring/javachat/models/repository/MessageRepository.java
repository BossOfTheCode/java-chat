package spring.javachat.models.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import spring.javachat.models.entity.Message;

import java.util.List;

@Transactional
public interface MessageRepository extends JpaRepository<Message, Integer> {
    @Query(value = "select * from messages INNER JOIN users ON users.id = messages.sender where login = ?1", nativeQuery = true)
    List<Message> findMessagesByUserName(String userName);
}
