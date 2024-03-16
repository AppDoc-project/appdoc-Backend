package webdoc.authentication.controller.dev;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import webdoc.authentication.domain.entity.TutorSpeciality;
import webdoc.authentication.domain.entity.user.tutee.Tutee;
import webdoc.authentication.domain.entity.user.tutor.Tutor;
import webdoc.authentication.domain.entity.user.tutor.enums.AuthenticationProcess;
import webdoc.authentication.domain.entity.user.tutor.enums.Specialities;
import webdoc.authentication.domain.response.CodeMessageResponse;
import webdoc.authentication.repository.UserRepository;
import java.util.List;
/*
* 개발 데이터 초기화
 */
@RestController
@RequestMapping("/auth/init")
@RequiredArgsConstructor
public class DevController {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @GetMapping("/tutee")
    @Transactional
    public CodeMessageResponse insertBasic(){



        Tutee tutee1 = Tutee.createTutee(
                "tutee@gmail.com",passwordEncoder.encode("tutee1234"),"김튜티","01042112234"
        );

        Tutee tutee2 = Tutee.createTutee(
                "tutee1@gmail.com","tutee1234","김민석","01042112234"
        );

        Tutee tutee3 = Tutee.createTutee(
                "tutee2@gmail.com","tutee1234","김민식","01042112234"
        );

        Tutee tutee4 = Tutee.createTutee(
                "tutee3@gmail.com","tutee1234","김준수","01042112234"
        );

        Tutee tutee5 = Tutee.createTutee(
                "tutee4@gmail.com","tutee1234","김준철","01042112234"
        );

        Tutee tutee6 = Tutee.createTutee(
                "tutee5@gmail.com","tutee1234","송하윤","01042112234"
        );

        Tutee tutee7 = Tutee.createTutee(
                "tutee6@gmail.com","tutee1234","박준철","01042112234"
        );

        Tutee tutee8 = Tutee.createTutee(
                "tutee7@gmail.com","tutee1234","심영철","01042112234"
        );

        Tutee tutee9 = Tutee.createTutee(
                "tutee8@gmail.com","tutee1234","박도순","01042112234"
        );

        Tutee tutee10 = Tutee.createTutee(
                "tutee9@gmail.com","tutee1234","김선일","01042112234"
        );

        userRepository.saveAll(List.of(tutee1,tutee2,tutee3,tutee4,tutee5,tutee6,tutee7,tutee8,tutee9,tutee10));


        return new CodeMessageResponse("개발 데이터 삽입 성공",200,200);

    }

