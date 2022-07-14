package com.epam.spring.time_tracking.repository;

import com.epam.spring.time_tracking.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepo extends JpaRepository<Category, Long> {

    Optional<Category> findByIsDefault(boolean isDefault);

    boolean existsByNameEn(String nameEn);

    boolean existsByNameUa(String nameUa);

}
