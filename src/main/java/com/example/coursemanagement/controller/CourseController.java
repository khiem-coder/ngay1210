package com.example.coursemanagement.controller;

import com.example.coursemanagement.dto.CourseForm;
import com.example.coursemanagement.entity.Course;
import com.example.coursemanagement.exception.BusinessException;
import com.example.coursemanagement.service.CategoryService;
import com.example.coursemanagement.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;
    private final CategoryService categoryService;

    public CourseController(CourseService courseService, CategoryService categoryService) {
        this.courseService = courseService;
        this.categoryService = categoryService;
    }

    // ------------------------------------------------------------------ Danh sách + tra cứu
    @GetMapping
    public String list(@RequestParam(required = false) String keyword,
                       @RequestParam(required = false) Integer categoryId,
                       @RequestParam(required = false) String status,
                       @RequestParam(required = false) BigDecimal minFee,
                       @RequestParam(required = false) BigDecimal maxFee,
                       @RequestParam(required = false, defaultValue = "") String sort,
                       Model model) {

        List<Course> courses = List.of();
        String filterError = null;

        if (categoryId != null && !categoryService.exists(categoryId)) {
            filterError = "Loại khóa học đã chọn không tồn tại.";
        } else if (minFee != null && maxFee != null && minFee.compareTo(maxFee) > 0) {
            filterError = "Học phí tối thiểu không được lớn hơn học phí tối đa.";
        } else {
            courses = courseService.search(keyword, categoryId, status, minFee, maxFee, sort);
        }

        // Giữ lại điều kiện đã nhập để hiển thị trên form
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("status", status);
        model.addAttribute("minFee", minFee);
        model.addAttribute("maxFee", maxFee);
        model.addAttribute("sort", sort);

        model.addAttribute("courses", courses);
        model.addAttribute("filterError", filterError);
        model.addAttribute("categories", categoryService.findAll());
        return "courses/list";
    }

    // ------------------------------------------------------------------ Chi tiết
    @GetMapping("/{id}")
    public String details(@PathVariable Integer id, Model model) {
        model.addAttribute("course", courseService.getById(id));
        return "courses/details";
    }

    // ------------------------------------------------------------------ Create
    @GetMapping("/create")
    public String createForm(Model model) {
        prepareForm(model, new CourseForm(), "Thêm khóa học", "/courses/create");
        return "courses/form";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("form") CourseForm form, BindingResult br,
                         Model model, RedirectAttributes ra) {
        courseService.validate(form, null, br);
        if (br.hasErrors()) {
            prepareForm(model, null, "Thêm khóa học", "/courses/create");
            return "courses/form";
        }
        Course saved = courseService.create(form);
        ra.addFlashAttribute("success", "Đã thêm khóa học \"" + saved.getName() + "\".");
        return "redirect:/courses/" + saved.getId();
    }

    // ------------------------------------------------------------------ Edit
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model) {
        Course course = courseService.getById(id);
        prepareForm(model, CourseForm.from(course), "Sửa khóa học", "/courses/" + id + "/edit");
        return "courses/form";
    }

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Integer id, @Valid @ModelAttribute("form") CourseForm form,
                       BindingResult br, Model model, RedirectAttributes ra) {
        courseService.getById(id);  // CourseId không tồn tại -> trang lỗi 404 thân thiện
        courseService.validate(form, id, br);
        if (br.hasErrors()) {
            prepareForm(model, null, "Sửa khóa học", "/courses/" + id + "/edit");
            return "courses/form";
        }
        courseService.update(id, form);
        ra.addFlashAttribute("success", "Đã cập nhật khóa học.");
        return "redirect:/courses/" + id;
    }

    // ------------------------------------------------------------------ Delete
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id, RedirectAttributes ra) {
        courseService.delete(id);
        ra.addFlashAttribute("success", "Đã xóa khóa học.");
        return "redirect:/courses";
    }

    // ------------------------------------------------------------------ Ảnh khóa học
    @PostMapping("/{courseId}/images")
    public String addImage(@PathVariable Integer courseId,
                           @RequestParam(required = false) String imageUrl,
                           @RequestParam(required = false, defaultValue = "false") boolean thumbnail,
                           RedirectAttributes ra) {
        try {
            courseService.addImage(courseId, imageUrl, thumbnail);
            ra.addFlashAttribute("success", "Đã thêm ảnh cho khóa học.");
        } catch (BusinessException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/courses/" + courseId;
    }

    @PostMapping("/{courseId}/images/{imageId}/thumbnail")
    public String setThumbnail(@PathVariable Integer courseId, @PathVariable Integer imageId,
                               RedirectAttributes ra) {
        courseService.setThumbnail(courseId, imageId);
        ra.addFlashAttribute("success", "Đã đổi ảnh đại diện.");
        return "redirect:/courses/" + courseId;
    }

    @PostMapping("/{courseId}/images/{imageId}/delete")
    public String deleteImage(@PathVariable Integer courseId, @PathVariable Integer imageId,
                              RedirectAttributes ra) {
        courseService.deleteImage(courseId, imageId);
        ra.addFlashAttribute("success", "Đã xóa ảnh.");
        return "redirect:/courses/" + courseId;
    }

    // ------------------------------------------------------------------ helper
    private void prepareForm(Model model, CourseForm form, String title, String action) {
        if (form != null) {
            model.addAttribute("form", form);
        }
        model.addAttribute("title", title);
        model.addAttribute("formAction", action);
        model.addAttribute("categories", categoryService.findAll());   // chọn loại từ database
    }
}
