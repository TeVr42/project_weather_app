package cz.stin.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class InputValidators {
    public static boolean isValidCardNumber(String cardNumber) {
        String strippedNumber = cardNumber.replaceAll("\\s", "");
        if (strippedNumber.length() != 16) {
            return false;
        }
        for (int i = 0; i < strippedNumber.length(); i++) {
            if (!Character.isDigit(strippedNumber.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isValidUsername(String username) {
        String regex = "^[a-zA-Z0-9_]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(username);

        return matcher.matches() && username.length() > 0;
    }

    public static boolean isValidPassword(String password) {
        String regex = "^[a-zA-Z0-9_&*;]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);

        return matcher.matches() && password.length() > 5;
    }
}