    @GetMapping("/bass")
    @Transactional
    // Bass + Composition
    public CodeMessageResponse insertBass(){

        Tutor bass1 = createTutor(
                "http://sssss.com","01042325779","tutor@100.com",
                "김제니",passwordEncoder.encode("tutor1234"),"ROLE_TUTOR","베이스 전문가",AuthenticationProcess.AUTHENTICATION_SUCCESS
        );

        Tutor bass2 = createTutor(
                "http://sssss.com","01042325770","tutor@101.com",
                "이제니",passwordEncoder.encode("tutor1234"),"ROLE_TUTOR","베이스 전문가",AuthenticationProcess.AUTHENTICATION_SUCCESS
        );

        Tutor bass3 = createTutor(
                "http://sssss.com","01042325778","tutor@102.com",
                "최제니",passwordEncoder.encode("tutor1234"),"ROLE_TUTOR","베이스 전문가",AuthenticationProcess.AUTHENTICATION_SUCCESS
        );

        Tutor bass4 = createTutor(
                "http://sssss.com","01042325777","tutor@103.com",
                "박제니",passwordEncoder.encode("tutor1234"),"ROLE_TUTOR","베이스 전문가",AuthenticationProcess.AUTHENTICATION_SUCCESS
        );

        Tutor bass5 = createTutor(
                "http://sssss.com","01042325776","tutor@104.com",
                "홍제니",passwordEncoder.encode("tutor1234"),"ROLE_TUTOR","베이스 전문가",AuthenticationProcess.AUTHENTICATION_SUCCESS
        );

        bass1.setTutorSpecialities(List.of(new TutorSpeciality(bass1, Specialities.BASS)));
        bass2.setTutorSpecialities(List.of(new TutorSpeciality(bass2, Specialities.BASS)));
        bass3.setTutorSpecialities(List.of(new TutorSpeciality(bass3, Specialities.BASS)));
        bass4.setTutorSpecialities(List.of(new TutorSpeciality(bass4, Specialities.BASS)));
        bass5.setTutorSpecialities(List.of(new TutorSpeciality(bass5, Specialities.BASS)));

        Tutor comp1 = createTutor(
                "http://sssss.com","01042325719","tutor@105.com",
                "김세령",passwordEncoder.encode("tutor1234"),"ROLE_TUTOR","작곡 전문가",AuthenticationProcess.AUTHENTICATION_SUCCESS
        );
        Tutor comp2 = createTutor(
                "http://sssss.com","01042325720","tutor@106.com",
                "이세령",passwordEncoder.encode("tutor1234"),"ROLE_TUTOR","작곡 전문가",AuthenticationProcess.AUTHENTICATION_SUCCESS
        );
        Tutor comp3 = createTutor(
                "http://sssss.com","01042325738","tutor@107.com",
                "최세령",passwordEncoder.encode("tutor1234"),"ROLE_TUTOR","작곡 전문가",AuthenticationProcess.AUTHENTICATION_SUCCESS
        );
        Tutor comp4 = createTutor(
                "http://sssss.com","01042325747","tutor@108.com",
                "박세령",passwordEncoder.encode("tutor1234"),"ROLE_TUTOR","작곡 전문가",AuthenticationProcess.AUTHENTICATION_SUCCESS
        );
        Tutor comp5 = createTutor(
                "http://sssss.com","01042325756","tutor@109.com",
                "홍세령",passwordEncoder.encode("tutor1234"),"ROLE_TUTOR","작곡 전문가",AuthenticationProcess.AUTHENTICATION_SUCCESS
        );

        comp1.setTutorSpecialities(List.of(new TutorSpeciality(comp1, Specialities.COMPOSITION)));
        comp2.setTutorSpecialities(List.of(new TutorSpeciality(comp2, Specialities.COMPOSITION)));
        comp3.setTutorSpecialities(List.of(new TutorSpeciality(comp3, Specialities.COMPOSITION)));
        comp4.setTutorSpecialities(List.of(new TutorSpeciality(comp4, Specialities.COMPOSITION)));
        comp5.setTutorSpecialities(List.of(new TutorSpeciality(comp5, Specialities.COMPOSITION)));

        userRepository.saveAll(
                List.of(bass1,bass2,bass3,bass4,bass5,comp1,comp2,comp3,comp4,comp5)
        );

        return new CodeMessageResponse("개발 데이터 삽입 성공",200,200);


    }


