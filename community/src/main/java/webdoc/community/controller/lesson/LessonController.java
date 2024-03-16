package webdoc.community.controller.lesson;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import webdoc.community.domain.entity.feedback.request.FeedbackCreateRequest;
import webdoc.community.domain.entity.lesson.response.LessonResponse;
import webdoc.community.domain.entity.review.request.ReviewCreateRequest;
import webdoc.community.domain.entity.user.response.UserResponse;
import webdoc.community.domain.response.ArrayResponse;
import webdoc.community.domain.response.CodeMessageResponse;
import webdoc.community.domain.response.ObjectResponse;
import webdoc.community.service.LessonService;
import webdoc.community.utility.messageprovider.CommonMessageProvider;
import webdoc.community.utility.messageprovider.ResponseCodeProvider;

import java.time.LocalDateTime;
import java.util.List;

/*
 * 레슨 관련 응답 처리
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/lesson")
public class LessonController {

    private final LessonService lessonService;

    // 아직 피드백,평가를 작성하지 않은 레슨 들
    @GetMapping("/yet")
    public ArrayResponse<LessonResponse> getMissedLessons() {
        try{
            List<LessonResponse> responses;
            UserResponse user = (UserResponse) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (user.getIsTutor()){
                responses = lessonService.getNoFeedbackByTutor(user.getId());
            }else{
                responses = lessonService.getNoReviewByTutee(user.getId());
            }

            return ArrayResponse.of(responses,200);

        }catch(IllegalStateException e){
            throw e;
        }catch(Exception e){
            throw new RuntimeException(e);
        }

    }

    // 월 년 별 레슨기록
    @GetMapping("/detail")
    public ArrayResponse<LessonResponse> getLessonByYearAndMonth(@RequestParam Integer year, @RequestParam Integer month){

        try{
            List<LessonResponse> responses;
            UserResponse user = (UserResponse) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            responses = lessonService.fetchLessonByYearAndMonth(year,month,user.getId());

            return ArrayResponse.of(responses,200);

        }catch(IllegalStateException e){
            throw e;
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    // 리뷰 작성
    @PutMapping("/review")
    public CodeMessageResponse enrollReview(@Validated @RequestBody ReviewCreateRequest request, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            throw new IllegalArgumentException("바인딩 실패");
        }
        try{
            UserResponse user = (UserResponse) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            // 튜터는 리뷰 작성 불가
            if (user.getIsTutor()) throw new IllegalStateException("비정상적인 접근입니다");
            lessonService.WriteReview(request.getLessonId(),user.getId(),request.getReview(),request.getScore());

            return new CodeMessageResponse(CommonMessageProvider.REQUEST_SUCCESS,200, ResponseCodeProvider.SUCCESS);

        }catch(IllegalStateException e){
            throw e;
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    // 피드백 작성
    @PutMapping("/feedback")
    public CodeMessageResponse enrollFeedback(@Validated @RequestBody FeedbackCreateRequest request, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            throw new IllegalArgumentException("바인딩 실패");
        }
        try{
            UserResponse user = (UserResponse) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            // 튜티는 피드백 작성 불가
            if (!user.getIsTutor()) throw new IllegalStateException("비정상적인 접근입니다");
            lessonService.WriteFeedback(request.getLessonId(),user.getId(),request.getFeedback());

            return new CodeMessageResponse(CommonMessageProvider.REQUEST_SUCCESS,200, ResponseCodeProvider.SUCCESS);

        }catch(IllegalStateException e){
            throw e;
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    // 레슨 종료
    @GetMapping("/end")
    public CodeMessageResponse closeLesson(@RequestParam Long lessonId){
        try{
            UserResponse user = (UserResponse) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            // 튜티는 종료 불가
            if (!user.getIsTutor()) throw new IllegalStateException("비정상적인 접근입니다");
            lessonService.endLesson(lessonId,user.getId());

            return new CodeMessageResponse(CommonMessageProvider.REQUEST_SUCCESS,200, ResponseCodeProvider.SUCCESS);

        }catch(IllegalStateException e){
            throw e;
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    // 현재 진행 중인 레슨정보
    @GetMapping("/ongoing")
    public ObjectResponse<LessonResponse> fetchOngoingLesson(){
        try{

            UserResponse user = (UserResponse) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return new ObjectResponse<>(lessonService.fetchOnGoingLesson(user.getId()),200);

        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    // 특정회원 레슨 존재여부
    @GetMapping("/server")
    public String hasLesson(){
        try{

            UserResponse user = (UserResponse) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Boolean result = lessonService.hasLesson(user.getId(), LocalDateTime.now());

            if (result) return "YES";
            else return "NO";

        }catch(Exception e){
            throw new RuntimeException(e);
        }

    }

    // 특정 레슨의 정보 받아 오기
    @GetMapping("/{lessonId}")
    public ObjectResponse<LessonResponse> enterLesson(@PathVariable Long lessonId){
        try{
            return new ObjectResponse<LessonResponse>(lessonService.fetchRemoteLesson(lessonId),200);
        }catch(Exception e){
            throw new RuntimeException("서버에러",e);
        }
    }



}
