package com.blog.blog.controller;

import com.blog.blog.Model.Comment;
import com.blog.blog.Model.Post;
import com.blog.blog.Model.ResponseModel;
import com.blog.blog.Model.Subreddit;
import com.blog.blog.dto.CommentsDto;
import com.blog.blog.repository.CommentRepository;
import com.blog.blog.repository.PostRepository;
import com.blog.blog.service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/comments")
public class CommentsController {

    private final CommentService commentService;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public CommentsController(CommentService commentService,CommentRepository commentRepository,PostRepository postRepository) {
        this.commentService = commentService;
        this.commentRepository=commentRepository;
        this.postRepository=postRepository;
    }

    @PostMapping("/save")
    public ResponseEntity<?> createComment(@RequestBody CommentsDto commentsDto) {
        if (commentsDto.getPostId() != null) {
            commentService.save(commentsDto);
            return new ResponseEntity<>(new ResponseModel("Comments have been added Successfully!"), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(new ResponseModel("Comments cannot be added"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateComment(@RequestBody CommentsDto commentsDto, @PathVariable Long id) {
        Optional<Comment> comment = commentRepository.findById(id);
        Optional<Post> post = postRepository.findById(commentsDto.getPostId());
        if (comment.isPresent()) {
            if (post.isPresent()) {
                if (!comment.get().getText().contains(commentsDto.getText())) {
                    commentService.update(commentsDto, id);
                } else {
                    return new ResponseEntity<>(new ResponseModel("Comments text is same as old value"), HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                return new ResponseEntity<>(new ResponseModel("PostId :" + commentsDto.getPostId() + " doesnot exists"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity<>(new ResponseModel("Comments Updated Successfully!!"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ResponseModel("Comment with id-" + id + "-doesnot exists"), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @GetMapping("/by-post/{postId}")
    public ResponseEntity<List<CommentsDto>> getAllCommentsForPost(@PathVariable Long postId)
    {
       return ResponseEntity.status(HttpStatus.OK)
                .body(commentService.getAllCommentsForPost(postId));
    }

    @GetMapping("/by-user/{userName}")
    public ResponseEntity<List<CommentsDto>> getAllCommentsForUser(@PathVariable String userName){
        return ResponseEntity.status(HttpStatus.OK)
                .body(commentService.getAllCommentsForUser(userName));
    }

}
