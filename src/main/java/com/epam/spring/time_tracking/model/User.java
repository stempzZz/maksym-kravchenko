package com.epam.spring.time_tracking.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@NamedQuery(name = "User.findAllNotInActivity",
        query = "select u from User u where u.blocked = false and u.admin = false and " +
                "u not in (select au.user from ActivityUser au where au.activity = ?1)")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String firstName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private int activityCount;

    @Column(nullable = false)
    private double spentTime;

    private boolean admin;

    private boolean blocked;

    @OneToMany(mappedBy = "user")
    @ToString.Exclude
    private List<ActivityUser> activities;

}
