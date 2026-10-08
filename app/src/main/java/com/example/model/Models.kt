package com.example.model

import java.util.UUID

enum class UserRole(val label: String) {
  CUSTOMER("Khách hàng"),
  TECHNICIAN("Kỹ thuật viên"),
  BRANCH_MANAGER("Quản lý chi nhánh"),
  HQ_ADMIN("Trụ sở chính")
}

data class User(
  val id: String,
  val name: String,
  val phone: String,
  val email: String,
  val role: UserRole,
  val branchId: String? = null
)

data class TechnicianEmployee(
  val id: String,
  val name: String,
  val specialization: String,
  val branchId: String,
  val activeJobsCount: Int,
  val maxCapacity: Int = 3,
  val kpiScore: Double = 98.5
)

enum class VehicleType(val label: String) {
  CAR("Ô tô"),
  MOTORCYCLE("Xe máy")
}

enum class VerificationStatus(val label: String) {
  PENDING("Chờ xác minh"),
  VERIFIED("Đã xác minh"),
  REJECTED("Từ chối")
}

data class Vehicle(
  val id: String = UUID.randomUUID().toString(),
  val customerId: String,
  val licensePlate: String,
  val make: String,
  val model: String,
  val year: Int,
  val type: VehicleType,
  val color: String,
  val mileageKm: Int,
  val verificationStatus: VerificationStatus = VerificationStatus.VERIFIED,
  val vinNumber: String = ""
)

enum class BranchType(val label: String) {
  DIRECT("Trực thuộc"),
  FRANCHISE("Nhượng quyền")
}

data class Branch(
  val id: String,
  val name: String,
  val address: String,
  val district: String,
  val city: String,
  val type: BranchType,
  val phone: String,
  val maxDailyCapacity: Int = 20,
  val currentActiveJobs: Int = 5,
  val distanceKm: Double = 1.2,
  val rating: Double = 4.9,
  val latitude: Double = 10.7328,
  val longitude: Double = 106.7212,
  val openingHours: String = "08:00 - 18:30",
  val servicesAvailable: List<String> = listOf("Bảo dưỡng định kỳ", "Detailing & Spa", "Sửa chữa chung", "Cân thước lái 3D")
)

enum class ServiceCategory(val label: String) {
  MAINTENANCE("Bảo dưỡng định kỳ"),
  DETAILING("Chăm sóc & Spa xe"),
  REPAIR("Sửa chữa & Thay thế"),
  INSPECTION("Kiểm tra toàn diện")
}

data class ServicePackage(
  val id: String,
  val name: String,
  val description: String,
  val category: ServiceCategory,
  val estimatedMinutes: Int,
  val carPriceVnd: Long,
  val motoPriceVnd: Long,
  val warrantyDays: Int
)

enum class BookingStatus(val label: String) {
  PENDING_CONFIRMATION("Chờ xác nhận"),
  CONFIRMED("Đã xác nhận"),
  VEHICLE_RECEIVED("Đã tiếp nhận xe"),
  INSPECTING("Đang kiểm tra xe"),
  QUOTED("Đã có báo giá"),
  QUOTE_APPROVED("Khách đã duyệt báo giá"),
  IN_SERVICE("Đang thực hiện"),
  QUALITY_CHECK("Kiểm tra chất lượng (QC)"),
  READY_FOR_PICKUP("Sẵn sàng bàn giao"),
  COMPLETED("Hoàn thành & Bàn giao"),
  CANCELLED("Đã hủy")
}

enum class BayType(val label: String) {
  QUICK_SERVICE("Khoang Bảo dưỡng nhanh"),
  DIAGNOSTIC_LIFT("Khoang Nâng Chẩn đoán"),
  CERAMIC_DETAILING("Khoang Phòng sạch Detailing/Ceramic"),
  WHEEL_ALIGNMENT("Khoang Cân chỉnh Thước lái 3D")
}

data class ServiceBay(
  val id: String,
  val bayNumber: Int,
  val name: String,
  val type: BayType,
  val branchId: String,
  val currentBookingId: String? = null,
  val isOccupied: Boolean = false
)

