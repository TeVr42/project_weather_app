package cz.stin.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import cz.stin.model.AppUser;
import cz.stin.model.Constants;
import cz.stin.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ExtendWith(MockitoExtension.class)
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

    @Test
    void testLoginWhenAuthorized() {
        when(session.getAttribute(Constants.ATTRIBUTE_AUTHORIZED)).thenReturn(true);

        String result = userController.login(session, model);

        assertEquals("redirect:/", result);
    }

    @Test
    void testLoginWithMessage() {
        when(session.getAttribute(Constants.ATTRIBUTE_AUTHORIZED)).thenReturn(false);
        when(model.getAttribute(Constants.ATTRIBUTE_MESSAGE)).thenReturn("Test message");

        String result = userController.login(session, model);

        assertEquals("login", result);
        verify(model).addAttribute(Constants.ATTRIBUTE_MESSAGE, "Test message");
    }

    @Test
    void testLoginWithoutMessage() {
        when(session.getAttribute(Constants.ATTRIBUTE_AUTHORIZED)).thenReturn(false);
        when(model.getAttribute(Constants.ATTRIBUTE_MESSAGE)).thenReturn(null);

        String result = userController.login(session, model);

        assertEquals("login", result);
        verify(model, never()).addAttribute(eq(Constants.ATTRIBUTE_MESSAGE), anyString());
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

        verify(session).setAttribute(Constants.ATTRIBUTE_AUTHORIZED, true);
        verify(session).setAttribute(Constants.ATTRIBUTE_USERNAME, username);
        assert result.equals("redirect:/");
    }

    @Test
    void testLoginWithInvalidUsername() {
        MockHttpSession session = new MockHttpSession();
        String username = "test user";
        String password = "password";

        String result = userController.login(username, password, session, model);

        verify(model).addAttribute(Constants.ATTRIBUTE_AUTHORIZED, false);
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

        verify(model).addAttribute(Constants.ATTRIBUTE_AUTHORIZED, false);
        assert result.equals("login");
    }

    @Test
    void testLogin_UnknownUsername() {
        when(userService.findUserByUsername(anyString())).thenReturn(null);
        HttpSession session = mock(HttpSession.class);
        Model model = mock(Model.class);

        String result = userController.login("unknownUser", "password", session, model);

        verify(model).addAttribute(Constants.ATTRIBUTE_MESSAGE, Constants.getMessageUnknownUsername());
        assertEquals("login", result);
    }

    @Test
    void testRegisterWithValidInput() {
        MockHttpSession session = new MockHttpSession();
        String username = "newuser";
        String password = "newpassword";
        String cardNumber = "1234 5678 9000 1234";

        String result = userController.register(username, password, cardNumber, session, model, redirectAttributes);

        verify(userService).addUser(any(AppUser.class));
        verify(redirectAttributes).addFlashAttribute(Constants.ATTRIBUTE_MESSAGE, Constants.getMessageSuccessfulRegistration());
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

        verify(model).addAttribute(Constants.ATTRIBUTE_MESSAGE, Constants.getMessageAlreadyUsedUsername());
        assert result.equals("register");
    }

    @Test
    void testRegisterWithInvalidUsername() {
        MockHttpSession session = new MockHttpSession();
        String username = "new user";
        String password = "newpassword";
        String cardNumber = "1234 5678 9000 1234";

        String result = userController.register(username, password, cardNumber, session, model, redirectAttributes);

        verify(model).addAttribute(Constants.ATTRIBUTE_MESSAGE, Constants.getMessageInvalidUsername());
        assert result.equals("register");
    }

    @Test
    void testRegisterWhenAuthorized() {
        when(session.getAttribute(Constants.ATTRIBUTE_AUTHORIZED)).thenReturn(true);

        String result = userController.register(session, model);

        assertEquals("redirect:/", result);
    }

    @Test
    void testRegisterWhenNotAuthorized() {
        when(session.getAttribute(Constants.ATTRIBUTE_AUTHORIZED)).thenReturn(false);

        String result = userController.register(session, model);

        assertEquals("register", result);
        verify(model).addAttribute(Constants.ATTRIBUTE_AUTHORIZED, false);
    }

    @Test
    void testRegister_InvalidCardNumber() {
        when(userService.findUserByUsername(anyString())).thenReturn(null);
        HttpSession session = mock(HttpSession.class);
        Model model = mock(Model.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        String result = userController.register("username", "password", "0000", session, model, redirectAttributes);

        verify(model).addAttribute(Constants.ATTRIBUTE_MESSAGE, Constants.getMessageInvalidCardNumber());
        assertEquals("register", result);
    }

    @Test
    void testRegister_InvalidPassword() {
        when(userService.findUserByUsername(anyString())).thenReturn(null);
        HttpSession session = mock(HttpSession.class);
        Model model = mock(Model.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        String result = userController.register("username", " 1", "0000000011112222", session, model, redirectAttributes);

        verify(model).addAttribute(Constants.ATTRIBUTE_MESSAGE, Constants.getMessageInvalidPassword());
        assertEquals("register", result);
    }

    @Test
    void testLogout() {
        MockHttpSession mockSession = new MockHttpSession();
        mockSession.setAttribute(Constants.ATTRIBUTE_AUTHORIZED, true);
        mockSession.setAttribute(Constants.ATTRIBUTE_USERNAME, "testUser");

        String result = userController.logout(mockSession, model);

        assertEquals("redirect:/", result);
        assertEquals(false, mockSession.getAttribute(Constants.ATTRIBUTE_AUTHORIZED));
        assertEquals("", mockSession.getAttribute(Constants.ATTRIBUTE_USERNAME));
    }
}
