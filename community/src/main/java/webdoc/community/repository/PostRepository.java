package webdoc.community.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import webdoc.community.domain.entity.post.Post;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post,Long> {
    @Query(value = "select distinct p from Post p" + " join fetch p.community " +
            " where p.community.id = :communityId"
            ,countQuery = "select count(p) from Post p where p.community = :communityId")
    Slice<Post> getPostByCommunityAndLimit(Long communityId, PageRequest pageRequest);

    @Query("select distinct p from Post p" + " join fetch p.community " +
            " where p.community.id = :communityId and p.id < :postId")
    Slice<Post> getPostByCommunityAndLimitAndId(long communityId,long postId,PageRequest pageRequest);

    @Query("select p from Post p" + " join fetch p.community " +
            " left join fetch p.pictures where p.id = :id")
    Optional<Post> getCertainPost(long id);

    // 전체 게시판 첫 검색
    @Query("select distinct p from Post p" + " join fetch p.community " +
        " where p.title like %:keyword%")
    Slice<Post> getPostByTitleAndLimit(String keyword,PageRequest pageRequest);

    @Query("select distinct p from Post p" + " join fetch p.community " +
            " where p.text like %:keyword% ")
    Slice<Post> getPostByContentAndLimit(String keyword,PageRequest pageRequest);
    @Query("select distinct p from Post p" +  " join fetch p.community " +
            " where p.title like %:keyword% and p.text like %:keword%")
    Slice<Post> getPostByContentAndTitleAndLimit(String keyword,PageRequest pageRequest);

    // 전체 게시판 스크롤 검색
    @Query("select distinct p from Post p" +  " join fetch p.community " +
            " where p.title like %:keyword% and p.id < :postId")
    Slice<Post> getPostByTitleAndLimitAndId(String keyword,Long postId,PageRequest pageRequest);

    @Query("select distinct p from Post p" +  " join fetch p.community " +
            " where p.text like %:keyword% and p.id < :postId")
    Slice<Post> getPostByContentAndLimitAndId(String keyword,Long postId,PageRequest pageRequest);
    @Query("select distinct p from Post p" +  " join fetch p.community " +
            " where (p.title like %:keyword% or p.text like %:keyword%) and p.id < :postId")
    Slice<Post> getPostByContentAndTitleAndLimitAndId(String keyword,Long postId,PageRequest pageRequest);


    // 게시판 별 첫 검색
    @Query("select distinct p from Post p" + " join fetch p.community " +
            " where p.title like %:keyword% and p.community.id = :communityId")
    Slice<Post> getPostByCommunityAndTitleAndLimit(String keyword,Long communityId,PageRequest pageRequest);

    @Query("select distinct p from Post p" + " join fetch p.community " +
            " where p.text like %:keyword% and p.community.id = :communityId ")
    Slice<Post> getPostByCommunityAndContentAndLimit(String keyword,Long communityId,PageRequest pageRequest);
    @Query("select distinct p from Post p" +  " join fetch p.community " +
            " where p.title like %:keyword% and p.text like %:keword% and p.community.id = :communityId")
    Slice<Post> getPostByCommunityAndContentAndTitleAndLimit(String keyword,Long communityId,PageRequest pageRequest);

    // 게시판 별 이후 검색
    @Query("select distinct p from Post p" + " join fetch p.community " +
            " where p.title like %:keyword% and p.community.id = :communityId and p.id < :postId")
    Slice<Post> getPostByCommunityAndTitleAndLimitAndId(String keyword,Long communityId,Long postId,PageRequest pageRequest);

    @Query("select distinct p from Post p" + " join fetch p.community " +
            " where p.text like %:keyword% and p.community.id = :communityId and p.id < :postId")
    Slice<Post> getPostByCommunityAndContentAndLimitAndId(String keyword,Long communityId,Long postId,PageRequest pageRequest);
    @Query("select distinct p from Post p" +  " join fetch p.community " +
            " where p.title like %:keyword% and p.text like %:keword% and p.community.id = :communityId and p.id < :postId")
    Slice<Post> getPostByCommunityAndContentAndTitleAndLimitAndId(String keyword,Long communityId,Long postId,PageRequest pageRequest);

    // 자기가 쓴 게시글 수 확인
    int countPostsByUserId(Long userId);

    // 자기가 쓴 게시글
    Slice<Post> getPostByUserId(Long userId, PageRequest pageRequest);

    // 내가 쓴 댓글이 담긴 게시글
    @Query("select distinct p from Post p "+ " join p.threads t " +
        " where t.userId = :userId")
    Slice<Post> getPostsWithMyThread(Long userId,PageRequest pageRequest);

    // 내가 북마크 한 게시글
    @Query("select distinct p from Post p" + " join p.bookmarks b " +
        " where b.userId = :userId")
    Slice<Post> getBookmarkedPosts(Long userId, PageRequest pageRequest);


}
