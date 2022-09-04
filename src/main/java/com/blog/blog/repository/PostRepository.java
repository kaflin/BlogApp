package com.blog.blog.repository;

import com.blog.blog.Model.Post;
import com.blog.blog.Model.Subreddit;
import com.blog.blog.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends  JpaRepository<Post,Long> {

    List<Post> findPostBySubreddit(Subreddit subreddit);

    List<Post> findAllBySubreddit(Subreddit subreddit);

    List<Post> findByUser(User user);

    Optional<Post> findByPostName(String postName);
    Optional<Post> findById(Long id);

    Optional<Post> deleteByPostId(Long id);
}
