package spring.javachat.models.entity;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Objects;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty(message = "Имя пользователя не может быть пустым")
    @Size(min = 2, max = 20, message = "Имя пользователя должно содержать не менее 2 и не более 20 символов")
    @Pattern(regexp = "[a-zA-Z0-9_.]*", message = "Имя пользователя может содержать только цифры и буквы латинского алфавита")
    @Column(unique = true, nullable = false, length = 20)
    private String login;

    @NotEmpty(message = "Пароль не может быть пустым")
    @Size(min = 8, max = 60, message = "Пароль должен содержать не менее 8 и не более 60 символов")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[A-Z])(?=\\S+$).{8,}$", message = "Пароль может содержать только цифры и буквы латинского алфавита, а также обязательно должен включать одну цифру и одну заглавную букву")
    @Column(nullable = false, length = 60)
    private String password;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id) && login.equals(user.login) && password.equals(user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, login, password);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
