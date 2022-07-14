package com.epam.spring.time_tracking.model.keys;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityUserKey implements Serializable {

    @Column
    Long activityId;

    @Column
    Long userId;

}
