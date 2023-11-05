package webdoc.community.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import webdoc.community.domain.entity.community.Community;
import webdoc.community.domain.entity.like.Like;
import webdoc.community.domain.entity.post.Post;
import webdoc.community.domain.entity.post.request.PostCreateRequest;
import webdoc.community.domain.entity.user.User;
import webdoc.community.domain.entity.user.patient.Patient;
import webdoc.community.service.CommunityService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
@ActiveProfiles("test")
class PostRepositoryTest {


}