package webdoc.community.domain.entity.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Token {
    protected Token(){

    }

    public static Token createToken(LocalDateTime expiredAt, String value, User user){
        Token token = new Token();

        token.expiredAt = expiredAt;
        token.value = value;
        token.user = user;

        return token;
    }
    @Id
    @GeneratedValue
    private Long id;

    // 토큰 유효 기간 지정
    @Column(nullable = false)
    private LocalDateTime expiredAt;

    // 중복 로그인을 차단하기 위한 최소한의 조치
    @Column(nullable = false)
    private String value;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private User user;
}
