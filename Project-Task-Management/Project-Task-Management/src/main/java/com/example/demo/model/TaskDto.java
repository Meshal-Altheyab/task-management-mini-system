package com.example.demo.model;

import lombok.Data;

@Data
public class TaskDto {

    private String title;
    private String description;
    private TaskStatus status;
}
