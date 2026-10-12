package com.example.coursemanagement.service;

import com.example.coursemanagement.dto.CourseForm;
import com.example.coursemanagement.entity.Course;
import com.example.coursemanagement.entity.CourseImage;
import com.example.coursemanagement.exception.BusinessException;
import com.example.coursemanagement.exception.ResourceNotFoundException;
import com.example.coursemanagement.repository.*;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseCategoryRepository categoryRepository;
    private final CourseImageRepository imageRepository;

    public CourseService(CourseRepository courseRepository,
                         CourseCategoryRepository categoryRepository,
                         CourseImageRepository imageRepository) {
        this.courseRepository = courseRepository;
        this.categoryRepository = categoryRepository;
        this.imageRepository = imageRepository;
    }

    // ------------------------------------------------------------------ Tra cứu
    @Transactional(readOnly = true)
    public List<Course> search(String keyword, Integer categoryId, String status,
                               BigDecimal minFee, BigDecimal maxFee, String sort) {
        // Ràng buộc 3: "Đang tuyển sinh" chỉ lấy IsActive = true
        Boolean active = "active".equals(status) ? Boolean.TRUE
                : "inactive".equals(status) ? Boolean.FALSE : null;

        Sort order = switch (sort == null ? "" : sort) {
            case "fee_asc" -> Sort.by("tuitionFee").ascending();
            case "fee_desc" -> Sort.by("tuitionFee").descending();
            case "duration_asc" -> Sort.by("duration").ascending();
            default -> Sort.by("id").ascending();
        };

        return courseRepository.findAll(
                CourseSpecifications.search(keyword, categoryId, active, minFee, maxFee), order);
    }

    @Transactional(readOnly = true)
    public Course getById(Integer id) {
        return courseRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Không tìm thấy khóa học có mã " + id + "."));
    }

    // ------------------------------------------------------------------ Create / Edit / Delete
    /** Kiểm tra: loại khóa học tồn tại + tên không trùng trong cùng loại. */
    @Transactional(readOnly = true)
    public void validate(CourseForm form, Integer excludeId, BindingResult br) {
        Integer categoryId = form.getCourseCategoryId();
        if (categoryId != null && !categoryRepository.existsById(categoryId)) {
            br.rejectValue("courseCategoryId", "category.notFound", "Loại khóa học không tồn tại");
        }
        if (form.getName() != null && !form.getName().isBlank()
                && categoryId != null && !br.hasFieldErrors("courseCategoryId")) {
            String name = form.getName().trim();
            boolean duplicate = (excludeId == null)
                    ? courseRepository.existsByNameIgnoreCaseAndCategory_Id(name, categoryId)
                    : courseRepository.existsByNameIgnoreCaseAndCategory_IdAndIdNot(name, categoryId, excludeId);
            if (duplicate) {
                br.rejectValue("name", "name.duplicate", "Tên khóa học đã tồn tại trong loại khóa học này");
            }
        }
    }

    @Transactional
    public Course create(CourseForm form) {
        Course c = new Course();
        apply(c, form);
        return courseRepository.save(c);
    }

    @Transactional
    public void update(Integer id, CourseForm form) {
        Course c = getById(id);
        apply(c, form);
        courseRepository.save(c);
    }

    @Transactional
    public void delete(Integer id) {
        courseRepository.delete(getById(id));   // ảnh bị xóa theo (cascade)
    }

    private void apply(Course c, CourseForm form) {
        c.setName(form.getName().trim());
        c.setTuitionFee(form.getTuitionFee());
        c.setDuration(form.getDuration());
        c.setActive(form.isActive());
        c.setDescription(form.getDescription() == null || form.getDescription().isBlank()
                ? null : form.getDescription().trim());
        c.setCategory(categoryRepository.findById(form.getCourseCategoryId()).orElseThrow(() ->
                new ResourceNotFoundException("Không tìm thấy loại khóa học có mã "
                        + form.getCourseCategoryId() + ".")));
    }

    // ------------------------------------------------------------------ Ảnh khóa học
    @Transactional
    public void addImage(Integer courseId, String url, boolean thumbnail) {
        Course course = getById(courseId);
        String cleaned = url == null ? "" : url.trim();
        if (!isValidUrl(cleaned)) {
            throw new BusinessException("Đường dẫn ảnh không hợp lệ. Vui lòng nhập URL bắt đầu bằng http:// hoặc https://");
        }
        if (thumbnail) {
            imageRepository.clearThumbnail(courseId);   // ảnh đại diện cũ -> ảnh thường
        }
        CourseImage image = new CourseImage();
        image.setImageUrl(cleaned);
        image.setThumbnail(thumbnail);
        image.setCourse(course);
        imageRepository.save(image);
    }

    /** Đổi ảnh đại diện: ảnh cũ tự động chuyển thành ảnh thường. */
    @Transactional
    public void setThumbnail(Integer courseId, Integer imageId) {
        getById(courseId);
        findImage(courseId, imageId);
        imageRepository.clearThumbnail(courseId);
        imageRepository.markThumbnail(imageId);
    }

    @Transactional
    public void deleteImage(Integer courseId, Integer imageId) {
        getById(courseId);
        imageRepository.delete(findImage(courseId, imageId));
    }

    private CourseImage findImage(Integer courseId, Integer imageId) {
        return imageRepository.findById(imageId)
                .filter(i -> i.getCourse().getId().equals(courseId))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy ảnh có mã " + imageId + " của khóa học này."));
    }

    private boolean isValidUrl(String url) {
        if (url.isEmpty() || url.length() > 500) return false;
        try {
            URI uri = new URI(url);
            String scheme = uri.getScheme();
            return scheme != null
                    && (scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))
                    && uri.getHost() != null;
        } catch (Exception e) {
            return false;
        }
    }
}
