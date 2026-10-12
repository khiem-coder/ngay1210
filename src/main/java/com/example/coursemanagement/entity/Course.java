package com.example.coursemanagement.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Courses",
        // Tên khóa học không trùng trong cùng một loại khóa học
        uniqueConstraints = @UniqueConstraint(name = "UQ_Courses_Category_Name",
                columnNames = {"CourseCategoryId", "Name"}))
// Học phí > 0 và thời lượng > 0 (ràng buộc CHECK trong database)
@Check(constraints = "TuitionFee > 0 AND Duration > 0")
public class Course {

    public static final int SHORT_TERM_MAX_HOURS = 30;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Nationalized
    @Column(name = "Name", nullable = false, length = 150)
    private String name;

    @Column(name = "TuitionFee", nullable = false, precision = 18, scale = 2)
    private BigDecimal tuitionFee;

    @Column(name = "Duration", nullable = false)
    private Integer duration;

    @Column(name = "IsActive", nullable = false)
    private boolean active = true;

    @Nationalized
    @Column(name = "Description", length = 1000)
    private String description;

    // Mỗi Course thuộc một CourseCategory
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CourseCategoryId", nullable = false)
    private CourseCategory category;

    // Một Course có nhiều CourseImage (ảnh đại diện xếp trước)
    @OneToMany(mappedBy = "course", cascade = CascadeType.REMOVE)
    @OrderBy("thumbnail DESC, id ASC")
    private List<CourseImage> images = new ArrayList<>();

    /** Chỉ tính khi hiển thị, KHÔNG có cột trong database. */
    public String getDurationType() {
        return (duration != null && duration <= SHORT_TERM_MAX_HOURS) ? "Ngắn hạn" : "Dài hạn";
    }

    /** Ảnh đại diện (null nếu chưa có). */
    public CourseImage getThumbnailImage() {
        return images.stream().filter(CourseImage::isThumbnail).findFirst().orElse(null);
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getTuitionFee() { return tuitionFee; }
    public void setTuitionFee(BigDecimal tuitionFee) { this.tuitionFee = tuitionFee; }
    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public CourseCategory getCategory() { return category; }
    public void setCategory(CourseCategory category) { this.category = category; }
    public List<CourseImage> getImages() { return images; }
    public void setImages(List<CourseImage> images) { this.images = images; }
}
