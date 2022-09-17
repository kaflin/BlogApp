package com.blog.blog.controller;

import com.blog.blog.Model.ResponseModel;
import com.blog.blog.dto.VoteDto;
import com.blog.blog.service.VoteService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/votes")
@AllArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @PostMapping("/save")
    public ResponseEntity<?> createVote(@RequestBody VoteDto voteDto)
    {
      voteService.saveVote(voteDto);
        return new ResponseEntity<>(new ResponseModel("VOTE have been Updated Successfully"), HttpStatus.CREATED);
    }
}
