package com.epam.spring.time_tracking.dto.activity;

import com.epam.spring.time_tracking.model.Activity;
import lombok.Data;

import java.util.Date;
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
    private Date createTime;
    private Activity.Status status;
}
