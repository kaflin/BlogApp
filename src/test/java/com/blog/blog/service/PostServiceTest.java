package com.blog.blog.service;

import com.blog.blog.Model.Post;
import com.blog.blog.dto.PostResponse;
import com.blog.blog.mapper.PostMapper;
import com.blog.blog.repository.PostRepository;
import com.blog.blog.repository.SubredditRepository;
import com.blog.blog.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testng.asserts.Assertion;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.testng.asserts.Assertion.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {


    @Mock
    private PostRepository postRepository;
    @Mock
    private SubredditRepository subredditRepository;
    @Mock
    private AuthService authService;
    @Mock
    private PostMapper postMapper;
    @Mock
    private UserRepository userRepository;


    @Test
    @DisplayName("Should Find Post By id")
    void getPostById() {
        PostService postService =new PostService(postRepository,subredditRepository,postMapper,authService,userRepository);
        Post post =new Post(123L,"First Post","http://url.site","Test",0,null, Instant.now(),null);
        PostResponse expectedPostResponse =new PostResponse(123L,"First Post","http://url.site","Test","Test User","Test Subreddit",0,"1hour ago",0,false,false);


        Mockito.when(postRepository.findById(123L)).thenReturn(Optional.of(post));
        Mockito.when(postMapper.mapToDto(Mockito.any(Post.class))).thenReturn(expectedPostResponse);

        PostResponse actualResponse =postService.getPostById(123L);

        assertThat(actualResponse.getId()).isEqualTo(expectedPostResponse.getId());
        assertThat(actualResponse.getPostName()).isEqualTo(expectedPostResponse.getPostName());

    }

}
