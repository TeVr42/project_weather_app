package cz.stin.validator;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InputValidatorsTest {

    @Test
    void isValidCardNumber_ValidNumber_ReturnsTrue() {
        assertTrue(InputValidators.isValidCardNumber("1234567890123456"));
        assertTrue(InputValidators.isValidCardNumber("1234 5678 9012 3456"));
    }

    @Test
    void isValidCardNumber_InvalidNumber_ReturnsFalse() {
        assertFalse(InputValidators.isValidCardNumber("123456789012345"));
        assertFalse(InputValidators.isValidCardNumber("12345678901234567"));
        assertFalse(InputValidators.isValidCardNumber("123456789012ABCD"));
    }

    @Test
    void isValidUsername_ValidUsername_ReturnsTrue() {
        assertTrue(InputValidators.isValidUsername("username123"));
    }

    @Test
    void isValidUsername_InvalidUsername_ReturnsFalse() {
        assertFalse(InputValidators.isValidUsername("username$%"));
        assertFalse(InputValidators.isValidUsername("username user"));
        assertFalse(InputValidators.isValidUsername(""));
    }

    @Test
    void isValidPassword_ValidPassword_ReturnsTrue() {
        assertTrue(InputValidators.isValidPassword("Password123;&"));
        assertTrue(InputValidators.isValidPassword("password"));
    }

    @Test
    void isValidPassword_InvalidPassword_ReturnsFalse() {
        assertFalse(InputValidators.isValidPassword("pass"));
        assertFalse(InputValidators.isValidPassword("Password 123"));
        assertFalse(InputValidators.isValidPassword("Password*&+"));
    }
}
