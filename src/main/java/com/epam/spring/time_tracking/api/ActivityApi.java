package com.epam.spring.time_tracking.api;

import com.epam.spring.time_tracking.dto.activity.ActivityDto;
import com.epam.spring.time_tracking.dto.user.UserDto;
import com.epam.spring.time_tracking.dto.user.UserInActivityDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "Activity management API")
@RequestMapping("/api/v1/activity")
public interface ActivityApi {

    @ApiOperation("Get all activities")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    List<ActivityDto> getActivities();

    @ApiImplicitParams({
            @ApiImplicitParam(name = "activityId", paramType = "path", required = true, value = "Activity id")
    })
    @ApiOperation("Get activity")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{activityId}")
    ActivityDto getActivity(@PathVariable int activityId);

    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", paramType = "path", required = true, value = "User id")
    })
    @ApiOperation("Get activities for regular user")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/user/{userId}")
    List<ActivityDto> getActivitiesForUser(@PathVariable int userId);

    @ApiOperation("Create activity")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    ActivityDto createActivity(@RequestBody @Valid ActivityDto activityDto);

    @ApiImplicitParams({
            @ApiImplicitParam(name = "activityId", paramType = "path", required = true, value = "Activity id")
    })
    @ApiOperation("Get activity users")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{activityId}/user")
    List<UserInActivityDto> getActivityUsers(@PathVariable int activityId);

    @ApiImplicitParams({
            @ApiImplicitParam(name = "activityId", paramType = "path", required = true, value = "Activity id")
    })
    @ApiOperation("Get users, who are not in activity")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{activityId}/user/available")
    List<UserDto> getUsersNotInActivity(@PathVariable int activityId);

    @ApiImplicitParams({
            @ApiImplicitParam(name = "activityId", paramType = "path", required = true, value = "Activity id"),
            @ApiImplicitParam(name = "userId", paramType = "path", required = true, value = "User id")
    })
    @ApiOperation("Get user information in activity")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{activityId}/user/{userId}")
    UserInActivityDto getUserInActivity(@PathVariable int activityId, @PathVariable int userId);

    @ApiImplicitParams({
            @ApiImplicitParam(name = "activityId", paramType = "path", required = true, value = "Activity id"),
            @ApiImplicitParam(name = "userId", paramType = "path", required = true, value = "User id")
    })
    @ApiOperation("Add user to activity")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/{activityId}/user/{userId}")
    UserInActivityDto addUserToActivity(@PathVariable int activityId, @PathVariable int userId);

    @ApiImplicitParams({
            @ApiImplicitParam(name = "activityId", paramType = "path", required = true, value = "Activity id"),
            @ApiImplicitParam(name = "userId", paramType = "path", required = true, value = "User id")
    })
    @ApiOperation("Remove user from activity")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{activityId}/user/{userId}")
    ResponseEntity<Void> removeUserFromActivity(@PathVariable int activityId, @PathVariable int userId);

    @ApiImplicitParams({
            @ApiImplicitParam(name = "activityId", paramType = "path", required = true, value = "Activity id"),
            @ApiImplicitParam(name = "userId", paramType = "path", required = true, value = "User id")
    })
    @ApiOperation("Start activity")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{activityId}/user/{userId}/start")
    UserInActivityDto startActivity(@PathVariable int activityId, @PathVariable int userId);

    @ApiImplicitParams({
            @ApiImplicitParam(name = "activityId", paramType = "path", required = true, value = "Activity id"),
            @ApiImplicitParam(name = "userId", paramType = "path", required = true, value = "User id")
    })
    @ApiOperation("Stop activity")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{activityId}/user/{userId}/stop")
    UserInActivityDto stopActivity(@PathVariable int activityId, @PathVariable int userId);

    @ApiImplicitParams({
            @ApiImplicitParam(name = "activityId", paramType = "path", required = true, value = "Activity id")
    })
    @ApiOperation("Update activity")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{activityId}")
    ActivityDto updateActivity(@PathVariable int activityId, @RequestBody @Valid ActivityDto activityDto);

    @ApiImplicitParams({
            @ApiImplicitParam(name = "activityId", paramType = "path", required = true, value = "Activity id")
    })
    @ApiOperation("Delete activity")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{activityId}")
    ResponseEntity<Void> deleteActivity(@PathVariable int activityId);

}
