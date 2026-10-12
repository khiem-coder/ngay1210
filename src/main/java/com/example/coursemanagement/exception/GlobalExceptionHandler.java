package com.example.coursemanagement.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/** Không để lỗi hệ thống hiện thẳng ra màn hình: mọi lỗi đều về trang error.html thân thiện. */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ModelAndView handleNotFound(ResourceNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "Không tìm thấy dữ liệu", ex.getMessage());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ModelAndView handleNoResource(NoResourceFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "Không tìm thấy trang", "Trang bạn truy cập không tồn tại.");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ModelAndView handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return build(HttpStatus.BAD_REQUEST, "Yêu cầu không hợp lệ",
                "Giá trị của tham số '" + ex.getName() + "' không hợp lệ.");
    }

    @ExceptionHandler(BusinessException.class)
    public ModelAndView handleBusiness(BusinessException ex) {
        return build(HttpStatus.BAD_REQUEST, "Không thể thực hiện thao tác", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleOther(Exception ex) {
        log.error("Lỗi không mong muốn", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Đã xảy ra lỗi",
                "Hệ thống gặp sự cố. Vui lòng thử lại sau.");
    }

    private ModelAndView build(HttpStatus status, String title, String message) {
        ModelAndView mav = new ModelAndView("error");
        mav.setStatus(status);
        mav.addObject("status", status.value());
        mav.addObject("title", title);
        mav.addObject("message", message);
        return mav;
    }
}
