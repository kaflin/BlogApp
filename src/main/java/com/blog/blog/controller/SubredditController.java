package com.blog.blog.controller;
import com.blog.blog.Model.ResponseModel;
import com.blog.blog.Model.Subreddit;
import com.blog.blog.dto.SubredditDto;
import com.blog.blog.repository.SubredditRepository;
import com.blog.blog.service.SubredditService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/subreddit")
@AllArgsConstructor
@Slf4j
public class SubredditController {

    private final SubredditService subredditService;
    private final SubredditRepository subredditRepository;


    @PostMapping("/save")
    public ResponseEntity<?> createSubreddit(@RequestBody SubredditDto subredditDto) {
        if (!subredditDto.getName().isEmpty() && !subredditDto.getDescription().isEmpty()) {
            Optional<Subreddit> name = subredditRepository.findByName(subredditDto.getName());
            if (!(name.isPresent())) {
                return ResponseEntity.status(HttpStatus.CREATED).body(subredditService.save(subredditDto));
            } else {
                return new ResponseEntity<>(new ResponseModel("Duplicate Name is not allowed"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(new ResponseModel("Name and Description cannot be empty"), HttpStatus.NOT_FOUND);

        }
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateSubreddit(@RequestBody SubredditDto subredditDto, @PathVariable Long id) {
        Optional<Subreddit> subreddit = subredditRepository.findById(id);
        if (subreddit.isPresent()) {
            if(!subreddit.get().getName().contains(subredditDto.getName())||!(subreddit.get().getDescription().contains(subredditDto.getDescription()))) {
                subredditService.update(subredditDto, id);
            }else{
                return new ResponseEntity<>(new ResponseModel("Subreddit name and description is same as old value"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(new ResponseModel("Subreddit with id-" + id + "-doesnot exists"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(new ResponseModel("Subreddit have been Updated Successfully"), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<SubredditDto>> getAllSubreddits() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(subredditService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubredditDto> getSubredditById(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(subredditService.getSubredditById(id));
    }
    @DeleteMapping("/deleteSubReddit/{id}")
    public ResponseEntity deleteSubReddit(@PathVariable Long id) {
        subredditService.deleteBySubRedditId(id);
        return new ResponseEntity(new ResponseModel("SubReddit have been deleted Successfully !!"), HttpStatus.OK );
    }
}
