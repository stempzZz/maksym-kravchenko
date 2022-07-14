package com.epam.spring.time_tracking.model;

import com.epam.spring.time_tracking.model.enums.status.RequestStatus;
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
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    private boolean forDelete;

    @Column(nullable = false)
    private LocalDateTime createTime = LocalDateTime.now();

    @ManyToOne(optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Activity activity;

}
