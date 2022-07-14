package com.epam.spring.time_tracking.model;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@NamedNativeQuery(name = "Category.findByIsDefault",
        query = "select * from category where is_default = ?1", resultClass = Category.class)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nameEn;

    @Column(unique = true, nullable = false)
    private String nameUa;

    private boolean isDefault = false;

    @ManyToMany(mappedBy = "categories")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ToString.Exclude
    private List<Activity> activities;

}
