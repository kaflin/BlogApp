package com.blog.blog.service;
import com.blog.blog.Model.*;
import com.blog.blog.dto.CommentsDto;
import com.blog.blog.exceptions.PostNotFoundException;
import com.blog.blog.exceptions.SpringRedditException;
import com.blog.blog.exceptions.UsernameNotFoundException;
import com.blog.blog.mapper.CommentMapper;
import com.blog.blog.repository.CommentRepository;
import com.blog.blog.repository.PostRepository;
import com.blog.blog.repository.UserRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private static final String POST_URL = "";
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final MailContentBuilder mailContentBuilder;
    private final MailService mailService;

    public CommentService(PostRepository postRepository, UserRepository userRepository, AuthService authService, CommentMapper commentMapper, CommentRepository commentRepository, MailContentBuilder mailContentBuilder, MailService mailService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.authService = authService;
        this.commentMapper = commentMapper;
        this.commentRepository = commentRepository;
        this.mailContentBuilder = mailContentBuilder;
        this.mailService = mailService;
    }

    public void save(CommentsDto commentsDto) {
        Post post = postRepository.findById(commentsDto.getPostId())
                .orElseThrow(() -> new PostNotFoundException("Post Not found of id -" + commentsDto.getPostId().toString()));
        Comment comment = commentMapper.map(commentsDto, post, authService.getCurrentUser());
        commentRepository.save(comment);

        String message = mailContentBuilder.build(post.getUser().getUsername() + " posted a comment on your post." + POST_URL);
        sendCommentNotification(message, post.getUser());

    }
    public void update(@NotNull CommentsDto commentsDto, Long id) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setCreatedDate(Instant.now());
        comment.setText(commentsDto.getText());

        Optional<Post> post=postRepository.findById(commentsDto.getPostId());//To set postId
        comment.setPost(post.get());

        User currentUser = authService.getCurrentUser();//To set userId
        comment.setUser(currentUser);
        commentRepository.save(comment);

    }

    private void sendCommentNotification(String message, User user) {
        mailService.sendMail(new NotificationEmail(user.getUsername() + " Commented on your post", user.getEmail(), message));
    }

    public List<CommentsDto> getAllCommentsForPost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId.toString()));
        return commentRepository.findByPost(post)
                .stream()
                .map(commentMapper::mapToDto)
                .collect(Collectors.toList());
    }

    public List<CommentsDto> getAllCommentsForUser(String userName) {

        User user=userRepository.findByUsername(userName)
                .orElseThrow(()->new UsernameNotFoundException(userName));
        return commentRepository.findAllByUser(user)
                .stream()
                .map(commentMapper::mapToDto)
                .collect(Collectors.toList());

    }
    public boolean containsSwearWords(String comment)
    {
        if(comment.contains("shit"))
        {
            throw new SpringRedditException("Comments contains unacceptable language");
        }
        return true;
    }
}

