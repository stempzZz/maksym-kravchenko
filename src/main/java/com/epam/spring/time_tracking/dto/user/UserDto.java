package com.epam.spring.time_tracking.dto.user;

import com.epam.spring.time_tracking.dto.group.OnAuthorization;
import com.epam.spring.time_tracking.dto.group.OnCreate;
import com.epam.spring.time_tracking.dto.group.OnUpdate;
import com.epam.spring.time_tracking.dto.group.OnUpdatePassword;
import com.epam.spring.time_tracking.dto.validation.Password;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int id;

    @NotBlank(message = "'lastName' shouldn't be empty", groups = {OnCreate.class, OnUpdate.class})
    @Null(message = "'lastName' should be absent in request", groups = {OnUpdatePassword.class, OnAuthorization.class})
    private String lastName;

    @NotBlank(message = "'firstName' shouldn't be empty", groups = {OnCreate.class, OnUpdate.class})
    @Null(message = "'firstName' should be absent in request", groups = {OnUpdatePassword.class, OnAuthorization.class})
    private String firstName;

    @Email
    @NotBlank(message = "'email' shouldn't be empty", groups = {OnCreate.class, OnUpdate.class, OnAuthorization.class})
    @Null(message = "'email' should be absent in request", groups = OnUpdatePassword.class)
    private String email;

    @NotBlank(message = "'currentPassword' shouldn't be empty", groups = OnUpdatePassword.class)
    @Null(message = "'currentPassword' should be absent in request", groups = {OnCreate.class, OnUpdate.class, OnAuthorization.class})
    private String currentPassword;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Password(groups = {OnCreate.class, OnUpdatePassword.class})
    @NotBlank(message = "'password' shouldn't be empty", groups = {OnCreate.class, OnUpdatePassword.class, OnAuthorization.class})
    @Null(message = "'password' should be absent in request", groups = OnUpdate.class)
    private String password;

    @NotBlank(message = "'repeatPassword' shouldn't be empty", groups = {OnCreate.class, OnUpdatePassword.class})
    @Null(message = "'repeatPassword' should be absent in request", groups = {OnUpdate.class, OnAuthorization.class})
    private String repeatPassword;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int activityCount;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private double spentTime;

    private boolean admin;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private boolean blocked;
}
