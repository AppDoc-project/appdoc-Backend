package webdoc.community.domain.entity.post.enums;

public enum PostSortType {

    LATEST("최신순"), LIKE("좋아요순"), THREAD("댓글순"), VIEW("조회순");
    private final String name;

    PostSortType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
