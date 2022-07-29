package com.epam.spring.time_tracking.test.util;

import com.epam.spring.time_tracking.dto.activity.ActivityDto;
import com.epam.spring.time_tracking.model.Activity;
import com.epam.spring.time_tracking.model.Category;
import com.epam.spring.time_tracking.model.User;
import com.epam.spring.time_tracking.model.enums.status.ActivityStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ActivityDataUtilTest {

    public static final Long ACTIVITY_1_ID = 1L;
    public static final String ACTIVITY_1_NAME = "Activity 1";

    public static final Long ACTIVITY_2_ID = 2L;
    public static final String ACTIVITY_2_NAME = "Activity 2";

    public static final Long ACTIVITY_3_ID = 2L;
    public static final String ACTIVITY_3_NAME = "Activity 2";

    public static final String ACTIVITY_DESCRIPTION = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer dignissim.";
    public static final String ACTIVITY_IMAGE = "image.png";
    public static final List<Category> ACTIVITY_CATEGORIES = List.of(CategoryDataUtilTest.getDefaultCategory());

    public static final String UPDATE_NAME = "Activity 121";
    public static final String UPDATE_DESCRIPTION = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas tempor.";
    public static final String UPDATE_IMAGE = "image2.jpg";

    public static Activity getActivity1(int peopleCount, ActivityStatus status, User creator) {
        Activity activity = new Activity();
        activity.setId(ACTIVITY_1_ID);
        activity.setName(ACTIVITY_1_NAME);
        activity.setDescription(ACTIVITY_DESCRIPTION);
        activity.setImage(ACTIVITY_IMAGE);
        activity.setPeopleCount(peopleCount);
        activity.setCreateTime(LocalDateTime.now());
        activity.setStatus(status);
        activity.setCreator(creator);
        activity.setCategories(ACTIVITY_CATEGORIES);
        return activity;
    }

    public static Activity getActivity2(int peopleCount, ActivityStatus status, User creator) {
        Activity activity = new Activity();
        activity.setId(ACTIVITY_2_ID);
        activity.setName(ACTIVITY_2_NAME);
        activity.setDescription(ACTIVITY_DESCRIPTION);
        activity.setImage(ACTIVITY_IMAGE);
        activity.setPeopleCount(peopleCount);
        activity.setCreateTime(LocalDateTime.now());
        activity.setStatus(status);
        activity.setCreator(creator);
        activity.setCategories(ACTIVITY_CATEGORIES);
        return activity;
    }

    public static Activity getActivity3(int peopleCount, ActivityStatus status, User creator) {
        Activity activity = new Activity();
        activity.setId(ACTIVITY_3_ID);
        activity.setName(ACTIVITY_3_NAME);
        activity.setDescription(ACTIVITY_DESCRIPTION);
        activity.setImage(ACTIVITY_IMAGE);
        activity.setPeopleCount(peopleCount);
        activity.setCreateTime(LocalDateTime.now());
        activity.setStatus(status);
        activity.setCreator(creator);
        activity.setCategories(ACTIVITY_CATEGORIES);
        return activity;
    }

    public static ActivityDto getActivity1DtoForInputData(User creator) {
        ActivityDto activityDto = new ActivityDto();
        activityDto.setName(ACTIVITY_1_NAME);
        activityDto.setDescription(ACTIVITY_DESCRIPTION);
        activityDto.setImage(ACTIVITY_IMAGE);
        activityDto.setCreatorId(creator.getId());
        return activityDto;
    }

    public static ActivityDto getUpdatedActivity1Dto(List<Long> categoryIds, int peopleCount) {
        ActivityDto activityDto = new ActivityDto();
        activityDto.setId(ACTIVITY_1_ID);
        activityDto.setName(UPDATE_NAME);
        activityDto.setCategoryIds(categoryIds);
        activityDto.setDescription(UPDATE_DESCRIPTION);
        activityDto.setImage(UPDATE_IMAGE);
        activityDto.setPeopleCount(peopleCount);
        return activityDto;
    }

}
