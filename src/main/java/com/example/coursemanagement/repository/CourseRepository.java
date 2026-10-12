package com.example.coursemanagement.repository;

import com.example.coursemanagement.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CourseRepository extends JpaRepository<Course, Integer>, JpaSpecificationExecutor<Course> {
    long countByCategory_Id(Integer categoryId);
    boolean existsByCategory_Id(Integer categoryId);
    boolean existsByNameIgnoreCaseAndCategory_Id(String name, Integer categoryId);
    boolean existsByNameIgnoreCaseAndCategory_IdAndIdNot(String name, Integer categoryId, Integer id);
}
