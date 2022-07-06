package com.epam.spring.time_tracking.api;

import com.epam.spring.time_tracking.dto.activity.ActivityDto;
import com.epam.spring.time_tracking.dto.activity.ActivityForUserProfileDto;
import com.epam.spring.time_tracking.dto.group.OnAuthorization;
import com.epam.spring.time_tracking.dto.group.OnCreate;
import com.epam.spring.time_tracking.dto.group.OnUpdate;
import com.epam.spring.time_tracking.dto.group.OnUpdatePassword;
import com.epam.spring.time_tracking.dto.user.UserDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "User management API")
@RequestMapping("/api/v1/user")
public interface UserApi {

    @ApiOperation("Get all users")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<UserDto> getUsers();

    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", paramType = "path", required = true, value = "User id")
    })
    @ApiOperation("Get user")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable int userId);

    @ApiOperation("Register user")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    public UserDto createUser(@RequestBody @Validated(OnCreate.class) UserDto userDto);

    @ApiOperation("Authorize user")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/auth")
    public UserDto authUser(@RequestBody @Validated(OnAuthorization.class) UserDto userDto);

    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", paramType = "path", required = true, value = "User id")
    })
    @ApiOperation("Get user's activities for profile")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{userId}/activity")
    public List<ActivityForUserProfileDto> getUserActivitiesForProfile(@PathVariable int userId);

    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", paramType = "path", required = true, value = "User id")
    })
    @ApiOperation("Get admin's activities for profile")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/admin/{userId}/activity")
    public List<ActivityDto> getAdminActivitiesForProfile(@PathVariable int userId);

    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", paramType = "path", required = true, value = "User id"),
            @ApiImplicitParam(name = "isBlocked", paramType = "path", required = true, value = "Block value")
    })
    @ApiOperation("Block/unblock user")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{userId}/block/{isBlocked}")
    public UserDto blockUser(@PathVariable int userId, @PathVariable boolean isBlocked);

    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", paramType = "path", required = true, value = "User id")
    })
    @ApiOperation("Update user's information")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{userId}/info")
    public UserDto updateUserInfo(@PathVariable int userId, @RequestBody @Validated(OnUpdate.class) UserDto userDto);

    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", paramType = "path", required = true, value = "User id")
    })
    @ApiOperation("Update user's password")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{userId}/password")
    public UserDto updateUserPassword(@PathVariable int userId, @RequestBody @Validated(OnUpdatePassword.class) UserDto userDto);

    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", paramType = "path", required = true, value = "User id")
    })
    @ApiOperation("Delete user")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable int userId);

}
