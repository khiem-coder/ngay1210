package com.example.coursemanagement;

import com.example.coursemanagement.entity.Course;
import com.example.coursemanagement.entity.CourseCategory;
import com.example.coursemanagement.entity.CourseImage;
import com.example.coursemanagement.repository.CourseCategoryRepository;
import com.example.coursemanagement.repository.CourseImageRepository;
import com.example.coursemanagement.repository.CourseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * CODE FIRST: bảng đã được Hibernate tạo từ các entity (spring.jpa.hibernate.ddl-auto=update).
 * Lớp này chạy sau khi bảng đã có để:
 *  1) tạo index "mỗi khóa học tối đa 1 ảnh đại diện" (JPA không khai báo được filtered index);
 *  2) nạp dữ liệu mẫu (chỉ khi chưa có loại khóa học nào) - chạy lại nhiều lần không bị trùng.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final CourseCategoryRepository categoryRepository;
    private final CourseRepository courseRepository;
    private final CourseImageRepository imageRepository;
    private final JdbcTemplate jdbc;

    public DataSeeder(CourseCategoryRepository categoryRepository, CourseRepository courseRepository,
                      CourseImageRepository imageRepository, JdbcTemplate jdbc) {
        this.categoryRepository = categoryRepository;
        this.courseRepository = courseRepository;
        this.imageRepository = imageRepository;
        this.jdbc = jdbc;
    }

    @Override
    public void run(String... args) {
        createThumbnailIndex();
        if (categoryRepository.count() > 0) {
            log.info("Database đã có dữ liệu, bỏ qua bước nạp dữ liệu mẫu.");
            return;
        }

        CourseCategory web = category("Lập trình Web", "Các khóa học phát triển ứng dụng web");
        CourseCategory mobile = category("Lập trình Di động", "Các khóa học phát triển ứng dụng di động");
        CourseCategory db = category("Cơ sở dữ liệu", "Các khóa học thiết kế và quản trị cơ sở dữ liệu");

        Course aspnet = course("ASP.NET Core", 5_000_000, 40, true, "Xây dựng ứng dụng web với ASP.NET Core MVC", web);
        Course react = course("ReactJS", 4_500_000, 30, true, "Xây dựng giao diện web hiện đại với ReactJS", web);
        course("HTML CSS cơ bản", 1_500_000, 20, true, "Nền tảng HTML và CSS cho người mới bắt đầu", web);   // chưa có ảnh
        Course flutter = course("Flutter", 6_000_000, 45, true, "Lập trình ứng dụng đa nền tảng với Flutter", mobile);
        course("Android Kotlin", 5_500_000, 36, false, "Lập trình Android với Kotlin (tạm ngừng tuyển sinh)", mobile); // chưa có ảnh
        Course sql = course("SQL Server cơ bản", 2_000_000, 24, true, "Truy vấn và thiết kế cơ sở dữ liệu SQL Server", db);

        image(aspnet, "https://picsum.photos/seed/aspnet1/600/400", true);
        image(aspnet, "https://picsum.photos/seed/aspnet2/600/400", false);
        image(react, "https://picsum.photos/seed/react1/600/400", true);
        image(react, "https://picsum.photos/seed/react2/600/400", false);
        image(react, "https://picsum.photos/seed/react3/600/400", false);
        image(flutter, "https://picsum.photos/seed/flutter1/600/400", true);
        image(sql, "https://picsum.photos/seed/sql1/600/400", true);

        log.info("Đã nạp dữ liệu mẫu: 3 loại khóa học, 6 khóa học, 7 ảnh.");
    }

    private void createThumbnailIndex() {
        try {
            jdbc.execute("IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'UX_CourseImages_OneThumbnail' "
                    + "AND object_id = OBJECT_ID('CourseImages')) "
                    + "CREATE UNIQUE INDEX UX_CourseImages_OneThumbnail ON CourseImages(CourseId) WHERE IsThumbnail = 1");
        } catch (Exception ex) {
            log.warn("Không tạo được index ảnh đại diện (không ảnh hưởng chạy ứng dụng): {}", ex.getMessage());
        }
    }

    private CourseCategory category(String name, String description) {
        CourseCategory c = new CourseCategory();
        c.setName(name);
        c.setDescription(description);
        return categoryRepository.save(c);
    }

    private Course course(String name, long fee, int duration, boolean active, String description, CourseCategory category) {
        Course c = new Course();
        c.setName(name);
        c.setTuitionFee(BigDecimal.valueOf(fee));
        c.setDuration(duration);
        c.setActive(active);
        c.setDescription(description);
        c.setCategory(category);
        return courseRepository.save(c);
    }

    private void image(Course course, String url, boolean thumbnail) {
        CourseImage i = new CourseImage();
        i.setImageUrl(url);
        i.setThumbnail(thumbnail);
        i.setCourse(course);
        imageRepository.save(i);
    }
}
