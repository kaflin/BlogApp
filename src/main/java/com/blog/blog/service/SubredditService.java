package com.blog.blog.service;
import com.blog.blog.Model.Post;
import com.blog.blog.Model.Subreddit;
import com.blog.blog.Model.User;
import com.blog.blog.dto.PostRequest;
import com.blog.blog.dto.SubredditDto;
import com.blog.blog.exceptions.SpringRedditException;
import com.blog.blog.exceptions.SubredditNotFoundException;
import com.blog.blog.mapper.SubredditMapper;
import com.blog.blog.repository.SubredditRepository;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final AuthService authService;

    public SubredditService(SubredditRepository subredditRepository, SubredditMapper subredditMapper,AuthService authService) {
        this.subredditRepository = subredditRepository;
        this.subredditMapper = subredditMapper;
        this.authService = authService;
    }


    @Transactional
    public SubredditDto save(SubredditDto subredditDto){
        User currentUser = authService.getCurrentUser();
        Subreddit subreddit = subredditRepository.save(subredditMapper.mapDtoToSubreddit(subredditDto,currentUser));
            subredditDto.setId(subreddit.getId());
            return subredditDto;
    }

    public void update(@NotNull SubredditDto subredditDto, Long id) {
        Subreddit subreddit = new Subreddit();
        subreddit.setId(id);
        subreddit.setCreatedDate(Instant.now());
        subreddit.setDescription(subredditDto.getDescription());
        User currentUser = authService.getCurrentUser();//To set userId
        subreddit.setUser(currentUser);
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
    @Transactional
    public Object deleteBySubRedditId(Long id) {
        Subreddit subreddit = subredditRepository.findById(id)
                .orElseThrow(() -> new SubredditNotFoundException(id.toString()));
        if(subreddit.getId()!=null) {
            return subredditRepository.deleteSubredditById(subreddit.getId());
        }else{
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}


