package com.epam.spring.time_tracking.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Category {

    private int id;
    private String nameEN;
    private String nameUA;

    public Category() {

    }

    public Category(int id, String nameEN, String nameUA) {
        this.id = id;
        this.nameEN = nameEN;
        this.nameUA = nameUA;
    }

}
