package webdoc.authentication.domain.entity.user.tutor.enums;

import webdoc.authentication.domain.entity.TutorSpeciality;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public enum Specialities {
    PIANO("피아노"), GUITAR("기타"), VOCAL("보컬"),
    DRUM("드럼"),BASS("베이스"), MUSIC_THEORY("음악이론"),
    COMPOSITION("작곡"), WIND_INSTRUMENT("관악기"), STRING_INSTRUMENT("현악기"),
    KEYBOARD_INSTRUMENT("건반악기");


    private final String name;

    Specialities(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static String enumToString(List<Specialities> specialities){
        String ret = "";
        for (Specialities speciality : specialities){
            ret += speciality.getName() + " ";
        }
        return ret;
    }


    public static List<Specialities> stringToEnum(String s){
        return Arrays.stream(s.split(" "))
                .map(e->{
                    return Arrays.stream(Specialities.values())
                            .filter(t->{
                                return t.getName().equals(e);

                            }).findFirst().orElseThrow(()-> new NoSuchElementException("해당하는 enum이 없습니다"));
                }).collect(Collectors.toList());
    }
}