package com.epam.spring.time_tracking.model;

import com.epam.spring.time_tracking.model.enums.status.ActivityStatus;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    private String image;

    @Column(nullable = false)
    private int peopleCount = 0;

    @Column(nullable = false)
    private LocalDateTime createTime = LocalDateTime.now();

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ActivityStatus status;

    @ManyToOne(optional = false)
    private User creator;

    @ManyToMany
    @JoinTable(name = "activity_category",
            joinColumns = @JoinColumn(name = "activity_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    @ToString.Exclude
    private List<Category> categories;

    @OneToMany(mappedBy = "activity", orphanRemoval = true)
    @ToString.Exclude
    private List<ActivityUser> users;

}
