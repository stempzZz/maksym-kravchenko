package com.epam.spring.time_tracking.model;

import com.epam.spring.time_tracking.model.enums.status.ActivityUserStatus;
import com.epam.spring.time_tracking.model.keys.ActivityUserKey;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ActivityUser {

    @EmbeddedId
    private ActivityUserKey id = new ActivityUserKey();

    @ManyToOne
    @MapsId("activityId")
    @JoinColumn(name = "activity_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Activity activity;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    private LocalDateTime startTime;

    private LocalDateTime stopTime;

    @Column(nullable = false)
    private double spentTime = 0.0;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ActivityUserStatus status = ActivityUserStatus.NOT_STARTED;

}
