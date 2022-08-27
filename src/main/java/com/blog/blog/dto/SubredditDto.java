package com.blog.blog.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SubredditDto {

    private Long id;
    private String name;
    private String description;
    private Integer numberOfPosts;


}