data class Booking(
  val id: String = UUID.randomUUID().toString().take(8).uppercase(),
  val customerId: String,
  val vehicleId: String,
  val branchId: String,
  val servicePackageId: String,
  val appointmentDate: String, // YYYY-MM-DD
  val appointmentTime: String, // HH:mm
  val status: BookingStatus = BookingStatus.PENDING_CONFIRMATION,
  val customerNotes: String = "",
  val createdAtMillis: Long = System.currentTimeMillis(),
  val assignedTechId: String? = null,
  val assignedTechName: String? = null,
  val bayId: String? = null
)

data class InspectionCheckItem(
  val id: String,
  val name: String,
  val category: String,
  val isPassed: Boolean = true,
  val note: String = "",
  val requiresAction: Boolean = false
)

data class QuoteItem(
  val id: String = UUID.randomUUID().toString(),
  val name: String,
  val isPart: Boolean, // true = Phụ tùng, false = Công thợ/Dịch vụ
  val quantity: Int = 1,
  val unitPriceVnd: Long,
  val isApproved: Boolean = true,
  val warrantyDays: Int = 15
) {
  val totalPriceVnd: Long get() = unitPriceVnd * quantity
}

data class Quote(
  val id: String = "BG-" + UUID.randomUUID().toString().take(6).uppercase(),
  val bookingId: String,
  val version: Int = 1,
  val createdAtMillis: Long = System.currentTimeMillis(),
  val validDays: Int = 15,
  val items: List<QuoteItem>,
  val isSignedByCustomer: Boolean = false,
  val customerDecisionTimeMillis: Long? = null
) {
  val totalApprovedAmountVnd: Long
    get() = items.filter { it.isApproved }.sumOf { it.totalPriceVnd }
    
  val totalProposedAmountVnd: Long
    get() = items.sumOf { it.totalPriceVnd }
}

enum class PaymentMethod(val label: String) {
  VIETQR("Chuyển khoản VietQR"),
  CASH("Tiền mặt tại quầy"),
  CARD("Thẻ tín dụng / Ghi nợ"),
  E_WALLET("Ví MoMo / VNPay")
}

data class PaymentTransaction(
  val id: String = "PAY-" + UUID.randomUUID().toString().take(8).uppercase(),
  val bookingId: String,
  val quoteId: String,
  val amountVnd: Long,
  val method: PaymentMethod,
  val isConfirmed: Boolean = false,
  val transactionTimeMillis: Long = System.currentTimeMillis(),
  val note: String = ""
)

data class InventoryItem(
  val id: String,
  val code: String,
  val name: String,
  val branchId: String,
  val actualStock: Int,
  val reservedStock: Int,
  val unitPriceVnd: Long,
  val compatibleVehicles: String
) {
  val availableStock: Int get() = (actualStock - reservedStock).coerceAtLeast(0)
}

data class AuditLog(
  val id: String = UUID.randomUUID().toString(),
  val timestampMillis: Long = System.currentTimeMillis(),
  val actorName: String,
  val actorRole: UserRole,
  val action: String,
  val targetEntity: String,
  val details: String
)

data class FranchiseContract(
  val id: String,
  val branchId: String,
  val branchName: String,
  val franchiseeOwner: String,
  val startDate: String,
  val endDate: String,
  val investmentCapitalVnd: Long,
  val monthlyRoyaltyPercent: Double,
  val currentRevenueVnd: Long,
  val currentExpenseVnd: Long,
  val complianceScore: Int = 98,
  val pendingAuditIssues: Int = 0
)

// --- TÍNH NĂNG MỞ RỘNG MỚI ---

data class ServiceInvoice(
  val invoiceNumber: String = "HD-" + UUID.randomUUID().toString().take(6).uppercase(),
  val issueDate: String,
  val subtotalVnd: Long,
  val vatVnd: Long = (subtotalVnd * 0.08).toLong(), // VAT 8%
  val totalVnd: Long = subtotalVnd + (subtotalVnd * 0.08).toLong(),
  val paymentMethod: PaymentMethod = PaymentMethod.VIETQR,
  val isPaid: Boolean = true,
  val eInvoiceUrl: String = "https://einvoice.ivehicle.vn/view/"
)

