package com.example.coursemanagement.dto;

import com.example.coursemanagement.entity.Course;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class CourseForm {

    @NotBlank(message = "Tên khóa học không được để trống")
    @Size(max = 150, message = "Tên khóa học tối đa 150 ký tự")
    private String name;

    @NotNull(message = "Học phí không được để trống")
    @DecimalMin(value = "0", inclusive = false, message = "Học phí phải lớn hơn 0")
    @Digits(integer = 16, fraction = 2, message = "Học phí không hợp lệ")
    private BigDecimal tuitionFee;

    @NotNull(message = "Thời lượng không được để trống")
    @Min(value = 1, message = "Thời lượng phải lớn hơn 0")
    private Integer duration;

    private boolean active = true;

    @Size(max = 1000, message = "Mô tả tối đa 1000 ký tự")
    private String description;

    @NotNull(message = "Vui lòng chọn loại khóa học")
    private Integer courseCategoryId;

    public static CourseForm from(Course c) {
        CourseForm f = new CourseForm();
        f.name = c.getName();
        f.tuitionFee = c.getTuitionFee();
        f.duration = c.getDuration();
        f.active = c.isActive();
        f.description = c.getDescription();
        f.courseCategoryId = c.getCategory().getId();
        return f;
    }

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
    public Integer getCourseCategoryId() { return courseCategoryId; }
    public void setCourseCategoryId(Integer courseCategoryId) { this.courseCategoryId = courseCategoryId; }
}