    @GetMapping("/guitar")
    @Transactional
    // Guitar + Keyboard
    public CodeMessageResponse insertGuitar(){

        Tutor guitar1 = createTutor(
                "http://sssss.com","01042325779","tutor@110.com",
                "김진호",passwordEncoder.encode("tutor1234"),"ROLE_TUTOR","기타 전문가",AuthenticationProcess.AUTHENTICATION_SUCCESS
        );
        Tutor guitar2 = createTutor(
                "http://sssss.com","01042325770","tutor@111.com",
                "이진호",passwordEncoder.encode("tutor1234"),"ROLE_TUTOR","기타 전문가",AuthenticationProcess.AUTHENTICATION_SUCCESS
        );
        Tutor guitar3 = createTutor(
                "http://sssss.com","01042325778","tutor@112.com",
                "최진호",passwordEncoder.encode("tutor1234"),"ROLE_TUTOR","기타 전문가",AuthenticationProcess.AUTHENTICATION_SUCCESS
        );
        Tutor guitar4 = createTutor(
                "http://sssss.com","01042325777","tutor@113.com",
                "박진호",passwordEncoder.encode("tutor1234"),"ROLE_TUTOR","기타 전문가",AuthenticationProcess.AUTHENTICATION_SUCCESS
        );
        Tutor guitar5 = createTutor(
                "http://sssss.com","01042325776","tutor@114.com",
                "홍제니",passwordEncoder.encode("tutor1234"),"ROLE_TUTOR","기타 전문가",AuthenticationProcess.AUTHENTICATION_SUCCESS
        );

        guitar1.setTutorSpecialities(List.of(new TutorSpeciality(guitar1, Specialities.GUITAR)));
        guitar2.setTutorSpecialities(List.of(new TutorSpeciality(guitar2, Specialities.GUITAR)));
        guitar3.setTutorSpecialities(List.of(new TutorSpeciality(guitar3, Specialities.GUITAR)));
        guitar4.setTutorSpecialities(List.of(new TutorSpeciality(guitar4, Specialities.GUITAR)));
        guitar5.setTutorSpecialities(List.of(new TutorSpeciality(guitar5, Specialities.GUITAR)));



        Tutor key = createTutor(
                "http://sssss.com","01042325719","tutor@115.com",
                "김하정",passwordEncoder.encode("tutor1234"),"ROLE_TUTOR","건반악기 전문가",AuthenticationProcess.AUTHENTICATION_SUCCESS
        );
        Tutor key2 = createTutor(
                "http://sssss.com","01042325720","tutor@116.com",
                "이하정",passwordEncoder.encode("tutor1234"),"ROLE_TUTOR","건반악기 전문가",AuthenticationProcess.AUTHENTICATION_SUCCESS
        );
        Tutor key3 = createTutor(
                "http://sssss.com","01042325738","tutor@117.com",
                "최하정",passwordEncoder.encode("tutor1234"),"ROLE_TUTOR","건반악기 전문가",AuthenticationProcess.AUTHENTICATION_SUCCESS
        );
        Tutor key4 = createTutor(
                "http://sssss.com","01042325747","tutor@118.com",
                "박하정",passwordEncoder.encode("tutor1234"),"ROLE_TUTOR","건반악기 전문가",AuthenticationProcess.AUTHENTICATION_SUCCESS
        );
        Tutor key5 = createTutor(
                "http://sssss.com","01042325756","tutor@119.com",
                "홍하정",passwordEncoder.encode("tutor1234"),"ROLE_TUTOR","건반악기 전문가",AuthenticationProcess.AUTHENTICATION_SUCCESS
        );

        key.setTutorSpecialities(List.of(new TutorSpeciality(key, Specialities.KEYBOARD_INSTRUMENT)));
        key2.setTutorSpecialities(List.of(new TutorSpeciality(key2, Specialities.KEYBOARD_INSTRUMENT)));
        key3.setTutorSpecialities(List.of(new TutorSpeciality(key3, Specialities.KEYBOARD_INSTRUMENT)));
        key4.setTutorSpecialities(List.of(new TutorSpeciality(key4, Specialities.KEYBOARD_INSTRUMENT)));
        key5.setTutorSpecialities(List.of(new TutorSpeciality(key5, Specialities.KEYBOARD_INSTRUMENT)));

        userRepository.saveAll(
                List.of(guitar1,guitar2,guitar3,guitar4,guitar5,key,key2,key3,key4,key5)
        );



        return new CodeMessageResponse("개발 데이터 삽입 성공",200,200);
    }

