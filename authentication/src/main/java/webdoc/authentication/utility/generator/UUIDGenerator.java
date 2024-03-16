package webdoc.authentication.utility.generator;

import java.util.UUID;
/*
* UUID 생성
 */
public class UUIDGenerator {

    private UUIDGenerator() {
    }

    public static String generateRandomUUID() {
        return UUID.randomUUID().toString();
    }

}
