package webdoc.community.controller.tutor;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import webdoc.community.domain.entity.reservation.request.ReservationCreateRequest;
import webdoc.community.domain.entity.reservation.response.ReservationResponse;
import webdoc.community.domain.entity.user.response.UserResponse;
import webdoc.community.domain.exceptions.TuteeAlreadyHasReservationException;
import webdoc.community.domain.exceptions.TutorAlreadyHasReservationException;
import webdoc.community.domain.exceptions.UserNotExistException;
import webdoc.community.domain.response.ArrayResponse;
import webdoc.community.domain.response.CodeMessageResponse;
import webdoc.community.domain.response.ObjectResponse;
import webdoc.community.service.ReservationService;
import webdoc.community.utility.messageprovider.CommonMessageProvider;
import webdoc.community.utility.messageprovider.ResponseCodeProvider;

import java.util.List;

/*
 * 예약 관련 응답 처리
 */
@RestController
@RequestMapping("/tutor/reservation")
@Slf4j
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;



    // 예약을 등록하는 api
    @PostMapping
    public CodeMessageResponse enrollReservation(@Validated @RequestBody  ReservationCreateRequest createRequest, BindingResult bindingResult, HttpServletRequest req){
        if(bindingResult.hasErrors()){
            throw new IllegalArgumentException("바인딩 실패");
        }
        try{

            UserResponse user = (UserResponse) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (!user.getIsTutor()) throw new IllegalStateException("비정상적인 접근 입니다");
            reservationService.enrollReservation(createRequest, user.getId());

            return new CodeMessageResponse(CommonMessageProvider.REQUEST_SUCCESS,200, ResponseCodeProvider.SUCCESS);

        }catch(UserNotExistException | TuteeAlreadyHasReservationException | TutorAlreadyHasReservationException | IllegalStateException | IllegalArgumentException e){
            throw e;
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    // 예약 목록을 가져오는 api
    @GetMapping("/list")
    public ArrayResponse<ReservationResponse> fetchList(HttpServletRequest req){

        try{

            UserResponse user = (UserResponse) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            List<ReservationResponse> responseList = reservationService.getMyReservations(user.getId(),user.getIsTutor());

            return new ArrayResponse<>(responseList,200,responseList.size());

        }catch(IllegalStateException e){
            throw e;
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }





    // 예약을 취소하는 api
    @DeleteMapping
    public CodeMessageResponse cancelReservation(@RequestParam Long reservationId){
        try{

            UserResponse user = (UserResponse) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            reservationService.cancelReservation(reservationId,user.getId());
            return new CodeMessageResponse(CommonMessageProvider.REQUEST_SUCCESS,200, ResponseCodeProvider.SUCCESS);

        }catch(IllegalStateException e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    // 임박한 예약을 가져오는 api
    @GetMapping("/upcoming")
    public ObjectResponse<ReservationResponse> fetchUpcoming(){
        try{

            UserResponse user = (UserResponse) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return new ObjectResponse<>(reservationService.fetchUpcomingReservation(user.getId()),200);

        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    // 예약 존재여부를 확인하는 로직
    @GetMapping("/server")
    public String hasReservation(){
        boolean result;
        try{

            UserResponse user = (UserResponse) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            result = reservationService.hasReservation(user.getId());
            if(result) return "YES";
            return "NO";

        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
