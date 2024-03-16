package webdoc.community.domain.entity.lesson.enums;

/*
 * 레슨 상태를 나타내는 ENUM
 */
public enum LessonStatus {
    ONGOING("진행중"), ENDED("종료");
    private final String name;
    LessonStatus(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
}
