package com.example.coursemanagement.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Nationalized;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "CourseCategories",
        uniqueConstraints = @UniqueConstraint(name = "UQ_CourseCategories_Name", columnNames = "Name"))
public class CourseCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Nationalized   // -> NVARCHAR (lưu được tiếng Việt có dấu)
    @Column(name = "Name", nullable = false, length = 100)
    private String name;

    @Nationalized
    @Column(name = "Description", length = 500)
    private String description;

    // Một CourseCategory có nhiều Course
    @OneToMany(mappedBy = "category")
    private List<Course> courses = new ArrayList<>();

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<Course> getCourses() { return courses; }
    public void setCourses(List<Course> courses) { this.courses = courses; }
}
