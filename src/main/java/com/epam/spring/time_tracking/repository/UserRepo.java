package com.epam.spring.time_tracking.repository;

import com.epam.spring.time_tracking.model.User;

public interface UserRepo {
    User getUserByEmail(String email);

    User createUser(User user);
}
