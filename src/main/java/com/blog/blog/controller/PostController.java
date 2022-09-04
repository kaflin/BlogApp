package com.blog.blog.controller;


import com.blog.blog.Model.Post;
import com.blog.blog.Model.ResponseModel;
import com.blog.blog.dto.PostRequest;
import com.blog.blog.dto.PostResponse;
import com.blog.blog.repository.PostRepository;
import com.blog.blog.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
@Slf4j
public class PostController {

    private final PostService postService;
    private final PostRepository postRepository;

    public PostController(PostService postService,PostRepository postRepository) {
        this.postService = postService;
        this.postRepository=postRepository;
    }

    @PostMapping
    public ResponseEntity<?> createPosts(@RequestBody PostRequest postRequest) {
        Optional<Post> postName = postRepository.findByPostName(postRequest.getPostName());
        if (!postName.isPresent()) {
            postService.save(postRequest);
        } else {
            return new ResponseEntity<>(new ResponseModel("PostName already exists"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(new ResponseModel("Post have been created"), HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updatePost(@RequestBody PostRequest postRequest, @PathVariable Long id) {
        Optional<Post> post = postRepository.findById(id);
        if (post.isPresent()) {
            if(!post.get().getPostName().contains(postRequest.getPostName())||!(post.get().getDescription().contains(postRequest.getDescription()))||!(post.get().getUrl().contains(postRequest.getUrl()))) {
                postService.update(postRequest, id);
            }else{
                return new ResponseEntity<>(new ResponseModel("Post's postName ,Description and url is same as old value"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(new ResponseModel("Post with id-" + id + "-doesnot exists"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(new ResponseModel("Post have been Updated Successfully"), HttpStatus.OK);
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getPost(@PathVariable Long id)
    {
        Optional<Post> post = postRepository.findById(id);
        if (post.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(postService.getPostById(id));
        } else {
            return new ResponseEntity<>(new ResponseModel("Post with id-" + id + "-doesnot exists"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPost()
    {
        return ResponseEntity.status(HttpStatus.OK).body(postService.getAllPost());
    }
    @GetMapping("/postBySubreddit/{id}")
    public ResponseEntity<List<PostResponse>> getPostBySubreddit(@PathVariable Long id)
    {
        return ResponseEntity.status(HttpStatus.OK).body(postService.getPostBySubreddit(id));
    }

    @GetMapping("by-user/{name}")
    public ResponseEntity<List<PostResponse>> getPostsByUsername(@PathVariable String name) {
        return  ResponseEntity.status(HttpStatus.OK).body(postService.getPostsByUsername(name));
    }

    @DeleteMapping("/deletePost/{id}")
    public ResponseEntity deletePost(@PathVariable Long id) {
        postService.deleteByPostId(id);
        return new ResponseEntity(new ResponseModel("Post have been deleted Successfully !!"), HttpStatus.OK );
    }

}
