package webdoc.authentication.utility.generator;

import java.util.Random;
/*
* 4개 랜덤 숫자 코드 생성 유틸리티
 */
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
