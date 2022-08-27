package com.blog.blog.mapper;


import com.blog.blog.Model.*;
import com.blog.blog.dto.PostRequest;
import com.blog.blog.dto.PostResponse;
import com.blog.blog.repository.CommentRepository;
import com.blog.blog.repository.VoteRepository;
import com.blog.blog.service.AuthService;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.Authorization;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
//import com.github.marlonlom.utilities.timeago.TimeAgo;

import java.sql.Time;
import java.util.Optional;

import static com.blog.blog.Model.VoteType.UPVOTE;

@Mapper(componentModel = "spring")
public abstract class  PostMapper {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private AuthService authService;


    @Mapping(target = "createdDate", expression = "java(java.time.Instant.now())")
    @Mapping(target = "description", source = "postRequest.description")
    @Mapping(target = "subreddit", source = "subreddit")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "voteCount",constant = "0")//WhenEver we are Saving the Post,The default  votecount should be zero
    public abstract Post map(PostRequest postRequest, Subreddit subreddit, User user);

    @Mapping(target="id",source="post.postId")
    @Mapping(target="postName",source="post.postName")
    @Mapping(target="description",source="post.description")
    @Mapping(target="url",source="post.url")
    @Mapping(target="subredditName",source="post.subreddit.name")
    @Mapping(target="userName",source="post.user.username")
    //Newly Added Field
    @Mapping(target="commentCount",expression = "java(commentCount(post))")
//    @Mapping(target="duration",expression = "java(getDuration(post))")
    @Mapping(target = "upVote", expression = "java(isPostUpVoted(post))")
    @Mapping(target = "downVote", expression = "java(isPostDownVoted(post))")
   public abstract PostResponse mapToDto(Post post);



    Integer commentCount(Post post)
    {
        return commentRepository.findByPost(post).size();
    }

//    String getDuration(Post post)
//    {
//        return TimeAgo.using(post.getCreatedDate().toEpochMilli());
//    }
boolean isPostUpVoted(Post post) {
    return checkVoteType(post, UPVOTE);
}

boolean isPostDownVoted(Post post) {
        return checkVoteType(post, VoteType.DOWNVOTE);
    }

private boolean checkVoteType(Post post, VoteType voteType) {
    if (authService.isLoggedIn()) {
        Optional<Vote> voteForPostByUser =
                voteRepository.findTopByPostAndUserOrderByVoteIdDesc(post,
                        authService.getCurrentUser());
        return voteForPostByUser.filter(vote -> vote.getVoteType().equals(voteType))
                .isPresent();
    }
    return false;
}

}
