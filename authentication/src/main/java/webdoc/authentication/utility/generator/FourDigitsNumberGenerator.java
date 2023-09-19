package webdoc.authentication.utility.generator;

import java.util.Random;

public class FourDigitsNumberGenerator {
    public static String generateFourDigitsNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 4; i++) {
            int digit = random.nextInt(10);
            sb.append(digit);
        }

        return sb.toString();
    }
}
