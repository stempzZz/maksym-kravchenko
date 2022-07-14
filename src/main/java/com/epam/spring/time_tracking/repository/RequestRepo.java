package com.epam.spring.time_tracking.repository;

import com.epam.spring.time_tracking.model.Activity;
import com.epam.spring.time_tracking.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestRepo extends JpaRepository<Request, Long> {

    @Query("select case when count(r) > 0 then true else false end from Request r " +
            "where r.activity = ?1 and r.forDelete = true " +
            "and (r.status = com.epam.spring.time_tracking.model.enums.status.RequestStatus.WAITING " +
            "or r.status = com.epam.spring.time_tracking.model.enums.status.RequestStatus.CONFIRMED)")
    boolean requestForDeleteWithActivityExists(Activity activity);

    void deleteAllByActivity(Activity activity);

}
