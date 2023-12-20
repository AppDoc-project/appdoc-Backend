package webdoc.community.config.init;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;
import webdoc.community.domain.entity.community.Community;
import webdoc.community.repository.CommunityRepository;

import java.util.List;

@RequiredArgsConstructor
public class PostConstruct {
    private final CommunityRepository communityRepository;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void devInit(){
        Community piano = Community.createCommunity("피아노");
        Community guitar = Community.createCommunity("기타");
        Community vocal = Community.createCommunity("보컬");
        Community drum = Community.createCommunity("드럼");
        Community bass = Community.createCommunity("베이스");
        Community composition = Community.createCommunity("작곡");
        Community windInst = Community.createCommunity("관악기");
        Community stringInst = Community.createCommunity("현악기");
        Community keyboardInst = Community.createCommunity("건반악기");
        Community freeBoard = Community.createCommunity("자유게시판");

        communityRepository.saveAll(List.of(
                piano,guitar,vocal,drum,bass,composition,windInst,stringInst,keyboardInst,freeBoard
        ));

    }

}
