package com.example.coursemanagement.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "CourseImages")
public class CourseImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Nationalized
    @Column(name = "ImageUrl", nullable = false, length = 500)
    private String imageUrl;

    @Column(name = "IsThumbnail", nullable = false)
    private boolean thumbnail;

    // Xóa khóa học thì ảnh bị xóa theo (ON DELETE CASCADE trong database)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CourseId", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Course course;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public boolean isThumbnail() { return thumbnail; }
    public void setThumbnail(boolean thumbnail) { this.thumbnail = thumbnail; }
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
}
