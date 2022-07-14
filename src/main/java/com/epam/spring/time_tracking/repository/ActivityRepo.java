package com.epam.spring.time_tracking.repository;

import com.epam.spring.time_tracking.model.Activity;
import com.epam.spring.time_tracking.model.User;
import com.epam.spring.time_tracking.model.enums.status.ActivityStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ActivityRepo extends JpaRepository<Activity, Long> {

    List<Activity> findAllByCreator(User creator);

    List<Activity> findAllByCreator(User creator, Pageable pageable);

    @Query("select a from Activity a where a in (select au.activity from ActivityUser au where au.user = ?1)")
    List<Activity> findAllForUser(User user, Pageable pageable);

    List<Activity> findAllByStatusIn(Collection<ActivityStatus> status, Pageable pageable);

}
