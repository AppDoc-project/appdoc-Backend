package webdoc.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import webdoc.community.domain.entity.post.Picture;
import webdoc.community.domain.entity.post.Post;
/*
 * 사진 repository
 */
public interface PictureRepository extends JpaRepository<Picture,Long> {

    // 특정 게시글에 담긴 사진 수 반환
    int countPictureByPostId(Long postId);
}
