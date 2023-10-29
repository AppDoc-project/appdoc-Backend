package webdoc.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import webdoc.community.domain.entity.post.Picture;
import webdoc.community.domain.entity.post.Post;

public interface PictureRepository extends JpaRepository<Picture,Long> {
    int countPictureByPost(Post post);
}
