package webdoc.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import webdoc.community.domain.entity.like.Bookmark;
import webdoc.community.domain.entity.post.Post;
import webdoc.community.domain.entity.user.User;

import java.util.List;

public interface BookmarkRepository extends JpaRepository<Bookmark,Long> {
    int countBookmarkByPost(Post post);
    List<Bookmark> findBookmarkByPostAndUser(Post post, User user);
}
