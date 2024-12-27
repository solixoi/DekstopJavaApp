import by.server.models.entities.User;
import by.server.repositories.UserRepository;
import by.server.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.sql.Timestamp;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User validUser;
    private User existingEmailUser;
    private User existingUsernameUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validUser = new User("tehstuser", new Timestamp(System.currentTimeMillis()), "password123", "test@example.com", "Test", "User", null);
        existingEmailUser = new User("aexistinguser", new Timestamp(System.currentTimeMillis()), "password123", "existing@example.com", "Existing", "User", null);
        existingUsernameUser = new User("exihstinguser", new Timestamp(System.currentTimeMillis()), "password123", "other@example.com", "Existing", "User", null);
    }

    @Test
    void testSaveUserEmailAlreadyExists() {
        when(userRepository.findByEmail(validUser.getEmail())).thenReturn(existingEmailUser);
        when(userRepository.findByName(validUser.getUsername())).thenReturn(null);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.save(validUser));
        assertEquals("Email already exists", exception.getMessage());
    }

    @Test
    void testSaveUserUsernameAlreadyExists() {
        when(userRepository.findByEmail(validUser.getEmail())).thenReturn(null);
        when(userRepository.findByName(validUser.getUsername())).thenReturn(existingUsernameUser);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.save(validUser));
        assertEquals("Email already exists", exception.getMessage());
    }

    @Test
    void testSaveUserBothEmailAndUsernameAlreadyExists() {
        when(userRepository.findByEmail(validUser.getEmail())).thenReturn(existingEmailUser);
        when(userRepository.findByName(validUser.getUsername())).thenReturn(existingUsernameUser);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.save(validUser));
        assertEquals("Email already exists", exception.getMessage());
    }
}
