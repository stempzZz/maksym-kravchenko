package com.epam.spring.time_tracking.api;

import com.epam.spring.time_tracking.dto.activity.ActivityDto;
import com.epam.spring.time_tracking.dto.group.OnCreate;
import com.epam.spring.time_tracking.dto.request.RequestDto;
import com.epam.spring.time_tracking.dto.request.RequestForListDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "Request management API")
@RequestMapping("/api/v1/request")
public interface RequestApi {

    @ApiOperation("Get all requests")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    List<RequestForListDto> getRequests(@PageableDefault(sort = "createTime", direction = Direction.DESC) Pageable pageable);

    @ApiImplicitParams({
            @ApiImplicitParam(name = "requestId", paramType = "path", required = true, value = "Request id")
    })
    @ApiOperation("Get request")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{requestId}")
    RequestDto getRequest(@PathVariable Long requestId);

    @ApiOperation("Create request for adding activity")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/activity/add")
    RequestDto createRequestToAdd(@RequestBody @Validated(OnCreate.class) ActivityDto activityDto);

    @ApiImplicitParams({
            @ApiImplicitParam(name = "activityId", paramType = "path", required = true, value = "Activity id")
    })
    @ApiOperation("Create request for removing activity")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/activity/{activityId}/remove")
    RequestDto createRequestToRemove(@PathVariable Long activityId);

    @ApiImplicitParams({
            @ApiImplicitParam(name = "requestId", paramType = "path", required = true, value = "Request id")
    })
    @ApiOperation("Confirm request")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/{requestId}/confirm")
    RequestDto confirmRequest(@PathVariable Long requestId);

    @ApiImplicitParams({
            @ApiImplicitParam(name = "requestId", paramType = "path", required = true, value = "Request id")
    })
    @ApiOperation("Decline request")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/{requestId}/decline")
    RequestDto declineRequest(@PathVariable Long requestId);

    @ApiImplicitParams({
            @ApiImplicitParam(name = "requestId", paramType = "path", required = true, value = "Request id")
    })
    @ApiOperation("Delete request")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{requestId}")
    ResponseEntity<Void> deleteRequest(@PathVariable Long requestId);

}
