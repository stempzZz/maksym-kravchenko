package com.epam.spring.time_tracking.test.util;

import com.epam.spring.time_tracking.dto.user.UserDto;
import com.epam.spring.time_tracking.model.User;
import com.epam.spring.time_tracking.test.dto.TestUserDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserDataUtilTest {

    public static final Long ADMIN_ID = 1L;
    public static final String ADMIN_LAST_NAME = "Adminov";
    public static final String ADMIN_FIRST_NAME = "Adam";
    public static final String ADMIN_EMAIL = "admin@qwer.com";

    public static final Long USER_1_ID = 2L;
    public static final String USER_1_LAST_NAME = "Smith";
    public static final String USER_1_FIRST_NAME = "John";
    public static final String USER_1_EMAIL = "smith@qwer.com";

    public static final Long USER_2_ID = 3L;
    public static final String USER_2_LAST_NAME = "Wayne";
    public static final String USER_2_FIRST_NAME = "Bruce";
    public static final String USER_2_EMAIL = "wayne@qwer.com";

    public static final String UPDATE_LAST_NAME = "Stark";
    public static final String UPDATE_FIRST_NAME = "Tony";
    public static final String UPDATE_EMAIL = "stark@qwer.com";

    private static final String PASSWORD = "qwerty123";
    private static final String UPDATE_PASSWORD = "qwerty3123";

    public static User getAdmin() {
        User admin = new User();
        admin.setId(ADMIN_ID);
        admin.setLastName(ADMIN_LAST_NAME);
        admin.setFirstName(ADMIN_FIRST_NAME);
        admin.setEmail(ADMIN_EMAIL);
        admin.setPassword(PASSWORD);
        admin.setAdmin(true);
        return admin;
    }

    public static User getUser1(int activityCount, double spentTime) {
        User user = new User();
        user.setId(USER_1_ID);
        user.setLastName(USER_1_LAST_NAME);
        user.setFirstName(USER_1_FIRST_NAME);
        user.setEmail(USER_1_EMAIL);
        user.setPassword(PASSWORD);
        user.setActivityCount(activityCount);
        user.setSpentTime(spentTime);
        return user;
    }

    public static User getUser2(int activityCount, double spentTime) {
        User user = new User();
        user.setId(USER_2_ID);
        user.setLastName(USER_2_LAST_NAME);
        user.setFirstName(USER_2_FIRST_NAME);
        user.setEmail(USER_2_EMAIL);
        user.setPassword(PASSWORD);
        user.setActivityCount(activityCount);
        user.setSpentTime(spentTime);
        return user;
    }

    public static TestUserDto getUser1DtoForInputData() {
        TestUserDto userDto = new TestUserDto();
        userDto.setLastName(USER_1_LAST_NAME);
        userDto.setFirstName(USER_1_FIRST_NAME);
        userDto.setEmail(USER_1_EMAIL);
        userDto.setPassword(PASSWORD);
        userDto.setRepeatPassword(PASSWORD);
        return userDto;
    }

    public static TestUserDto getUser1DtoForAuthorization() {
        TestUserDto userDto = new TestUserDto();
        userDto.setEmail(USER_1_EMAIL);
        userDto.setPassword(PASSWORD);
        return userDto;
    }

    public static TestUserDto getUser1WithUpdatedInformation() {
        TestUserDto userDto = new TestUserDto();
        userDto.setLastName(UPDATE_LAST_NAME);
        userDto.setFirstName(UPDATE_FIRST_NAME);
        userDto.setEmail(UPDATE_EMAIL);
        return userDto;
    }

    public static TestUserDto getUser1WithUpdatedPassword() {
        TestUserDto userDto = new TestUserDto();
        userDto.setCurrentPassword(PASSWORD);
        userDto.setPassword(UPDATE_PASSWORD);
        userDto.setRepeatPassword(UPDATE_PASSWORD);
        return userDto;
    }

}
