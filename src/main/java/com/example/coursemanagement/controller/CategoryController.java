package com.example.coursemanagement.controller;

import com.example.coursemanagement.dto.CategoryForm;
import com.example.coursemanagement.exception.BusinessException;
import com.example.coursemanagement.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        return "categories/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        prepare(model, new CategoryForm(), "Thêm loại khóa học", "/categories/create");
        return "categories/form";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("form") CategoryForm form, BindingResult br,
                         Model model, RedirectAttributes ra) {
        categoryService.validate(form, null, br);
        if (br.hasErrors()) {
            prepare(model, null, "Thêm loại khóa học", "/categories/create");
            return "categories/form";
        }
        categoryService.create(form);
        ra.addFlashAttribute("success", "Đã thêm loại khóa học.");
        return "redirect:/categories";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model) {
        prepare(model, CategoryForm.from(categoryService.getById(id)),
                "Sửa loại khóa học", "/categories/" + id + "/edit");
        return "categories/form";
    }

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Integer id, @Valid @ModelAttribute("form") CategoryForm form,
                       BindingResult br, Model model, RedirectAttributes ra) {
        categoryService.getById(id);
        categoryService.validate(form, id, br);
        if (br.hasErrors()) {
            prepare(model, null, "Sửa loại khóa học", "/categories/" + id + "/edit");
            return "categories/form";
        }
        categoryService.update(id, form);
        ra.addFlashAttribute("success", "Đã cập nhật loại khóa học.");
        return "redirect:/categories";
    }

    /** Ràng buộc 1: còn khóa học thì không cho xóa, báo lỗi ngay trên trang. */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            categoryService.delete(id);
            ra.addFlashAttribute("success", "Đã xóa loại khóa học.");
        } catch (BusinessException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/categories";
    }

    private void prepare(Model model, CategoryForm form, String title, String action) {
        if (form != null) {
            model.addAttribute("form", form);
        }
        model.addAttribute("title", title);
        model.addAttribute("formAction", action);
    }
}
