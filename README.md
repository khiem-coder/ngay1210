# Quản lý khóa học – Spring Boot (**Code First**)

**Công nghệ:** Spring Boot 3.3, Spring Data JPA (Hibernate), Thymeleaf, SQL Server, Java 17+ (khuyên dùng JDK 21).

## Code First nghĩa là gì ở đây?
- Cấu trúc bảng được **khai báo bằng code** trong các entity (`Course`, `CourseCategory`, `CourseImage`):
  kiểu cột, `NOT NULL`, khóa ngoại, `UNIQUE (CourseCategoryId, Name)`, `CHECK (học phí > 0, thời lượng > 0)`, `ON DELETE CASCADE`...
- `spring.jpa.hibernate.ddl-auto=update` → khi chạy ứng dụng, **Hibernate tự tạo bảng** nếu chưa có.
- `DataSeeder` tự nạp dữ liệu mẫu (3 loại, 6 khóa học, 7 ảnh) lần chạy đầu tiên; chạy lại không bị trùng.

## Cách chạy
1. SSMS: chạy `database/01_setup_database_and_login.sql` (tạo database rỗng `CourseManagement` + tài khoản `course_app`).
   - Nếu trước đó đã có bảng từ bản Database First: chạy thêm `database/02_reset_tables_optional.sql`.
2. Kiểm tra `src/main/resources/application.properties`: URL, `username`, `password` (phải có đủ 3 dòng `spring.datasource.*`).
3. Terminal trong thư mục project (không cần cài Maven):
   - Windows: `.\mvnw.cmd spring-boot:run`
   - Linux/macOS: `./mvnw spring-boot:run`
4. Mở http://localhost:8080 — vào SSMS, Refresh database `CourseManagement` sẽ thấy 3 bảng do Hibernate tạo.

## Ràng buộc quan trọng
| Ràng buộc | Khai báo trong entity (database) | Kiểm tra trong code |
|---|---|---|
| TuitionFee > 0, Duration > 0 | `@Check` | `@DecimalMin`, `@Min` |
| Tên không trùng trong cùng loại | `@UniqueConstraint` | `CourseService.validate` |
| CourseCategoryId phải tồn tại | `@ManyToOne` (khóa ngoại) | `CourseService.validate` |
| Mỗi khóa học tối đa 1 ảnh đại diện | filtered unique index (tạo bởi `DataSeeder`) | `CourseService.setThumbnail` |
| Không xóa loại còn khóa học | khóa ngoại (không cascade) | `CategoryService.delete` |

## Gợi ý demo (14 mục theo đề)
1–2. SSMS: 3 bảng do Hibernate tạo + Database Diagram.
3–9. `/courses`: danh sách, tìm theo tên, lọc loại / trạng thái / khoảng học phí, sắp xếp, cột Ngắn hạn/Dài hạn.
10. Thêm / Sửa khóa học.  11–12. `/courses/{id}`: thêm ảnh bằng URL, đặt ảnh đại diện.
13. `/categories`: xóa "Lập trình Web" → báo lỗi vì còn khóa học.
14. `/courses/999` → trang lỗi thân thiện.

## Xem lỗi khi chạy
Log hiện ở Terminal/Debug Console và được ghi vào `app.log`. Tìm dòng `Caused by:`.
