package com.example.coursemanagement.service;

import com.example.coursemanagement.dto.CategoryForm;
import com.example.coursemanagement.entity.CourseCategory;
import com.example.coursemanagement.exception.BusinessException;
import com.example.coursemanagement.exception.ResourceNotFoundException;
import com.example.coursemanagement.repository.CourseCategoryRepository;
import com.example.coursemanagement.repository.CourseRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.util.List;

@Service
public class CategoryService {

    private final CourseCategoryRepository categoryRepository;
    private final CourseRepository courseRepository;

    public CategoryService(CourseCategoryRepository categoryRepository, CourseRepository courseRepository) {
        this.categoryRepository = categoryRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional(readOnly = true)
    public List<CourseCategory> findAll() {
        return categoryRepository.findAll(Sort.by("name"));
    }

    @Transactional(readOnly = true)
    public boolean exists(Integer id) {
        return id != null && categoryRepository.existsById(id);
    }

    @Transactional(readOnly = true)
    public CourseCategory getById(Integer id) {
        return categoryRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Không tìm thấy loại khóa học có mã " + id + "."));
    }

    public void validate(CategoryForm form, Integer excludeId, BindingResult br) {
        if (form.getName() == null || form.getName().isBlank() || br.hasFieldErrors("name")) return;
        String name = form.getName().trim();
        boolean duplicate = (excludeId == null)
                ? categoryRepository.existsByNameIgnoreCase(name)
                : categoryRepository.existsByNameIgnoreCaseAndIdNot(name, excludeId);
        if (duplicate) {
            br.rejectValue("name", "name.duplicate", "Tên loại khóa học đã tồn tại");
        }
    }

    @Transactional
    public CourseCategory create(CategoryForm form) {
        CourseCategory c = new CourseCategory();
        apply(c, form);
        return categoryRepository.save(c);
    }

    @Transactional
    public void update(Integer id, CategoryForm form) {
        CourseCategory c = getById(id);
        apply(c, form);
        categoryRepository.save(c);
    }

    /** Ràng buộc 1: còn khóa học thuộc loại này thì không được xóa. */
    @Transactional
    public void delete(Integer id) {
        CourseCategory c = getById(id);
        long count = courseRepository.countByCategory_Id(id);
        if (count > 0) {
            throw new BusinessException("Không thể xóa loại khóa học \"" + c.getName()
                    + "\" vì vẫn còn " + count + " khóa học thuộc loại này.");
        }
        categoryRepository.delete(c);
    }

    private void apply(CourseCategory c, CategoryForm form) {
        c.setName(form.getName().trim());
        c.setDescription(form.getDescription() == null || form.getDescription().isBlank()
                ? null : form.getDescription().trim());
    }
}
