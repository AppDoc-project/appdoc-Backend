package webdoc.community.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import webdoc.community.domain.entity.community.Community;
import webdoc.community.domain.entity.community.CommunityResponse;
import webdoc.community.domain.entity.post.Picture;
import webdoc.community.domain.entity.post.Post;
import webdoc.community.domain.entity.post.request.PostCreateRequest;
import webdoc.community.domain.entity.user.User;
import webdoc.community.repository.CommunityRepository;
import webdoc.community.repository.PostRepository;
import webdoc.community.repository.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityService {
    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;

    private final PostRepository postRepository;

    public List<CommunityResponse> getAllCommunities(){
        return
                communityRepository.findAll()
                        .stream()
                        .map(s->new CommunityResponse(s.getName(),s.getId()))
                        .collect(Collectors.toList());
    }

    @Transactional
    public Post createPost(PostCreateRequest request, Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new NoSuchElementException("비정상적인 접근입니다"));

        Community community = communityRepository.findById(request.getCommunityId())
                .orElseThrow(()-> new NoSuchElementException("비정상적인 접근입니다"));


        Post post = Post.CreatePost(user,request.getText(),community);
        request.getAddresses().stream()
                .forEach(e->{
                    if(!StringUtils.hasText(e.getAddress()) || e.getPriority() == null){
                        throw new IllegalArgumentException("바인딩에 실패하였습니다");
                    }
                    Picture picture = Picture.createPicture(e.getAddress(),e.getPriority());
                    post.addPictures(picture);
                });

        return postRepository.save(post);




    }




}
