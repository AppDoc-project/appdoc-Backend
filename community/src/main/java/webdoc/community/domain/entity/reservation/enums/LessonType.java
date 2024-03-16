package webdoc.community.domain.entity.reservation.enums;
/*
* 예약 및 레슨 타입을 나타내는 ENUM
 */
public enum LessonType {
    FACETOFACE("대면"), REMOTE("비대면");

    private final String name;

    LessonType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
