package com.blog.blog.service;

import com.blog.blog.Model.Post;
import com.blog.blog.Model.Subreddit;
import com.blog.blog.Model.User;
import com.blog.blog.dto.PostRequest;
import com.blog.blog.dto.SubredditDto;
import com.blog.blog.exceptions.SpringRedditException;
import com.blog.blog.mapper.SubredditMapper;
import com.blog.blog.repository.SubredditRepository;
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
public class SubredditService {


    private final SubredditRepository subredditRepository;
    private final SubredditMapper subredditMapper;

    public SubredditService(SubredditRepository subredditRepository, SubredditMapper subredditMapper) {
        this.subredditRepository = subredditRepository;
        this.subredditMapper = subredditMapper;
    }


    @Transactional
    public SubredditDto save(SubredditDto subredditDto){
            Subreddit subreddit = subredditRepository.save(subredditMapper.mapDtoToSubreddit(subredditDto));
            subredditDto.setId(subreddit.getId());
            return subredditDto;
    }

    public void update(@NotNull SubredditDto subredditDto, Long id) {
        Subreddit subreddit = new Subreddit();
        subreddit.setId(id);
        subreddit.setCreatedDate(Instant.now());
        subreddit.setDescription(subredditDto.getDescription());
        subreddit.setName(subredditDto.getName());
        subredditRepository.save(subreddit);
    }

    @Transactional
    public List<SubredditDto> getAll() {
        return subredditRepository.findAll()
                .stream()
                .map(subredditMapper::mapSubredditToDto)
                .collect(Collectors.toList());
    }

    public SubredditDto getSubredditById(Long id) {
        Subreddit subreddit = subredditRepository.findById(id)
                .orElseThrow(() -> new SpringRedditException("No SubReddit found with Id-" + id));
        return subredditMapper.mapSubredditToDto(subreddit);
    }
}


