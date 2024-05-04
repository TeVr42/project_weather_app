package cz.stin.service;

import cz.stin.model.AppUser;
import cz.stin.repository.IUserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserServiceTest {

    @Mock
    private IUserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void addUser_ValidUser_UserSaved() {
        AppUser user = new AppUser();
        user.setUsername("kocicka");
        user.setPassword("password123");
        userService.addUser(user);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void findUserByUsername_ExistingUser_ReturnsUser() {
        AppUser expectedUser = new AppUser();
        expectedUser.setUsername("username");
        expectedUser.setPassword("password");
        when(userRepository.findByUsername("username")).thenReturn(expectedUser);
        AppUser actualUser = userService.findUserByUsername("username");
        assertEquals(expectedUser, actualUser);
    }

    @Test
    void findUserByUsername_NonExistingUser_ReturnsNull() {
        when(userRepository.findByUsername("username")).thenReturn(null);
        AppUser actualUser = userService.findUserByUsername("username");
        assertNull(actualUser);
    }
}
