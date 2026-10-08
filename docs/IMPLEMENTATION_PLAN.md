# Kế hoạch Triển khai Hệ thống iVehicle (Mobile-First Android)

## 1. Khảo sát Hiện trạng & Mục tiêu Sản phẩm
- Ứng dụng: iVehicle - Hệ thống quản lý dịch vụ chăm sóc ô tô & xe máy cao cấp tại Việt Nam.
- Ngôn ngữ: Tiếng Việt, Tiền tệ VND, Múi giờ Asia/Ho_Chi_Minh.
- Thiết kế: Theo chỉ dẫn thiết kế **Liquid Glass Automotive** (Glassmorphism + Apple HIG, frosted glass translucency, tactile cards, pills, blue `#007aff`/`#0058bc`, dark `#111114`, tertiary `#34c759` / `#72fe88`).
- Đối tượng người dùng & RBAC:
  1. Khách hàng (Customer): Quản lý xe (ô tô/xe máy), xác minh xe, đặt lịch hẹn 3 bước, duyệt báo giá (duyệt toàn bộ hoặc từng hạng mục), thanh toán (tiền mặt/chuyển khoản QR/thẻ/ví), theo dõi tiến độ sửa chữa/kiểm tra xe, bảo hành, nhắc lịch.
  2. Kỹ thuật viên (Technician): Nhận việc theo ca/kỹ năng, thực hiện checklist kiểm tra xe (ảnh/ghi chú), báo phụ tùng, kiểm tra chất lượng (QC), bàn giao.
  3. Quản lý chi nhánh (Branch Manager): Phân công KTV, tiếp nhận xe, lập báo giá có versioning, quản lý kho chi nhánh (tồn thực, tồn giữ chỗ, tồn khả dụng), đối soát thanh toán.
  4. Quản trị viên trụ sở chính (HQ Admin): Quản lý mạng lưới chi nhánh (Trực thuộc vs Nhượng quyền), theo dõi doanh thu/chi phí/vốn đầu tư, hợp đồng nhượng quyền, yêu cầu giải trình, audit log toàn hệ thống.

## 2. Kiến trúc & Mô hình Dữ liệu (Modular Architecture)
- State Machine chuẩn:
  `DRAFT_CREATED` -> `CONFIRMED` -> `VEHICLE_RECEIVED` -> `INSPECTION` -> `QUOTED` -> `PARTIALLY_OR_FULLY_APPROVED` -> `PAYMENT_PENDING` -> `IN_PROGRESS` -> `QC_CHECK` -> `COMPLETED_HANDOVER` -> `WARRANTY_ACTIVE`.
- Lưu trữ: Database Local In-Memory & Repository Pattern với mock adapter sandbox (Zalo OA ZNS, QR VietQR Pay, E-Invoice, Inventory).
- Báo giá: Versioning (v1, v2...), hiệu lực 15 ngày, lưu snapshot phê duyệt theo từng line item.
- Quy tắc hủy/đổi lịch: Trước 2 giờ cho phép hủy tự do, trong vòng 2 giờ chuyển sang yêu cầu duyệt bởi Quản lý.

## 3. Lộ trình Triển khai
- Giai đoạn 1: Base UI/UX Theme (Liquid Glass Automotive palette, typography, elevation).
- Giai đoạn 2: Model & State Machine Core (Entities, Roles, Repositories, Audit Log).
- Giai đoạn 3: Customer Module (Trang chủ, Xe của tôi, Đặt lịch hẹn 3 bước, Duyệt báo giá, Thanh toán QR).
- Giai đoạn 4: Technician & Workshop Module (Danh sách việc, Checklist kiểm tra xe, Ảnh, Vật tư, QC).
- Giai đoạn 5: Management & Franchise Module (Kho chi nhánh, Điều chuyển, Báo cáo doanh thu/vốn, Hợp đồng & Audit log).
- Giai đoạn 6: Testing & Hoàn thiện tài liệu kiến trúc.
