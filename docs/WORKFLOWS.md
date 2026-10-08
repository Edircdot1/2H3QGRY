# Quy Trình Nghiệp Vụ iVehicle (Business Workflows)

## 1. Vòng Đời Lịch Hẹn & Sửa Chữa (State Machine)
```
[DRAFT] 
  │
  ▼ (Khách đặt qua app)
[PENDING_CONFIRMATION] 
  │
  ▼ (Chi nhánh xác nhận)
[CONFIRMED] 
  │
  ▼ (Khách đưa xe đến xưởng)
[VEHICLE_RECEIVED] 
  │
  ▼ (KTV thực hiện checklist 6 hạng mục)
[INSPECTING] 
  │
  ▼ (Lập báo giá v1 hiệu lực 15 ngày)
[QUOTED] 
  │
  ▼ (Khách duyệt từng hạng mục)
[QUOTE_APPROVED] (Giữ chỗ phụ tùng trong kho)
  │
  ▼ (Thanh toán qua VietQR/Tiền mặt)
[IN_SERVICE] 
  │
  ▼ (KTV hoàn tất công việc)
[QUALITY_CHECK (QC)] 
  │
  ▼ (Nghiệm thu đạt chuẩn)
[COMPLETED / READY_FOR_PICKUP] 
  │
  ▼ (Kích hoạt bảo hành 7-180 ngày)
[WARRANTY_ACTIVE]
```

## 2. Quy Tắc Duyệt Báo Giá (Quote Approval Rules)
- Báo giá có hiệu lực trong vòng **15 ngày** kể từ thời điểm phát hành.
- Khách hàng có quyền duyệt toàn bộ hoặc chọn từng dòng riêng lẻ (Line-item selection).
- Các hạng mục không duyệt sẽ được hệ thống lưu vết riêng để theo dõi và nhắc nhở vào kỳ bảo dưỡng kế tiếp.
- Khi khách hàng bấm Ký duyệt, hệ thống tự động:
  1. Tính lại tổng tiền dựa trên các mục được duyệt.
  2. Tạo bản ghi Audit Log.
  3. Giữ chỗ (Reserve) các mã phụ tùng tương ứng trong kho chi nhánh, tránh tình trạng tồn âm hoặc thiếu hụt khi thi công.

## 3. Quy Tắc Đổi / Hủy Lịch Hẹn
- Trước thời điểm hẹn ít nhất **2 giờ**: Khách hàng được tự do đổi ngày/giờ hoặc hủy lịch trực tiếp trên ứng dụng.
- Dưới 2 giờ: Yêu cầu chuyển sang trạng thái chờ Quản lý chi nhánh tiếp nhận và xử lý thủ công, chống gian lận hoặc ảnh hưởng công suất khoang sửa chữa.
