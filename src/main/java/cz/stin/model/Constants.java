package cz.stin.model;

public class Constants {
    private static final String MESSAGE_UNKNOWN_LOCATION = "Tuhle lokaci bohužel neznám, zkuste prosím jinou.";
    private static final String MESSAGE_PROCESSING_MISTAKE = "Při zpracování dat se vyskytla chyba, omlouváme se ale požadevek momentálně nejsme schopni naplnit.";
    private static final String MESSAGE_INVALID_USERNAME = "Neplatné číslo uživatelské jméno, může obsahovat pouze malá a velká písmena, číslice a podtržítka";
    private static final String MESSAGE_UNKNOWN_USERNAME = "Uživatelské jméno neexistuje, pokud jste tu poprvé zaregistrujte se";
    private static final String MESSAGE_WRONG_PASSWORD = "Neplatné heslo";
    private static final String MESSAGE_SUCCESSFUL_REGISTRATION = "Registrace proběhla úspěšně, můžete se přihlásit";
    private static final String MESSAGE_ALREADY_USED_USERNAME = "Uživatelské jméno již existuje";
    private static final String MESSAGE_INVALID_CARD_NUMBER = "Neplatné číslo karty (platných je 16 číslic)";
    private static final String MESSAGE_INVALID_PASSWORD = "Neplatné heslo, musí být delší než 5 znaků a obsahovat pouze písmena, číslice a znaky: _ & * ;";
    public static String getMessageUnknownLocation() {
        return MESSAGE_UNKNOWN_LOCATION;
    }

    public static String getMessageProcessingMistake() {
        return MESSAGE_PROCESSING_MISTAKE;
    }

    public static String getMessageInvalidUsername() {
        return MESSAGE_INVALID_USERNAME;
    }

    public static String getMessageUnknownUsername() {
        return MESSAGE_UNKNOWN_USERNAME;
    }

    public static String getMessageWrongPassword() {
        return MESSAGE_WRONG_PASSWORD;
    }

    public static String getMessageSuccessfulRegistration() {
        return MESSAGE_SUCCESSFUL_REGISTRATION;
    }

    public static String getMessageAlreadyUsedUsername() {
        return MESSAGE_ALREADY_USED_USERNAME;
    }

    public static String getMessageInvalidCardNumber() {
        return MESSAGE_INVALID_CARD_NUMBER;
    }

    public static String getMessageInvalidPassword() {
        return MESSAGE_INVALID_PASSWORD;
    }
}


