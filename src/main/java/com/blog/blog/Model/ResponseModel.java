package com.blog.blog.Model;

import lombok.Data;

@Data //lombok
public class ResponseModel {

    String message;

    public ResponseModel(String message) {
        this.message = message;
    }
}