    @GetMapping("/music")
    @Transactional
    // Music + Piano
    public CodeMessageResponse insertMusic(){

        Tutor music1 = createTutor(
                "http://sssss.com","01042325779","tutor@120.com",
                "김진석",passwordEncoder.encode("tutor1234"),"ROLE_TUTOR","이론 전문가",AuthenticationProcess.AUTHENTICATION_SUCCESS
        );
        Tutor music2 = createTutor(
                "http://sssss.com","01042325770","tutor@121.com",
                "이진석",passwordEncoder.encode("tutor1234"),"ROLE_TUTOR","이론 전문가",AuthenticationProcess.AUTHENTICATION_SUCCESS
        );
        Tutor music3 = createTutor(
                "http://sssss.com","01042325778","tutor@122.com",
                "최진석",passwordEncoder.encode("tutor1234"),"ROLE_TUTOR","이론 전문가",AuthenticationProcess.AUTHENTICATION_SUCCESS
        );
        Tutor music4 = createTutor(
                "http://sssss.com","01042325777","tutor@123.com",
                "박진석",passwordEncoder.encode("tutor1234"),"ROLE_TUTOR","이론 전문가",AuthenticationProcess.AUTHENTICATION_SUCCESS
        );
        Tutor music5 = createTutor(
                "http://sssss.com","01042325776","tutor@124.com",
                "홍진석",passwordEncoder.encode("tutor1234"),"ROLE_TUTOR","이론 전문가",AuthenticationProcess.AUTHENTICATION_SUCCESS
        );

        music1.setTutorSpecialities(List.of(new TutorSpeciality(music1, Specialities.MUSIC_THEORY)));
        music2.setTutorSpecialities(List.of(new TutorSpeciality(music2, Specialities.MUSIC_THEORY)));
        music3.setTutorSpecialities(List.of(new TutorSpeciality(music3, Specialities.MUSIC_THEORY)));
        music4.setTutorSpecialities(List.of(new TutorSpeciality(music4, Specialities.MUSIC_THEORY)));
        music5.setTutorSpecialities(List.of(new TutorSpeciality(music5, Specialities.MUSIC_THEORY)));



        Tutor piano1 = createTutor(
                "http://sssss.com","01042325719","tutor@125.com",
                "김시은",passwordEncoder.encode("tutor1234"),"ROLE_TUTOR","피아노 전문가",AuthenticationProcess.AUTHENTICATION_SUCCESS
        );
        Tutor piano2 = createTutor(
                "http://sssss.com","01042325720","tutor@126.com",
                "이시은",passwordEncoder.encode("tutor1234"),"ROLE_TUTOR","피아노 전문가",AuthenticationProcess.AUTHENTICATION_SUCCESS
        );
        Tutor piano3 = createTutor(
                "http://sssss.com","01042325738","tutor@127.com",
                "최시은",passwordEncoder.encode("tutor1234"),"ROLE_TUTOR","피아노 전문가",AuthenticationProcess.AUTHENTICATION_SUCCESS
        );
        Tutor piano4 = createTutor(
                "http://sssss.com","01042325747","tutor@128.com",
                "박시은",passwordEncoder.encode("tutor1234"),"ROLE_TUTOR","피아노 전문가",AuthenticationProcess.AUTHENTICATION_SUCCESS
        );
        Tutor piano5 = createTutor(
                "http://sssss.com","01042325756","tutor@129.com",
                "홍시은",passwordEncoder.encode("tutor1234"),"ROLE_TUTOR","피아노 전문가",AuthenticationProcess.AUTHENTICATION_SUCCESS
        );

        piano1.setTutorSpecialities(List.of(new TutorSpeciality(piano1, Specialities.PIANO)));
        piano2.setTutorSpecialities(List.of(new TutorSpeciality(piano2, Specialities.PIANO)));
        piano3.setTutorSpecialities(List.of(new TutorSpeciality(piano3, Specialities.PIANO)));
        piano4.setTutorSpecialities(List.of(new TutorSpeciality(piano4, Specialities.PIANO)));
        piano5.setTutorSpecialities(List.of(new TutorSpeciality(piano5, Specialities.PIANO)));

        userRepository.saveAll(
                List.of(music1,music2,music3,music4,music5,piano1,piano2,piano3,piano4,piano5)
        );



        return new CodeMessageResponse("개발 데이터 삽입 성공",200,200);
    }

