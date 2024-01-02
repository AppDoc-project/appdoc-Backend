package webdoc.authentication.utility.generator;

import java.util.UUID;

public class UUIDGenerator {

    private UUIDGenerator() {
    }

    public static String generateRandomUUID() {
        return UUID.randomUUID().toString();
    }

}
