package webdoc.community.domain.entity.user;
/*
* 튜터를 정렬하고자 하는 기준 ENUM
 */
public enum TutorSortType {
    LESSON("레슨"),SCORE("평점"),PICK("찜");


    private final String name;

    TutorSortType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

