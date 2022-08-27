package com.blog.blog.mapper;

import com.blog.blog.Model.Comment;
import com.blog.blog.Model.Post;
import com.blog.blog.Model.User;
import com.blog.blog.dto.CommentsDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(target="id",ignore=true)
    @Mapping(target="text",source="commentsDto.text")
    @Mapping(target="post",source="post")
    @Mapping(target="createdDate",expression = "java(java.time.Instant.now())")
    @Mapping(target="user",source="currentUser")
    Comment map(CommentsDto commentsDto, Post post, User currentUser);

    @Mapping(target="postId",expression="java(comment.getPost().getPostId())")
    @Mapping(target="postName",expression="java(comment.getPost().getPostName())")
    @Mapping(target="userName",expression="java(comment.getUser().getUsername())")
    @Mapping(target="userId",expression="java(comment.getUser().getId())")
    CommentsDto mapToDto(Comment comment);


}
