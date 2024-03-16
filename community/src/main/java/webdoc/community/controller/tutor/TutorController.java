package webdoc.community.controller.tutor;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import webdoc.community.domain.entity.pick.request.PickToggleRequest;
import webdoc.community.domain.entity.review.response.ReviewResponse;
import webdoc.community.domain.entity.tutor.response.TutorDetailResponse;
import webdoc.community.domain.entity.tutor.response.TutorResponse;
import webdoc.community.domain.entity.user.Specialities;
import webdoc.community.domain.entity.user.TutorSortType;
import webdoc.community.domain.entity.user.response.UserResponse;
import webdoc.community.domain.response.ArrayResponse;
import webdoc.community.domain.response.CodeMessageResponse;
import webdoc.community.domain.response.ObjectResponse;
import webdoc.community.service.TutorProfileService;
import webdoc.community.utility.messageprovider.CommonMessageProvider;
import webdoc.community.utility.messageprovider.ResponseCodeProvider;

import java.util.List;
import java.util.NoSuchElementException;


/*
* 튜터 관련 응답 처리
*/
@RestController
@RequestMapping("/tutor")
@Slf4j
@RequiredArgsConstructor
public class TutorController {

    private final TutorProfileService tutorProfileService;

    // 튜터를 찜하는 기능 제공
    @PostMapping("/pick")
    public CodeMessageResponse togglePick(@Validated @RequestBody PickToggleRequest pickToggleRequest,
                                          BindingResult bindingResult, HttpServletRequest req){
        if (bindingResult.hasErrors()){
            throw new IllegalArgumentException("바인딩 실패");
        }

        try{

            UserResponse user = (UserResponse) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            tutorProfileService.toggleTutor(user.getId(),pickToggleRequest.getTutorId());
            return new CodeMessageResponse(CommonMessageProvider.REQUEST_SUCCESS,200, ResponseCodeProvider.SUCCESS);

        }catch(IllegalStateException e){
            throw e;
        }catch(Exception e){
            throw new RuntimeException(e);
        }


    }

    // 튜터를 이름으로 검색하는 기능 제공
    @GetMapping("/name")
    public ArrayResponse<TutorResponse> fetchTutorByName(@RequestParam String name,@RequestParam(name = "speciality")Specialities specialities){
        try{

            UserResponse user = (UserResponse) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            List<TutorResponse> tutors = tutorProfileService.findTutorByName(name,user.getId(),specialities);
            return new ArrayResponse<>(tutors,200,tutors.size());

        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    // 튜터를 정렬 기준에 맞게 탐색하는 기능 제공
    @GetMapping("/sort")
    public ArrayResponse<TutorResponse> fetchTutorByLesson(@RequestParam int page, @RequestParam int limit, @RequestParam("type") TutorSortType type,
            @RequestParam("speciality") Specialities specialities, HttpServletRequest req){
        try{
            UserResponse user = (UserResponse) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            List<TutorResponse> tutors;
            if (type == TutorSortType.LESSON){
                tutors = tutorProfileService.sortTutorByLesson(limit,page, user.getId(),specialities.toString());
            }else if(type == TutorSortType.PICK){
                tutors = tutorProfileService.sortTutorByPick(limit,page, user.getId(),specialities.toString());
            }else{
                tutors = tutorProfileService.sortTutorByReview(limit,page, user.getId(),specialities.toString());
            }
            return new ArrayResponse<>(tutors,200,tutors.size());
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    // 특정 튜터의 상세정보를 탐색하는 기능 제공
    @GetMapping("/detail")
    public ObjectResponse<TutorDetailResponse> fetchTutorDetail(@RequestParam Long tutorId,HttpServletRequest req){
        try{

            UserResponse user = (UserResponse) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return new ObjectResponse<>(tutorProfileService.getTutorDetail(tutorId, user.getId()),200);

        }catch(NoSuchElementException e){
            throw e;
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    // 특정 튜터의 리뷰를 탐색하는 기능 제공
    @GetMapping("/review")
    public ArrayResponse<ReviewResponse> fetchReviews(@RequestParam boolean scroll, @RequestParam Long tutorId, @RequestParam int limit,
                                                      @RequestParam(required = false) Long reviewId,HttpServletRequest req){
        if (scroll && reviewId == null){
            throw new IllegalArgumentException("바인딩 실패");
        }
        try{
            if(!scroll){
                List<ReviewResponse> response = tutorProfileService.getFirstReview(tutorId,limit);
                return new ArrayResponse<>(response,200,response.size());
            }
            else{
                List<ReviewResponse> response = tutorProfileService.getNextReview(tutorId, reviewId,limit);
                return new ArrayResponse<>(response,200,response.size());
            }

        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

}