    @GetMapping("/string")
    @Transactional
    // string + vocal
    public CodeMessageResponse insertString(){

        Tutor string1 = createTutor(
                "http://sssss.com","01042325779","tutor@130.com",
                "김재희",passwordEncoder.encode("tutor1234"),"ROLE_TUTOR","현악기 전문가",AuthenticationProcess.AUTHENTICATION_SUCCESS
        );
        Tutor string2 = createTutor(
                "http://sssss.com","01042325770","tutor@131.com",
                "이재희",passwordEncoder.encode("tutor1234"),"ROLE_TUTOR","현악기 전문가",AuthenticationProcess.AUTHENTICATION_SUCCESS
        );
        Tutor string3 = createTutor(
                "http://sssss.com","01042325778","tutor@132.com",
                "최재희",passwordEncoder.encode("tutor1234"),"ROLE_TUTOR","현악기 전문가",AuthenticationProcess.AUTHENTICATION_SUCCESS
        );
        Tutor string4 = createTutor(
                "http://sssss.com","01042325777","tutor@133.com",
                "박재희",passwordEncoder.encode("tutor1234"),"ROLE_TUTOR","현악기 전문가",AuthenticationProcess.AUTHENTICATION_SUCCESS
        );
        Tutor string5 = createTutor(
                "http://sssss.com","01042325776","tutor@134.com",
                "홍재희",passwordEncoder.encode("tutor1234"),"ROLE_TUTOR","현악기 전문가",AuthenticationProcess.AUTHENTICATION_SUCCESS
        );

        string1.setTutorSpecialities(List.of(new TutorSpeciality(string1, Specialities.STRING_INSTRUMENT)));
        string2.setTutorSpecialities(List.of(new TutorSpeciality(string2, Specialities.STRING_INSTRUMENT)));
        string3.setTutorSpecialities(List.of(new TutorSpeciality(string3, Specialities.STRING_INSTRUMENT)));
        string4.setTutorSpecialities(List.of(new TutorSpeciality(string4, Specialities.STRING_INSTRUMENT)));
        string5.setTutorSpecialities(List.of(new TutorSpeciality(string5, Specialities.STRING_INSTRUMENT)));



        Tutor vocal1 = createTutor(
                "http://sssss.com","01042325719","tutor@135.com",
                "김민규",passwordEncoder.encode("tutor1234"),"ROLE_TUTOR","보컬 전문가",AuthenticationProcess.AUTHENTICATION_SUCCESS
        );
        Tutor vocal2 = createTutor(
                "http://sssss.com","01042325720","tutor@136.com",
                "이민규",passwordEncoder.encode("tutor1234"),"ROLE_TUTOR","보컬 전문가",AuthenticationProcess.AUTHENTICATION_SUCCESS
        );
        Tutor vocal3 = createTutor(
                "http://sssss.com","01042325738","tutor@137.com",
                "최민규",passwordEncoder.encode("tutor1234"),"ROLE_TUTOR","보컬 전문가",AuthenticationProcess.AUTHENTICATION_SUCCESS
        );
        Tutor vocal4 = createTutor(
                "http://sssss.com","01042325747","tutor@138.com",
                "박민규",passwordEncoder.encode("tutor1234"),"ROLE_TUTOR","보컬 전문가",AuthenticationProcess.AUTHENTICATION_SUCCESS
        );
        Tutor vocal5 = createTutor(
                "http://sssss.com","01042325756","tutor@139.com",
                "홍민규",passwordEncoder.encode("tutor1234"),"ROLE_TUTOR","보컬 전문가",AuthenticationProcess.AUTHENTICATION_SUCCESS
        );

        vocal1.setTutorSpecialities(List.of(new TutorSpeciality(vocal1, Specialities.VOCAL)));
        vocal2.setTutorSpecialities(List.of(new TutorSpeciality(vocal2, Specialities.VOCAL)));
        vocal3.setTutorSpecialities(List.of(new TutorSpeciality(vocal3, Specialities.VOCAL)));
        vocal4.setTutorSpecialities(List.of(new TutorSpeciality(vocal4, Specialities.VOCAL)));
        vocal5.setTutorSpecialities(List.of(new TutorSpeciality(vocal5, Specialities.VOCAL)));

        userRepository.saveAll(
                List.of(vocal1,vocal2,vocal3,vocal4,vocal5,string1,string2,string3,string4,string5)
        );



        return new CodeMessageResponse("개발 데이터 삽입 성공",200,200);
    }

