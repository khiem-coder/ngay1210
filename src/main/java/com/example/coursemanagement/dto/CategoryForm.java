package com.example.coursemanagement.dto;

import com.example.coursemanagement.entity.CourseCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoryForm {

    @NotBlank(message = "Tên loại khóa học không được để trống")
    @Size(max = 100, message = "Tên loại khóa học tối đa 100 ký tự")
    private String name;

    @Size(max = 500, message = "Mô tả tối đa 500 ký tự")
    private String description;

    public static CategoryForm from(CourseCategory c) {
        CategoryForm f = new CategoryForm();
        f.name = c.getName();
        f.description = c.getDescription();
        return f;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
