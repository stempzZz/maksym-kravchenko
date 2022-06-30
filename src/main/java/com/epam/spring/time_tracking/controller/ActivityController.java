package com.epam.spring.time_tracking.controller;

import com.epam.spring.time_tracking.dto.activity.ActivityDto;
import com.epam.spring.time_tracking.dto.activity.ActivityInputDto;
import com.epam.spring.time_tracking.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    @PostMapping("/activity")
    public ActivityDto createActivity(@RequestBody ActivityInputDto activityInputDto) {
        return activityService.createActivity(activityInputDto);
    }

    @GetMapping("/activity")
    public List<ActivityDto> getActivities() {
        return activityService.getActivities();
    }

    @PutMapping("/activity/{id}")
    public ActivityDto updateActivity(@PathVariable int id, @RequestBody ActivityInputDto activityInputDto) {
        return activityService.updateActivity(id, activityInputDto);
    }

    @DeleteMapping("/activity/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable int id) {
        activityService.deleteActivity(id);
        return ResponseEntity.noContent().build();
    }

}
