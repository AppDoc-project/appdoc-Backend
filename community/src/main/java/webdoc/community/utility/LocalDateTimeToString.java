package webdoc.community.utility;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeToString {

    // LocalDateTime을 대한민국 표준시의 형식으로 변환하는 메서드
    public static String convertToLocalDateTimeString(LocalDateTime localDateTime) {
        if (localDateTime == null ) return null;


        // 대한민국 표준시로 설정
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy:MM:dd:HH:mm")
                .withZone(java.time.ZoneId.of("Asia/Seoul"));
        return localDateTime.format(formatter);
    }


}