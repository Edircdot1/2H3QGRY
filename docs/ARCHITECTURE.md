# Kiến Trúc Hệ Thống iVehicle (Architecture Overview)

## 1. Tổng Quan Kiến Trúc
iVehicle được thiết kế theo mô hình **Modular Monolith Mobile-First**, tuân thủ hệ thống thiết kế **Liquid Glass Automotive** (Apple HIG + Glassmorphism translucent surfaces) tối ưu hóa cho màn hình cảm ứng Android tại Việt Nam.

```
                           +-------------------------------+
                           |        iVehicle UI            |
                           | (Compose Liquid Glass System) |
                           +---------------+---------------+
                                           |
                                           v
      +-------------------------------------------------------------------------+
      |                           AppRepository (State Flows)                   |
      | - RBAC Permission Matrix                                                |
      | - Booking & Inspection State Machine                                    |
      | - Quote Versioning & Partial Approval                                   |
      | - Inventory Management (Actual vs Reserved vs Available)                |
      | - Audit Logging & Traceability                                          |
      +------------------------------------+------------------------------------+
                                           |
                +--------------------------+--------------------------+
                |                                                     |
                v                                                     v
+-------------------------------+                     +-------------------------------+
|    Local Model / Database     |                     |    External Service Adapters  |
| - Vehicles, Bookings, Quotes  |                     | - VietQR Gateway (Sandbox)    |
| - Branches, Inventory, Audits |                     | - Zalo OA / ZNS (Mock Ready)  |
| - Franchise Contracts         |                     | - E-Invoice / ERP (Mock Ready)|
+-------------------------------+                     +-------------------------------+
```

## 2. Các Thực Thể Nghiệp Vụ Chính (Core Entities)
- **User & UserRole**: Phân quyền chi tiết (Customer, Technician, Branch Manager, HQ Admin).
- **Vehicle & VehicleOwnership**: Một xe chỉ thuộc một chủ sở hữu tại một thời điểm, có trạng thái xác minh, theo dõi ODO & số khung VIN.
- **Branch & FranchiseContract**: Phân định rõ Chi nhánh Trực thuộc (Direct) và Nhượng quyền (Franchise), tỷ lệ Royalty, vốn đầu tư, chi phí vận hành và chỉ số tuân thủ hợp đồng.
- **Booking & State Machine**: Quy trình tiếp nhận, kiểm tra xe, báo giá, thanh toán, thực hiện, kiểm tra chất lượng (QC) và bàn giao.
- **Quote & QuoteItem**: Báo giá có phiên bản (v1, v2...), hiệu lực 15 ngày, hỗ trợ khách duyệt toàn bộ hoặc từng hạng mục độc lập.
- **InventoryItem**: Quản lý 3 trạng thái kho: Tồn thực tế (Actual), Tồn giữ chỗ cho đơn đã duyệt (Reserved), và Tồn khả dụng (Available).
- **AuditLog**: Ghi vết thời gian thực mọi thay đổi trạng thái, duyệt giá, thu tiền và điều chuyển.
