package webdoc.community.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import webdoc.community.domain.entity.post.Post;
import webdoc.community.domain.entity.review.Review;

import java.util.List;
/*
 * 리뷰 repository
 */
public interface ReviewRepository extends JpaRepository<Review,Long> {

    // 특정 튜터에 대한 리뷰를 가져온다
    List<Review> findReviewByTutorId(Long tutorId);
    @Query("select r from Review r where r.tutorId=:tutorId")
    Slice<Review> findReviewByTutorId(Long tutorId, PageRequest pageRequest);

    // 특정 튜터에 대한 리뷰를 페이징을 통해 가져온다
    @Query("select r from Review r where r.tutorId=:tutorId and r.id < :reviewId")
    Slice<Review> findReviewByTutorIdAfter(Long tutorId,Long reviewId,PageRequest pageRequest);

    // 특정 튜터의 리뷰 개수를 가져온다
    int countReviewByTutorId(Long tutorId);

}