    @GetMapping("/wind")
    @Transactional
    // wind
    public CodeMessageResponse insertWind(){

        Tutor wind1 = createTutor(
                "http://sssss.com","01042325779","tutor@140.com",
                "김희주",passwordEncoder.encode("tutor1234"),"ROLE_TUTOR","관악기 전문가",AuthenticationProcess.AUTHENTICATION_SUCCESS
        );
        Tutor wind2 = createTutor(
                "http://sssss.com","01042325770","tutor@141.com",
                "이희주 ",passwordEncoder.encode("tutor1234"),"ROLE_TUTOR","관악기 전문가",AuthenticationProcess.AUTHENTICATION_SUCCESS
        );
        Tutor wind3 = createTutor(
                "http://sssss.com","01042325778","tutor@142.com",
                "최희주",passwordEncoder.encode("tutor1234"),"ROLE_TUTOR","관악기 전문가",AuthenticationProcess.AUTHENTICATION_SUCCESS
        );
        Tutor wind4 = createTutor(
                "http://sssss.com","01042325777","tutor@143.com",
                "박희주",passwordEncoder.encode("tutor1234"),"ROLE_TUTOR","관악기 전문가",AuthenticationProcess.AUTHENTICATION_SUCCESS
        );
        Tutor wind5 = createTutor(
                "http://sssss.com","01042325776","tutor@144.com",
                "홍희주",passwordEncoder.encode("tutor1234"),"ROLE_TUTOR","관악기 전문가",AuthenticationProcess.AUTHENTICATION_SUCCESS
        );

        Tutor tutor = createTutor(
                "http://ssss.com","01025045779","tutor@gmail.com","김튜터",
                passwordEncoder.encode("tutor1234"),"ROLE_TUTOR","",AuthenticationProcess.AUTHENTICATION_SUCCESS
        );
        tutor.setTutorSpecialities(List.of(new TutorSpeciality(tutor,Specialities.BASS)));

        wind1.setTutorSpecialities(List.of(new TutorSpeciality(wind1, Specialities.WIND_INSTRUMENT)));
        wind2.setTutorSpecialities(List.of(new TutorSpeciality(wind2, Specialities.WIND_INSTRUMENT)));
        wind3.setTutorSpecialities(List.of(new TutorSpeciality(wind3, Specialities.WIND_INSTRUMENT)));
        wind4.setTutorSpecialities(List.of(new TutorSpeciality(wind4, Specialities.WIND_INSTRUMENT)));
        wind5.setTutorSpecialities(List.of(new TutorSpeciality(wind5, Specialities.WIND_INSTRUMENT)));


        userRepository.saveAll(
                List.of(wind1,wind2,wind3,wind4,wind5)
        );



        return new CodeMessageResponse("개발 데이터 삽입 성공",200,200);
    }




    public Tutor createTutor(String authenticationAddress, String contact,String email,
                             String name, String password,String role,String selfDescription,AuthenticationProcess authenticationProcess){
        return
                Tutor.builder()
                        .authenticationAddress(authenticationAddress)
                        .contact(contact)
                        .email(email)
                        .name(name)
                        .password(password)
                        .role(role)
                        .selfDescription(selfDescription)
                        .authenticationProcess(authenticationProcess)
                        .build();
    }
}
