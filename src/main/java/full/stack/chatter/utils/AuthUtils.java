package full.stack.chatter.utils;


import java.security.SecureRandom;
import at.favre.lib.crypto.bcrypt.BCrypt;


public class AuthUtils {

    private static final String LOWER_CASE_LETTERS = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPER_CASE_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMBERS = "1234567890";
    private static final String SPECIAL_CHARS = "!@#$%^&*()-+<>";
    private static final String ALL_CHARS = LOWER_CASE_LETTERS + UPPER_CASE_LETTERS + NUMBERS + SPECIAL_CHARS;

    private static final SecureRandom random = new SecureRandom();


    public static String encryptPassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    public static boolean matchesPassword(String rawPassword, String encryptedPassword) {
        BCrypt.Result result = BCrypt.verifyer().verify(rawPassword.toCharArray(), encryptedPassword);
        return result.verified;
    }

    public static String generateRandomPassword(int length) {
        StringBuilder pass = new StringBuilder(length);
        pass.append(getRandomChar(LOWER_CASE_LETTERS));
        pass.append(getRandomChar(SPECIAL_CHARS));
        pass.append(getRandomChar(UPPER_CASE_LETTERS));
        pass.append(getRandomChar(NUMBERS));

        for (int x = 4; x < length; x++) {
            pass.append(getRandomChar(ALL_CHARS));
        }
        return pass.toString();
    }

    private static char getRandomChar(String charSet) {
        return charSet.charAt(random.nextInt(charSet.length()));
    }

}