package com.blog.blog.repository;

import com.blog.blog.Model.Subreddit;
import com.blog.blog.dto.SubredditDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubredditRepository extends JpaRepository<Subreddit,Long> {

    Optional<Subreddit> findByName(String subredditName);

    Optional<Subreddit> deleteSubredditById(Long id);
}
