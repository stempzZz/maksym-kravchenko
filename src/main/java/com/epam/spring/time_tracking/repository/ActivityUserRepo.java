package com.epam.spring.time_tracking.repository;

import com.epam.spring.time_tracking.model.Activity;
import com.epam.spring.time_tracking.model.ActivityUser;
import com.epam.spring.time_tracking.model.User;
import com.epam.spring.time_tracking.model.keys.ActivityUserKey;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityUserRepo extends JpaRepository<ActivityUser, ActivityUserKey> {

    List<ActivityUser> findAllByActivity(Activity activity);

    List<ActivityUser> findAllByActivity(Activity activity, Pageable pageable);

    List<ActivityUser> findAllByUser(User user, Pageable pageable);

    @Query("select case when count(au) > 0 then true else false end from ActivityUser au " +
            "where au.user = ?1 and au.activity = ?2")
    boolean userExistsInActivity(User user, Activity activity);

}
