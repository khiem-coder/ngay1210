package com.example.coursemanagement.repository;

import com.example.coursemanagement.entity.Course;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** Ghép điều kiện lọc thành MỘT câu truy vấn SQL (không lọc bằng vòng lặp). */
public final class CourseSpecifications {

    private CourseSpecifications() {}

    public static Specification<Course> search(String keyword, Integer categoryId, Boolean active,
                                               BigDecimal minFee, BigDecimal maxFee) {
        return (root, query, cb) -> {
            // fetch join loại khóa học để tránh N+1 (bỏ qua với câu count)
            if (query != null && !Long.class.equals(query.getResultType())
                    && !long.class.equals(query.getResultType())) {
                root.fetch("category", JoinType.INNER);
            }

            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("name")),
                        "%" + keyword.trim().toLowerCase() + "%"));
            }
            if (categoryId != null) {
                predicates.add(cb.equal(root.get("category").get("id"), categoryId));
            }
            if (active != null) {
                predicates.add(cb.equal(root.get("active"), active));
            }
            if (minFee != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("tuitionFee"), minFee));
            }
            if (maxFee != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("tuitionFee"), maxFee));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
