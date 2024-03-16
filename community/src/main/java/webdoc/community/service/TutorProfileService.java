package webdoc.community.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webdoc.community.domain.entity.pick.Pick;
import webdoc.community.domain.entity.review.Review;
import webdoc.community.domain.entity.review.response.ReviewResponse;
import webdoc.community.domain.entity.tutor.response.TutorDetailResponse;
import webdoc.community.domain.entity.tutor.response.TutorResponse;
import webdoc.community.domain.entity.user.Specialities;
import webdoc.community.domain.entity.user.response.UserResponse;
import webdoc.community.repository.PickRepository;
import webdoc.community.repository.ReviewRepository;
import webdoc.community.utility.LocalDateTimeToString;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

/*
 * 튜터 검색 및 프로필 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TutorProfileService {
    private final PickRepository pickRepository;
    private final UserService userService;
    private final StatisticsService statisticsService;

    private final ReviewRepository reviewRepository;

    @Value("${authentication.server}")
    private String authServer;

    // 튜터 찜을 토글한다
    @Transactional
    public void toggleTutor(Long tuteeId, Long tutorId){

        Optional<UserResponse> userResponse = userService.fetchUserResponseFromAuthServer(authServer + "/server/user/id/" + tutorId.toString(),10_000,10_000);
        UserResponse user = userResponse.get();

        if (!user.getIsTutor()){
            throw new IllegalStateException("비정상적인 접근입니다");
        }

        Pick pick = pickRepository.findByTutorIdAndTuteeId(tutorId,tuteeId);

        if(pick == null){
            pickRepository.save(Pick.createPick(tuteeId, tutorId));
        }else{
            pickRepository.delete(pick);
        }

    }

    // 튜터를 이름으로 검색한다
    @Transactional
    public List<TutorResponse> findTutorByName(String name, Long userId, Specialities specialities){
        List<UserResponse> users = userService.fetchUsersByName(authServer + "/server/tutor/", name,10_000,10_000)
                .stream().filter(e->e.getSpecialities().contains(specialities))
                .collect(Collectors.toList());
        return mapToTutor(users,userId);

    }

    // 튜터를 레슨 횟수를 기반으로 정렬한다
    public List<TutorResponse> sortTutorByLesson(int limit, int page, long userId, String speciality){
        // 0페이지 5개 (0,4) 1페이지 5개 (5,9)
        List<Long> tutors = statisticsService.getTutorByLesson(page*limit,limit,speciality);

        List<UserResponse> users = tutors.stream().map(e->userService.fetchUserResponseFromAuthServer(authServer+"/server/user/id/" + e.toString(),10_000,10_000)
                        .filter(s->!s.getNickName().equals("탈퇴회원"))
                        .orElseThrow(()-> new RuntimeException("서버에러가 발생하였습니다")))
                .collect(Collectors.toList());

        return mapToTutor(users,userId);
    }

    // 튜터를 찜 기반으로 정렬한다
    public List<TutorResponse> sortTutorByPick(int limit, int page, long userId,String speciality){
        // 0페이지 5개 (0,4) 1페이지 5개 (5,9)
        List<Long> tutors = statisticsService.getTutorByPick(page*limit,limit,speciality);

        List<UserResponse> users = tutors.stream().map(e->userService.fetchUserResponseFromAuthServer(authServer+"/server/user/id/" + e.toString(),10_000,10_000)
                        .filter(s->!s.getNickName().equals("탈퇴회원"))
                        .orElseThrow(()-> new RuntimeException("서버에러가 발생하였습니다")))
                .collect(Collectors.toList());

        return mapToTutor(users,userId);
    }
    // 튜터를 리뷰 점수를 기반으로 정렬한다

    public List<TutorResponse> sortTutorByReview(int limit, int page, long userId,String speciality){
        // 0페이지 5개 (0,4) 1페이지 5개 (5,9)
        List<Long> tutors = statisticsService.getTutorByScore(page*limit,limit,speciality);

        List<UserResponse> users = tutors.stream().map(e->userService.fetchUserResponseFromAuthServer(authServer+"/server/user/id/" + e.toString(),10_000,10_000)
                        .orElseThrow(()-> new RuntimeException("서버에러가 발생하였습니다")))
                .filter(s->!s.getNickName().equals("탈퇴회원"))
                .collect(Collectors.toList());

        return mapToTutor(users,userId);
    }

    // 튜터의 디테일 정보를 가져온다
    public TutorDetailResponse getTutorDetail(Long tutorId,Long userId){
        UserResponse userResponse = userService.fetchUserResponseFromAuthServer(authServer+"/server/user/id/" + tutorId.toString(),10_000,10_000)
                .orElseThrow(()->new IllegalStateException("비정상적인 접근입니다"));
        if (!userResponse.getIsTutor()){
            throw new NoSuchElementException("탈퇴한 회원입니다");
        }

        return mapToTutorDetail(userResponse,userId);

    }

    // 튜터에 해당하는 리뷰를 가져온다

    public List<ReviewResponse> getFirstReview(Long tutorId,int limit){
        PageRequest pageRequest = PageRequest.of(0,limit, Sort.by(Sort.Direction.DESC,
                "id"));

        List<Review> reviews = reviewRepository.findReviewByTutorId(tutorId,pageRequest).getContent();
        return mapToReviewResponse(reviews);
    }

    // 다음 리뷰를 가져온다
    public List<ReviewResponse> getNextReview(Long tutorId,Long reviewId,int limit){
        PageRequest pageRequest = PageRequest.of(0,limit, Sort.by(Sort.Direction.DESC,
                "id"));

        List<Review> reviews = reviewRepository.findReviewByTutorIdAfter(tutorId,reviewId,pageRequest).getContent();
        return mapToReviewResponse(reviews);
    }

    // 도메인 객체를 응답 객체로 변환

    private List<ReviewResponse> mapToReviewResponse(List<Review> reviews){
        return
                reviews.stream()
                        .map(e->{
                            UserResponse userResponse = userService.fetchUserResponseFromAuthServer(authServer+"/server/user/id/" + e.getTuteeId(),10_1000,10_1000)
                                    .orElseThrow(()->new IllegalStateException("비정상적인 접근입니다"));
                            return new ReviewResponse(
                                    e.getId(),userResponse.getId(),userResponse.getNickName(),e.getReview(),userResponse.getProfile(), LocalDateTimeToString.convertToLocalDateTimeString(e.getCreatedAt()),e.getScore()
                            );
                        }).collect(Collectors.toList());
    }


    // 도메인 객체를 응답 객체로 변환

    private List<TutorResponse> mapToTutor(List<UserResponse> users,Long userId){

        List<TutorResponse> tutorResponses = users.stream()
                .filter(UserResponse::getIsTutor)
                .map(e-> new TutorResponse(e.getNickName(),e.getId(),e.getSpecialities(),e.getProfile(), reviewRepository.countReviewByTutorId(e.getId())))
                .toList();

        tutorResponses.forEach(e->{
            // 내가 찜했는 지 아닌 지 여부
            Pick pick = pickRepository.findByTutorIdAndTuteeId(e.getId(),userId);

            Integer lessons = statisticsService.getLessonCount(e.getId());
            if(lessons == null){
                lessons = 0;
            }

            Double score = statisticsService.getReviewScore(e.getId());
            if(score == null){
                score = 0.0;
            }

            e.setScore(score);
            e.setLessonCount(lessons);
            e.setPickYn(pick != null);


        });

        return tutorResponses;


    }

    // 도메인 객체를 응답 객체로 변환

    private TutorDetailResponse mapToTutorDetail(UserResponse user,Long userId){

        TutorDetailResponse tutorResponse = new TutorDetailResponse(
                user.getNickName(),user.getId(),user.getSpecialities(),user.getProfile(),user.getSelfDescription(),
                reviewRepository.countReviewByTutorId(user.getId())
        );


        Pick pick = pickRepository.findByTutorIdAndTuteeId(user.getId(),userId);

        Integer lessons = statisticsService.getLessonCount(user.getId());
        if(lessons == null){
            lessons = 0;
        }

        Double score = statisticsService.getReviewScore(user.getId());
        if(score == null){
            score = 0.0;
        }

        tutorResponse.setScore(score);
        tutorResponse.setLessonCount(lessons);
        tutorResponse.setPickYn(pick != null);

        return tutorResponse;


    }


}
