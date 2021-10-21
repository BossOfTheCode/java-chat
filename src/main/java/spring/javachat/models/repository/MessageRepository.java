package spring.javachat.models.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import spring.javachat.models.entity.Message;

@Transactional
public interface MessageRepository extends JpaRepository<Message, Integer> {
}