data class ServiceHistoryRecord(
  val id: String = "HIST-" + UUID.randomUUID().toString().take(6).uppercase(),
  val vehicleId: String,
  val vehiclePlate: String,
  val vehicleModel: String,
  val branchName: String,
  val serviceDate: String,
  val serviceCategory: ServiceCategory,
  val packageName: String,
  val technicianName: String,
  val mileageKmAtService: Int,
  val performedItems: List<String>,
  val invoice: ServiceInvoice,
  val warrantyDays: Int = 15,
  val customerRating: Int = 5,
  val inspectionPassed: Boolean = true
)

enum class WarrantyStatus(val label: String) {
  ACTIVE("Còn hiệu lực"),
  EXPIRED("Hết hạn"),
  CLAIM_PENDING("Đang xử lý yêu cầu")
}

data class WarrantyRecord(
  val id: String = "WR-" + UUID.randomUUID().toString().take(6).uppercase(),
  val vehiclePlate: String,
  val itemName: String,
  val isPart: Boolean,
  val startDate: String,
  val endDate: String,
  val daysLeft: Int,
  val status: WarrantyStatus = WarrantyStatus.ACTIVE,
  val conditionNote: String = "Bảo hành 1 đổi 1 nếu lỗi kỹ thuật hoặc hao mòn bất thường."
)

enum class ExplanationStatus(val label: String) {
  PENDING_RESPONSE("Chờ đại lý giải trình"),
  RESOLVED("Đã chấp thuận giải trình"),
  ESCALATED("Chuyển hội đồng xử lý")
}

data class ExplanationRequest(
  val id: String = "EXP-" + UUID.randomUUID().toString().take(6).uppercase(),
  val branchId: String,
  val branchName: String,
  val issueTitle: String,
  val contractClause: String,
  val discrepancyDetail: String,
  val deadlineDate: String,
  val status: ExplanationStatus = ExplanationStatus.PENDING_RESPONSE,
  val responseNote: String = ""
)

enum class NotificationChannel(val label: String) {
  FIREBASE_FCM("Firebase Cloud Messaging (FCM)"),
  ZALO_OA("Zalo OA (ZNS)"),
  SMS("SMS Gateway"),
  IN_APP("Thông báo trong App")
}

data class FcmMaintenanceSchedule(
  val id: String = UUID.randomUUID().toString().take(8).uppercase(),
  val vehiclePlate: String,
  val vehicleModel: String,
  val title: String,
  val body: String,
  val scheduledDate: String,
  val targetOdoKm: Int,
  val fcmToken: String = "fcm_token_" + UUID.randomUUID().toString().take(12),
  val isSent: Boolean = false,
  val urgencyLevel: String = "Tiêu chuẩn"
)

enum class NotificationType(val label: String) {
  APPOINTMENT_REMINDER("Nhắc lịch hẹn"),
  SERVICE_PROGRESS("Tiến độ sửa chữa"),
  QUOTE_ALERT("Báo giá chờ duyệt"),
  MAINTENANCE_DUE("Bảo dưỡng định kỳ")
}

data class NotificationMessage(
  val id: String = UUID.randomUUID().toString(),
  val title: String,
  val body: String,
  val type: NotificationType = NotificationType.SERVICE_PROGRESS,
  val channel: NotificationChannel = NotificationChannel.IN_APP,
  val timestampText: String = "Vừa xong",
  val bookingId: String? = null,
  val isRead: Boolean = false,
  val isDelivered: Boolean = true
)

data class StockTransferRequest(
  val id: String = "TRF-" + UUID.randomUUID().toString().take(6).uppercase(),
  val partCode: String,
  val partName: String,
  val fromBranchId: String,
  val toBranchId: String,
  val quantity: Int,
  val status: String = "Đang vận chuyển",
  val createdAt: String
)
