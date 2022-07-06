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

    @NotBlank(message = "{validation.not_blank.last_name}", groups = {OnCreate.class, OnUpdate.class})
    @Null(message = "{validation.null.last_name}", groups = {OnUpdatePassword.class, OnAuthorization.class})
    private String lastName;

    @NotBlank(message = "{validation.not_blank.first_name}", groups = {OnCreate.class, OnUpdate.class})
    @Null(message = "{validation.null.first_name}", groups = {OnUpdatePassword.class, OnAuthorization.class})
    private String firstName;

    @Email
    @NotBlank(message = "{validation.not_blank.email}", groups = {OnCreate.class, OnUpdate.class, OnAuthorization.class})
    @Null(message = "{validation.null.email}", groups = OnUpdatePassword.class)
    private String email;

    @NotBlank(message = "{validation.not_blank.current_password}", groups = OnUpdatePassword.class)
    @Null(message = "{validation.null.current_password}", groups = {OnCreate.class, OnUpdate.class, OnAuthorization.class})
    private String currentPassword;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Password(groups = {OnCreate.class, OnUpdatePassword.class})
    @NotBlank(message = "{validation.not_blank.password}", groups = {OnCreate.class, OnUpdatePassword.class, OnAuthorization.class})
    @Null(message = "{validation.null.password}", groups = OnUpdate.class)
    private String password;

    @NotBlank(message = "{validation.not_blank.repeat_password}", groups = {OnCreate.class, OnUpdatePassword.class})
    @Null(message = "{validation.null.repeat_password}", groups = {OnUpdate.class, OnAuthorization.class})
    private String repeatPassword;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int activityCount;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private double spentTime;

    private boolean admin;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private boolean blocked;

}
