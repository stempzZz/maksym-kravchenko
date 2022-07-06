package com.epam.spring.time_tracking.dto.activity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ActivityInputDto {

    private int id;
    private String name;
    private List<Integer> categoryIds;
    private String description;
    private String image;
    private int peopleCount;
    private int creatorId;
    private LocalDateTime createTime;

}
