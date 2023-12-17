package webdoc.community.domain.entity.post.enums;

public enum PostSearchType {

    TITLE("제목"), CONTENT("내용"), TITLEANDCONTENT("제목내용");
    private final String name;

    PostSearchType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
