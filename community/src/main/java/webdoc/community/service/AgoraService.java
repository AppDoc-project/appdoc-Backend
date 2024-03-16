package webdoc.community.service;

import io.agora.media.RtcTokenBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webdoc.community.domain.entity.lesson.Lesson;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
/*
 * 화상 채팅을 위한 서비스
 */
public class AgoraService {

    private static String appId = "c980d1cddfc548579e6503150f4d7005";
    private static String appCertificate = "84b8019b3cb64989ad1d402c77332cb3";
    private static int expirationTimeInSeconds = 3600*24;

    // 화상 채팅을 위한 token을 발행한다
    public void createChannel(Lesson lesson,Long tutorId, Long tuteeId){

        RtcTokenBuilder token = new RtcTokenBuilder();
        int timestamp = (int) (System.currentTimeMillis() / 1000 + expirationTimeInSeconds);
        String channelName = UUID.randomUUID().toString();

        String tuteeToken = token.buildTokenWithUserAccount(appId, appCertificate,
                channelName, tuteeId.toString(), RtcTokenBuilder.Role.Role_Publisher, timestamp);

        String tutorToken = token.buildTokenWithUserAccount(appId, appCertificate,
                channelName, tutorId.toString(), RtcTokenBuilder.Role.Role_Publisher, timestamp);

        lesson.setChannelName(channelName);
        lesson.setTuteeToken(tuteeToken);
        lesson.setTutorToken(tutorToken);

    }

}
