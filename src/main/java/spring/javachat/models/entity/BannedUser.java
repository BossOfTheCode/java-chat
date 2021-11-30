package spring.javachat.models.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "banned_users")
public class BannedUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column
    private String reason;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BannedUser that = (BannedUser) o;
        return Objects.equals(id, that.id) && Objects.equals(user, that.user) && Objects.equals(reason, that.reason);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, reason);
    }

    @Override
    public String toString() {
        return "BannedUser{" +
                "id=" + id +
                ", user=" + user +
                ", reason='" + reason + '\'' +
                '}';
    }
}
