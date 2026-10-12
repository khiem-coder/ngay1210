package com.example.coursemanagement.repository;

import com.example.coursemanagement.entity.CourseImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CourseImageRepository extends JpaRepository<CourseImage, Integer> {

    /** Chuyển mọi ảnh của khóa học về ảnh thường (gọi trước khi đặt ảnh đại diện mới). */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update CourseImage i set i.thumbnail = false where i.course.id = :courseId")
    int clearThumbnail(@Param("courseId") Integer courseId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update CourseImage i set i.thumbnail = true where i.id = :imageId")
    int markThumbnail(@Param("imageId") Integer imageId);
}
