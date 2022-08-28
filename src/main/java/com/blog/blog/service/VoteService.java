package com.blog.blog.service;

import com.blog.blog.Model.Post;
import com.blog.blog.Model.Vote;
import com.blog.blog.dto.VoteDto;
import com.blog.blog.exceptions.PostNotFoundException;
import com.blog.blog.exceptions.SpringRedditException;
import com.blog.blog.repository.PostRepository;
import com.blog.blog.repository.VoteRepository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.Optional;

import static com.blog.blog.Model.VoteType.UPVOTE;

@Service
public class VoteService {

    private final VoteRepository voteRepository;
    private final PostRepository postRepository;
    private final AuthService authService;

    public VoteService(VoteRepository voteRepository, PostRepository postRepository, AuthService authService) {
        this.voteRepository = voteRepository;
        this.postRepository = postRepository;
        this.authService = authService;
    }

    @Transactional
    public void saveVote(VoteDto voteDto) {
        Post post = postRepository.findById(voteDto.getPostId())//As findById return Optional.We are using orElseThrow to Throw PostNot FoundException
                .orElseThrow(() -> new PostNotFoundException("Post Not Found with ID -" + voteDto.getPostId()));
        Optional<Vote> voteByPostAndUser = voteRepository.findTopByPostAndUserOrderByVoteIdDesc(post, authService.getCurrentUser());

        if (voteByPostAndUser.isPresent()
                && voteByPostAndUser.get().getVoteType()
                .equals(voteDto.getVoteType())) {
            throw new SpringRedditException("You have already " +
                    voteDto.getVoteType() + "'d for this post");
        }
        if (UPVOTE.equals(voteDto.getVoteType())) {
            post.setVoteCount(post.getVoteCount() + 1);
        } else {
            post.setVoteCount(post.getVoteCount() - 1);
        }
        voteRepository.save(mapToVote(voteDto, post));//map VoteDto to Vote
        postRepository.save(post);

    }

    private Vote mapToVote(VoteDto voteDto, Post post) {
        return Vote.builder()
                .voteType(voteDto.getVoteType())
                .post(post)
                .user(authService.getCurrentUser())
                .build();

    }
}
