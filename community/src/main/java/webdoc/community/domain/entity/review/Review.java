package webdoc.community.domain.entity.review;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import webdoc.community.domain.BaseEntity;
/*
* 리뷰 도메인 객체
 */
@Entity
@EqualsAndHashCode(of = "id")
@Getter
@Setter
public class Review extends BaseEntity {
    protected Review(){}

    public static Review createReview(Long tuteeId, Long tutorId, String review, Integer score){
        return Review
                .builder()
                .tutorId(tutorId)
                .tuteeId(tuteeId)
                .review(review)
                .score(score)
                .build();

    }
    @Builder
    private Review(Long tuteeId,Long tutorId,String review, Integer score){
        this.tuteeId = tuteeId;
        this.tutorId = tutorId;
        this.review = review;
        this.score = score;
    }

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private Long tuteeId;

    @Column(nullable = false)
    private Long tutorId;

    @Column(nullable = false)
    private Integer score;

    @Column(nullable = false,length = 500)
    private String review;


}