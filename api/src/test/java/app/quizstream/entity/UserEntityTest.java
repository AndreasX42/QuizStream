package app.quizstream.entity;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserEntityTest {

    @Autowired
    private TestEntityManager testEntityManager;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("myUserName", "myemail@mail.com", "myPassword");
    }

    @Test
    void testUserEntity_whenUsernameBlank_shouldNotSaveEntity() {

        user.setUsername("");

        assertThatThrownBy(() -> testEntityManager.persistAndFlush(user)).isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining(
                        "username cannot be blank");
    }

    @Test
    void testUserEntity_whenUsernameTooShort_shouldNotSaveEntity() {

        user.setUsername("xy");

        assertThatThrownBy(() -> testEntityManager.persistAndFlush(user)).isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining(
                        "username must be at least 3 characters");
    }

    @Test
    void testUserEntity_whenUsernameValid_shouldSaveEntity() {

        user.setUsername("xyz");

        User persistedUser = testEntityManager.persistAndFlush(user);

        User userInDb = testEntityManager.find(User.class, persistedUser.getId());

        assertUserValidAndExpected(user, userInDb);

    }

    @Test
    void testUserEntity_whenEmailBlank_shouldNotSaveEntity() {

        user.setEmail("");

        assertThatThrownBy(() -> testEntityManager.persistAndFlush(user)).isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining(
                        "email cannot be blank");
    }

    @Test
    void testUserEntity_whenEmailInvalid_shouldNotSaveEntity() {

        user.setEmail("invalid-email");

        assertThatThrownBy(() -> testEntityManager.persistAndFlush(user)).isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining(
                        "email must be a valid email address");
    }

    @Test
    void testUserEntity_whenEmailValid_shouldSaveEntity() {

        user.setEmail("user@test.com");

        User persistedUser = testEntityManager.persistAndFlush(user);

        User userInDb = testEntityManager.find(User.class, persistedUser.getId());

        assertUserValidAndExpected(user, userInDb);

    }


    private void assertUserValidAndExpected(User expectedUser, User actualUser) {
        assertThat(actualUser).isNotNull();
        assertThat(actualUser.getDateCreated()).isInstanceOf(LocalDateTime.class);
        assertThat(actualUser.getQuizzes()).isNull();

        assertThat(actualUser.getUsername()).isEqualTo(expectedUser.getUsername());
        assertThat(actualUser.getEmail()).isEqualTo(expectedUser.getEmail());
        assertThat(actualUser.getPassword()).isEqualTo(expectedUser.getPassword());
        assertThat(actualUser.getRole()).isEqualTo(expectedUser.getRole());
        assertThat(actualUser.getIsActive()).isEqualTo(expectedUser.getIsActive());
    }

}
