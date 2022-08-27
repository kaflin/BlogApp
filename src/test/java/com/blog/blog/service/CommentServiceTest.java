package com.blog.blog.service;

import com.blog.blog.exceptions.SpringRedditException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class CommentServiceTest {

//    @Test
//    @DisplayName("Test Should pass When Comment do not Contains Swear Words")
//    void shouldNotContainsSwearWordsInsideComments() {
//        CommentService commentService=new CommentService(null,null,null,null,null,null,null);
//        assertThat(commentService.containsSwearWords("This is a comment")).isFalse();
//    }

    @Test
    @DisplayName("Test Should pass When Comment do not Contains Swear Words")
    void shouldNotContainSwearWordsInsideComments() {
        CommentService commentService=new CommentService(null,null,null,null,null,null,null);
        assertThatThrownBy(()->{
            commentService.containsSwearWords("This is a shitty comment");
        }).isInstanceOf(SpringRedditException.class).hasMessage("Comments contains unacceptable language");
    }

}
