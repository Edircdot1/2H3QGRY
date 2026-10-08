# Phân Quyền Vai Trò Người Dùng (Role-Based Access Control)

| Vai trò | Phạm vi dữ liệu | Quyền hạn chính |
| :--- | :--- | :--- |
| **Khách hàng (Customer)** | Cá nhân & Xe thuộc quyền sở hữu | Thêm xe, đặt lịch 3 bước, duyệt từng mục báo giá, thanh toán qua QR, xem lịch sử sửa chữa theo từng xe, tra cứu bảo hành. |
| **Kỹ thuật viên (Technician)** | Xe và ca làm việc được phân công tại chi nhánh | Nhận việc, chấm checklist kiểm tra xe (đạt/không đạt), ghi chú hiện trạng, tra cứu kho phụ tùng và báo hoàn tất bàn giao. |
| **Quản lý chi nhánh (Branch Manager)** | Toàn bộ xe, lịch hẹn và kho tại chi nhánh phụ trách | Xác nhận/hủy lịch hẹn, phân công KTV theo ca và KPI, lập báo giá phiên bản, đối soát thanh toán tại quầy, quản lý xuất nhập tồn kho. |
| **Trụ sở chính (HQ Admin)** | Toàn hệ thống mạng lưới (Trực thuộc & Nhượng quyền) | Giám sát KPI, theo dõi doanh thu thực tế, vốn đầu tư, hợp đồng nhượng quyền, tỷ lệ Royalty, xử lý yêu cầu giải trình hợp đồng và truy vết Audit Log. |
