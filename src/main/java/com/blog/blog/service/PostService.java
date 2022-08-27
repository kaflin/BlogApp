package com.blog.blog.service;


import com.blog.blog.Model.Post;
import com.blog.blog.Model.Subreddit;
import com.blog.blog.Model.User;
import com.blog.blog.dto.PostRequest;
import com.blog.blog.dto.PostResponse;
import com.blog.blog.exceptions.PostNotFoundException;
import com.blog.blog.exceptions.SubredditNotFoundException;
import com.blog.blog.exceptions.UsernameNotFoundException;
import com.blog.blog.mapper.PostMapper;
import com.blog.blog.repository.PostRepository;
import com.blog.blog.repository.SubredditRepository;
import com.blog.blog.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final SubredditRepository subredditRepository;
    private final PostMapper postMapper;
    private final AuthService authService;
    private final UserRepository userRepository;

    public PostService(PostRepository postRepository, SubredditRepository subredditRepository, PostMapper postMapper, AuthService authService, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.subredditRepository = subredditRepository;
        this.postMapper = postMapper;
        this.authService = authService;
        this.userRepository = userRepository;
    }

    public void save(PostRequest postRequest) {
        Subreddit subreddit = subredditRepository.findByName(postRequest.getSubredditName())
                .orElseThrow(() -> new SubredditNotFoundException(postRequest.getSubredditName()));
        User currentUser = authService.getCurrentUser();
        postRepository.save(postMapper.map(postRequest, subreddit, currentUser));
    }

    @Transactional
    public PostResponse getPostById(Long id) {
        Post post=postRepository.findById(id)
                .orElseThrow(()->new PostNotFoundException(id.toString()));
        return postMapper.mapToDto(post);

    }


    public List<PostResponse> getAllPost() {
        return postRepository.findAll()
                .stream()
                .map(postMapper::mapToDto)
                .collect(Collectors.toList());

    }

    public List<PostResponse> getPostBySubreddit(Long id) {
        Subreddit subreddit=subredditRepository.findById(id)
                .orElseThrow(()->new SubredditNotFoundException(id.toString()));
        List<Post> posts =postRepository.findPostBySubreddit(subreddit);
        return posts.stream().map(postMapper::mapToDto).collect(Collectors.toList());
    }

    public void update(@NotNull PostRequest postRequest, Long id) {
        Post post=new Post();
        post.setPostId(id);
        post.setCreatedDate(Instant.now());
        post.setPostName(postRequest.getPostName());
        post.setUrl(postRequest.getUrl());

        Optional<Subreddit> subreddit=subredditRepository.findByName(postRequest.getSubredditName());
        post.setSubreddit(subreddit.get());

        User currentUser = authService.getCurrentUser();//To set userId
        post.setUser(currentUser);

        post.setDescription(postRequest.getDescription());
        postRepository.save(post);

    }

//    @Transactional(readOnly = true)
    public List<PostResponse> getPostsBySubreddit(Long subredditId) {
        Subreddit subreddit = subredditRepository.findById(subredditId)
                .orElseThrow(() -> new SubredditNotFoundException(subredditId.toString()));
        List<Post> posts = postRepository.findAllBySubreddit(subreddit);
        return posts.stream().map(postMapper::mapToDto).collect(Collectors.toList());
    }



//    @Transactional(readOnly = true)
    public List<PostResponse> getPostsByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        return postRepository.findByUser(user)
                .stream()
                .map(postMapper::mapToDto)
                .collect(Collectors.toList());
    }
}
