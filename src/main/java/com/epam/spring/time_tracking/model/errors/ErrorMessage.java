package com.epam.spring.time_tracking.model.errors;

public abstract class ErrorMessage {

    private ErrorMessage() {

    }

    public static final String ACTIVITY_NOT_FOUND = "Activity is not found!";
    public static final String CATEGORY_NOT_FOUND = "Category is not found!";
    public static final String REQUEST_NOT_FOUND = "Request is not found!";
    public static final String USER_NOT_FOUND = "User is not found!";

    public static final String CATEGORY_EXISTS_WITH_NAME_EN = "Category with entered name (EN) already exists!";
    public static final String CATEGORY_EXISTS_WITH_NAME_UA = "Category with entered name (UA) already exists!";
    public static final String USER_EXISTS_WITH_EMAIL = "User with entered email already exists!";
    public static final String USER_EXISTS_IN_ACTIVITY = "User is already exists in activity!";
    public static final String USER_DOES_NOT_EXIST_IN_ACTIVITY = "User does not exist in activity!";
    public static final String ADMIN_CAN_NOT_BE_IN_ACTIVITY = "Admin can not be in activity!";

    public static final String CREATOR_IS_NOT_AN_ADMIN = "Creator is not an admin!";
    public static final String CREATOR_IS_NOT_A_REGULAR_USER = "Creator is not a regular user!";
    public static final String INSTANTLY_ACTIVITY_CREATION = "For instantly activity creation, creator must be an admin!";
    public static final String ACTIVITY_IS_NOT_AVAILABLE = "Activity is not available or removed!";

    public static final String PASSWORD_CONFIRMATION_IS_FAILED = "Password confirmation is failed!";
    public static final String PASSWORD_VERIFICATION_IS_FAILED = "Password verification is failed!";

}
