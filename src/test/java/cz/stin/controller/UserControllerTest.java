package cz.stin.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import cz.stin.model.AppUser;
import cz.stin.model.Constants;
import cz.stin.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @Mock
    private HttpSession session;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoginWhenAuthorized() {
        when(session.getAttribute("authorized")).thenReturn(true);

        String result = userController.login(session, model);

        assertEquals("redirect:/", result);
    }

    @Test
    void testLoginWithMessage() {
        when(session.getAttribute("authorized")).thenReturn(false);
        when(model.getAttribute("message")).thenReturn("Test message");

        String result = userController.login(session, model);

        assertEquals("login", result);
        verify(model).addAttribute("message", "Test message");
    }

    @Test
    void testLoginWithoutMessage() {
        when(session.getAttribute("authorized")).thenReturn(false);
        when(model.getAttribute("message")).thenReturn(null);

        String result = userController.login(session, model);

        assertEquals("login", result);
        verify(model, never()).addAttribute(eq("message"), anyString());
    }

    @Test
    void testLoginWithValidCredentials() {
        String username = "testuser";
        String password = "password";

        AppUser testUser = new AppUser();
        testUser.setUsername(username);
        testUser.setPassword(password);
        testUser.setCardNumber("1234 5678 9123 4567");

        when(userService.findUserByUsername(username)).thenReturn(testUser);

        String result = userController.login(username, password, session, model);

        verify(session).setAttribute("authorized", true);
        verify(session).setAttribute("username", username);
        assert result.equals("redirect:/");
    }

    @Test
    void testLoginWithInvalidUsername() {
        MockHttpSession session = new MockHttpSession();
        String username = "test user";
        String password = "password";

        String result = userController.login(username, password, session, model);

        verify(model).addAttribute("authorized", false);
        assert result.equals("login");
    }

    @Test
    void testLoginWithInvalidPassword() {
        MockHttpSession session = new MockHttpSession();
        String username = "testuser";
        String password = "password";

        AppUser testUser = new AppUser();
        testUser.setUsername(username);
        testUser.setPassword("wrongpassword");
        testUser.setCardNumber( "1234 5678 9123 4567");

        when(userService.findUserByUsername(username)).thenReturn(testUser);

        String result = userController.login(username, password, session, model);

        verify(model).addAttribute("authorized", false);
        assert result.equals("login");
    }

    @Test
    void testRegisterWithValidInput() {
        MockHttpSession session = new MockHttpSession();
        String username = "newuser";
        String password = "newpassword";
        String cardNumber = "1234 5678 9000 1234";

        String result = userController.register(username, password, cardNumber, session, model, redirectAttributes);

        verify(userService).addUser(any(AppUser.class));
        verify(redirectAttributes).addFlashAttribute("message", Constants.getMessageSuccessfulRegistration());
        assert result.equals("redirect:/prihlaseni");
    }

    @Test
    void testRegisterWithExistingUsername() {
        MockHttpSession session = new MockHttpSession();
        String username = "existinguser";
        String password = "newpassword";
        String cardNumber = "1234 5678 9000 1234";

        AppUser testUser = new AppUser();
        testUser.setUsername(username);
        testUser.setPassword("password");
        testUser.setCardNumber( "1234 5678 9123 4567");

        when(userService.findUserByUsername(username)).thenReturn(testUser);

        String result = userController.register(username, password, cardNumber, session, model, redirectAttributes);

        verify(model).addAttribute("message", Constants.getMessageAlreadyUsedUsername());
        assert result.equals("register");
    }

    @Test
    void testRegisterWithInvalidUsername() {
        MockHttpSession session = new MockHttpSession();
        String username = "new user";
        String password = "newpassword";
        String cardNumber = "1234 5678 9000 1234";

        String result = userController.register(username, password, cardNumber, session, model, redirectAttributes);

        verify(model).addAttribute("message", Constants.getMessageInvalidUsername());
        assert result.equals("register");
    }

    @Test
    void testRegisterWhenAuthorized() {
        when(session.getAttribute("authorized")).thenReturn(true);

        String result = userController.register(session, model);

        assertEquals("redirect:/", result);
    }

    @Test
    void testRegisterWhenNotAuthorized() {
        when(session.getAttribute("authorized")).thenReturn(false);

        String result = userController.register(session, model);

        assertEquals("register", result);
        verify(model).addAttribute("authorized", false);
    }

    @Test
    void testLogout() {
        MockHttpSession mockSession = new MockHttpSession();
        mockSession.setAttribute("authorized", true);
        mockSession.setAttribute("username", "testUser");

        String result = userController.logout(mockSession, model);

        assertEquals("redirect:/", result);
        assertEquals(false, mockSession.getAttribute("authorized"));
        assertEquals("", mockSession.getAttribute("username"));
    }
}
