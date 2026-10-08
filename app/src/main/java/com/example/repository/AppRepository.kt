package com.example.repository

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.NumberFormat
import java.util.Locale

object AppRepository {

  fun formatVnd(amount: Long): String {
    val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))
    return "${formatter.format(amount)} đ"
  }

  // Current session user
  private val _currentUser = MutableStateFlow(
    User(
      id = "USR-001",
      name = "Ngô Thành Đạt",
      phone = "0988 123 456",
      email = "ngothanhdat08@gmail.com",
      role = UserRole.CUSTOMER
    )
  )
  val currentUser: StateFlow<User> = _currentUser.asStateFlow()

  fun switchUserRole(role: UserRole) {
    _currentUser.value = when (role) {
      UserRole.CUSTOMER -> User(
        id = "USR-001",
        name = "Ngô Thành Đạt",
        phone = "0988 123 456",
        email = "ngothanhdat08@gmail.com",
        role = UserRole.CUSTOMER
      )
      UserRole.TECHNICIAN -> User(
        id = "TECH-007",
        name = "Trần Văn Hưng (KTV Trưởng)",
        phone = "0912 345 678",
        email = "hung.tran@ivehicle.vn",
        role = UserRole.TECHNICIAN,
        branchId = "BR-01"
      )
      UserRole.BRANCH_MANAGER -> User(
        id = "MGR-002",
        name = "Lê Hoàng Quân (Quản lý CN Q7)",
        phone = "0909 888 777",
        email = "quan.le@ivehicle.vn",
        role = UserRole.BRANCH_MANAGER,
        branchId = "BR-01"
      )
      UserRole.HQ_ADMIN -> User(
        id = "HQ-001",
        name = "Phạm Quang Minh (Giám đốc Vận hành HQ)",
        phone = "0901 000 999",
        email = "minh.pham@ivehicle.vn",
        role = UserRole.HQ_ADMIN
      )
    }
    addAuditLog(
      actorName = _currentUser.value.name,
      actorRole = role,
      action = "ĐỔI_VAI_TRÒ",
      targetEntity = "UserSession",
      details = "Chuyển chế độ trải nghiệm sang ${role.label}"
    )
  }

  // Vehicles
  private val _vehicles = MutableStateFlow(
    listOf(
      Vehicle(
        id = "VEH-1",
        customerId = "USR-001",
        licensePlate = "51K-889.92",
        make = "Porsche",
        model = "Taycan 4S",
        year = 2023,
        type = VehicleType.CAR,
        color = "Xanh Frozen Blue",
        mileageKm = 12450,
        verificationStatus = VerificationStatus.VERIFIED,
        vinNumber = "WP0AA2Y14PSA12984"
      ),
      Vehicle(
        id = "VEH-2",
        customerId = "USR-001",
        licensePlate = "59-A3 999.88",
        make = "Ducati",
        model = "Panigale V4 S",
        year = 2024,
        type = VehicleType.MOTORCYCLE,
        color = "Đỏ Ducati Corse",
        mileageKm = 3200,
        verificationStatus = VerificationStatus.VERIFIED,
        vinNumber = "ZDM14AWW3PB004921"
      ),
      Vehicle(
        id = "VEH-3",
        customerId = "USR-001",
        licensePlate = "51H-456.78",
        make = "Mercedes-Benz",
        model = "GLC 300 4MATIC",
        year = 2022,
        type = VehicleType.CAR,
        color = "Đen Obsidian",
        mileageKm = 28900,
        verificationStatus = VerificationStatus.VERIFIED,
        vinNumber = "WDC2539491F847291"
      )
    )
  )
  val vehicles: StateFlow<List<Vehicle>> = _vehicles.asStateFlow()

  fun addVehicle(vehicle: Vehicle) {
    _vehicles.value = listOf(vehicle) + _vehicles.value
    addAuditLog(
      actorName = _currentUser.value.name,
      actorRole = _currentUser.value.role,
      action = "THÊM_XE",
      targetEntity = "Vehicle",
      details = "Thêm xe ${vehicle.make} ${vehicle.model} biển số ${vehicle.licensePlate}"
    )
  }

  // Branches
  private val _branches = MutableStateFlow(
    listOf(
      Branch(
        id = "BR-01",
        name = "iVehicle Flagship Quận 7",
        address = "120 Nguyễn Thị Thập, P. Tân Hưng",
        district = "Quận 7",
        city = "TP. Hồ Chí Minh",
        type = BranchType.DIRECT,
        phone = "028 3775 8899",
        maxDailyCapacity = 30,
        currentActiveJobs = 14,
        distanceKm = 1.4,
        rating = 4.95,
        latitude = 10.7412,
        longitude = 106.7088,
        openingHours = "08:00 - 19:00",
        servicesAvailable = listOf("Bảo dưỡng định kỳ", "Detailing & Spa", "Sửa chữa chung", "Cân thước lái 3D", "Trạm sạc EV")
      ),
      Branch(
        id = "BR-02",
        name = "iVehicle Premium Thảo Điền",
        address = "88 Quốc Hương, P. Thảo Điền, TP. Thủ Đức",
        district = "Thủ Đức",
        city = "TP. Hồ Chí Minh",
        type = BranchType.DIRECT,
        phone = "028 3620 1122",
        maxDailyCapacity = 25,
        currentActiveJobs = 8,
        distanceKm = 6.8,
        rating = 4.9,
        latitude = 10.8035,
        longitude = 106.7321,
        openingHours = "08:00 - 18:30",
        servicesAvailable = listOf("Bảo dưỡng định kỳ", "Phủ Ceramic 9H", "Chẩn đoán xe Đức & EV", "Phòng chờ VIP")
      ),
      Branch(
        id = "BR-03",
        name = "iVehicle AutoCare Tân Bình (Nhượng quyền)",
        address = "45 Trường Sơn, P. 2",
        district = "Tân Bình",
        city = "TP. Hồ Chí Minh",
        type = BranchType.FRANCHISE,
        phone = "028 3848 6677",
        maxDailyCapacity = 20,
        currentActiveJobs = 9,
        distanceKm = 8.5,
        rating = 4.8,
        latitude = 10.8123,
        longitude = 106.6645,
        openingHours = "07:30 - 18:00",
        servicesAvailable = listOf("Bảo dưỡng nhanh 60 phút", "Thay nhớt & Lọc gió", "Sửa chữa hệ thống treo", "Cứu hộ sân bay")
      ),
      Branch(
        id = "BR-04",
        name = "iVehicle Tây Hồ Center (Hà Nội)",
        address = "218 Lạc Long Quân, P. Bưởi",
        district = "Tây Hồ",
        city = "Hà Nội",
        type = BranchType.DIRECT,
        phone = "024 3719 5566",
        maxDailyCapacity = 25,
        currentActiveJobs = 6,
        distanceKm = 1140.0,
        rating = 4.92,
        latitude = 21.0478,
        longitude = 105.8112,
        openingHours = "08:00 - 18:30",
        servicesAvailable = listOf("Bảo dưỡng định kỳ", "Phủ Ceramic", "Cân thước lái Hunter 3D")
      )
    )
  )
  val branches: StateFlow<List<Branch>> = _branches.asStateFlow()

  // Function to calculate distance between coordinates (Haversine formula in km)
  fun calculateDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val r = 6371.0 // Radius of the Earth in km
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
        Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
        Math.sin(dLon / 2) * Math.sin(dLon / 2)
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    return Math.round(r * c * 10.0) / 10.0
  }

  // Service Packages
  private val _services = MutableStateFlow(
    listOf(
      ServicePackage(
        id = "SRV-01",
        name = "Bảo Dưỡng Cấp Cao (Major Service)",
        description = "Kiểm tra 48 hạng mục động cơ, phanh, lọc gió, dung dịch làm mát & hệ thống truyền động.",
        category = ServiceCategory.MAINTENANCE,
        estimatedMinutes = 120,
        carPriceVnd = 2450000,
        motoPriceVnd = 1150000,
        warrantyDays = 15
      ),
      ServicePackage(
        id = "SRV-02",
        name = "Phủ Ceramic & Liquid Glass Detailing",
        description = "Đánh bóng 3 bước xóa xước xoáy, phủ lớp thủy tinh lỏng bảo vệ sơn 9H siêu bóng gương.",
        category = ServiceCategory.DETAILING,
        estimatedMinutes = 240,
        carPriceVnd = 4800000,
        motoPriceVnd = 2200000,
        warrantyDays = 30
      ),
      ServicePackage(
        id = "SRV-03",
        name = "Chăm Sóc & Vệ Sinh Khoang Máy Siêu Âm",
        description = "Làm sạch khoang máy bằng CO2 khô/hơi nước nóng, dưỡng phục hồi chi tiết cao su & điện tử.",
        category = ServiceCategory.DETAILING,
        estimatedMinutes = 90,
        carPriceVnd = 950000,
        motoPriceVnd = 450000,
        warrantyDays = 7
      ),
      ServicePackage(
        id = "SRV-04",
        name = "Cân Chỉnh Thước Lái & Cân Mâm Điện Tử",
        description = "Sử dụng máy Hunter 3D Hoa Kỳ chuẩn xác đến từng milimet, chống mòn lốp và lệch lái.",
        category = ServiceCategory.REPAIR,
        estimatedMinutes = 60,
        carPriceVnd = 850000,
        motoPriceVnd = 350000,
        warrantyDays = 15
      )
    )
  )
  val services: StateFlow<List<ServicePackage>> = _services.asStateFlow()

  // Technicians
  private val _technicians = MutableStateFlow(
    listOf(
      TechnicianEmployee("TECH-007", "Trần Văn Hưng", "Chẩn đoán Động cơ & Khung gầm", "BR-01", activeJobsCount = 2, maxCapacity = 3, kpiScore = 98.8),
      TechnicianEmployee("TECH-008", "Vũ Minh Quân", "Hệ thống Điện & Pin Hybrid/EV", "BR-01", activeJobsCount = 1, maxCapacity = 3, kpiScore = 96.5),
      TechnicianEmployee("TECH-009", "Nguyễn Quốc Bảo", "Chăm sóc sơn Ceramic & Spa Detailing", "BR-02", activeJobsCount = 1, maxCapacity = 2, kpiScore = 99.0),
      TechnicianEmployee("TECH-010", "Đặng Hoàng Nam", "Kiểm định 3D & Thước lái", "BR-01", activeJobsCount = 0, maxCapacity = 3, kpiScore = 97.2)
    )
  )
  val technicians: StateFlow<List<TechnicianEmployee>> = _technicians.asStateFlow()

  // Service Bays (Khoang sửa chữa xưởng)
  private val _serviceBays = MutableStateFlow(
    listOf(
      ServiceBay("BAY-01", 1, "Khoang 01 (Bảo Dưỡng Nhanh)", BayType.QUICK_SERVICE, "BR-01", currentBookingId = "IV-8821", isOccupied = true),
      ServiceBay("BAY-02", 2, "Khoang 02 (Cầu Nâng Điện & EV)", BayType.DIAGNOSTIC_LIFT, "BR-01", currentBookingId = "IV-7490", isOccupied = true),
      ServiceBay("BAY-03", 3, "Khoang 03 (Phòng Sạch Ceramic)", BayType.CERAMIC_DETAILING, "BR-01", currentBookingId = null, isOccupied = false),
      ServiceBay("BAY-04", 4, "Khoang 04 (Cân Chỉnh Thước Lái 3D)", BayType.WHEEL_ALIGNMENT, "BR-01", currentBookingId = null, isOccupied = false),
      ServiceBay("BAY-05", 5, "Khoang 05 (Động Cơ & Hộp Số)", BayType.DIAGNOSTIC_LIFT, "BR-01", currentBookingId = null, isOccupied = false)
    )
  )
  val serviceBays: StateFlow<List<ServiceBay>> = _serviceBays.asStateFlow()

  fun reassignTaskToBay(bookingId: String, newBayId: String) {
    val bay = _serviceBays.value.find { it.id == newBayId } ?: return
    _serviceBays.value = _serviceBays.value.map { b ->
      when {
        b.id == newBayId -> b.copy(isOccupied = true, currentBookingId = bookingId)
        b.currentBookingId == bookingId -> b.copy(isOccupied = false, currentBookingId = null)
        else -> b
      }
    }
    _bookings.value = _bookings.value.map { b ->
      if (b.id == bookingId) b.copy(bayId = newBayId) else b
    }
    addAuditLog(
      actorName = _currentUser.value.name,
      actorRole = _currentUser.value.role,
      action = "ĐIỀU_CHUYỂN_KHOANG",
      targetEntity = "ServiceBay",
      details = "Quản lý điều chuyển xe đơn #$bookingId sang ${bay.name}"
    )
  }

  fun assignTechnician(bookingId: String, techId: String, reason: String = "Điều phối theo tải và chuyên môn") {
    val tech = _technicians.value.find { it.id == techId } ?: return
    _bookings.value = _bookings.value.map { b ->
      if (b.id == bookingId) b.copy(
        assignedTechId = tech.id,
        assignedTechName = tech.name
      ) else b
    }
    // Update active count
    _technicians.value = _technicians.value.map { t ->
      if (t.id == techId) t.copy(activeJobsCount = t.activeJobsCount + 1) else t
    }
    addAuditLog(
      actorName = _currentUser.value.name,
      actorRole = _currentUser.value.role,
      action = "PHÂN_CÔNG_KTV",
      targetEntity = "Booking",
      details = "Phân công KTV ${tech.name} cho xe lệnh #$bookingId. Lý do: $reason"
    )
  }

  // Bookings
  private val _bookings = MutableStateFlow(
    listOf(
      Booking(
        id = "IV-8821",
        customerId = "USR-001",
        vehicleId = "VEH-1",
        branchId = "BR-01",
        servicePackageId = "SRV-01",
        appointmentDate = "2026-10-12",
        appointmentTime = "09:30",
        status = BookingStatus.QUOTED,
        customerNotes = "Nhờ kiểm tra thêm tiếng ồn nhẹ khi phanh ở tốc độ chậm.",
        assignedTechId = "TECH-007",
        assignedTechName = "Trần Văn Hưng"
      ),
      Booking(
        id = "IV-7490",
        customerId = "USR-001",
        vehicleId = "VEH-2",
        branchId = "BR-02",
        servicePackageId = "SRV-02",
        appointmentDate = "2026-10-18",
        appointmentTime = "14:00",
        status = BookingStatus.CONFIRMED,
        customerNotes = "Cần phủ bóng kĩ phần gắp sau và bình xăng.",
        assignedTechId = "TECH-009",
        assignedTechName = "Nguyễn Quốc Bảo"
      ),
      Booking(
        id = "IV-5512",
        customerId = "USR-001",
        vehicleId = "VEH-3",
        branchId = "BR-01",
        servicePackageId = "SRV-03",
        appointmentDate = "2026-09-25",
        appointmentTime = "10:00",
        status = BookingStatus.COMPLETED,
        customerNotes = "Bảo dưỡng trước chuyến đi công tác.",
        assignedTechId = "TECH-007",
        assignedTechName = "Trần Văn Hưng"
      )
    )
  )
  val bookings: StateFlow<List<Booking>> = _bookings.asStateFlow()

  fun createBooking(
    vehicleId: String,
    branchId: String,
    servicePackageId: String,
    appointmentDate: String,
    appointmentTime: String,
    notes: String
  ): Booking {
    val newBooking = Booking(
      customerId = _currentUser.value.id,
      vehicleId = vehicleId,
      branchId = branchId,
      servicePackageId = servicePackageId,
      appointmentDate = appointmentDate,
      appointmentTime = appointmentTime,
      status = BookingStatus.PENDING_CONFIRMATION,
      customerNotes = notes
    )
    _bookings.value = listOf(newBooking) + _bookings.value
    addAuditLog(
      actorName = _currentUser.value.name,
      actorRole = _currentUser.value.role,
      action = "ĐẶT_LỊCH_HẸN",
      targetEntity = "Booking",
      details = "Tạo lịch hẹn mã #${newBooking.id} ngày $appointmentDate lúc $appointmentTime"
    )
    // Tự động đẩy thông báo xác nhận lịch hẹn vào Notification System
    pushNotification(
      title = "Đặt lịch hẹn thành công #${newBooking.id}",
      body = "Lịch hẹn ngày $appointmentDate lúc $appointmentTime đã được tiếp nhận. Chúng tôi sẽ nhắc bạn trước 2 giờ.",
      type = NotificationType.APPOINTMENT_REMINDER,
      channel = NotificationChannel.IN_APP,
      bookingId = newBooking.id
    )
    return newBooking
  }

  fun updateBookingStatus(bookingId: String, newStatus: BookingStatus, reason: String = "") {
    _bookings.value = _bookings.value.map { b ->
      if (b.id == bookingId) b.copy(status = newStatus) else b
    }
    addAuditLog(
      actorName = _currentUser.value.name,
      actorRole = _currentUser.value.role,
      action = "CHUYỂN_TRẠNG_THÁI_LỊCH",
      targetEntity = "Booking",
      details = "Lịch #${bookingId} sang trạng thái '${newStatus.label}'. Ghi chú: $reason"
    )

    // Tự động kích hoạt Notification cảnh báo tiến độ bảo dưỡng xe
    val notifMessage = when (newStatus) {
      BookingStatus.VEHICLE_RECEIVED -> "Xưởng đã tiếp nhận xe #${bookingId} và bàn giao cho KTV chuẩn bị kiểm tra."
      BookingStatus.INSPECTING -> "KTV đang thực hiện quy trình kiểm tra 6 hạng mục kỹ thuật cho xe #${bookingId}."
      BookingStatus.QUOTED -> "Đã có báo giá chi tiết cho đơn #${bookingId}. Vui lòng mở ứng dụng xem và phê duyệt hạng mục."
      BookingStatus.QUOTE_APPROVED -> "Báo giá đơn #${bookingId} đã được phê duyệt. Phụ tùng chính hãng đã được giữ chỗ."
      BookingStatus.IN_SERVICE -> "Xe #${bookingId} đang được kỹ thuật viên thi công sửa chữa & thay thế phụ tùng."
      BookingStatus.QUALITY_CHECK -> "KTV đã hoàn tất công việc. Xe #${bookingId} đang trong quy trình kiểm tra chất lượng (QC)."
      BookingStatus.READY_FOR_PICKUP -> "Xe #${bookingId} đã sẵn sàng bàn giao! Vui lòng đến xưởng nhận xe và kiểm tra nghiệm thu."
      BookingStatus.COMPLETED -> "Đơn #${bookingId} đã hoàn tất và bàn giao xe thành công. Chính sách bảo hành đã được kích hoạt."
      BookingStatus.CANCELLED -> "Lịch hẹn #${bookingId} đã được hủy theo yêu cầu."
      else -> null
    }

    if (notifMessage != null) {
      pushNotification(
        title = "Cập nhật tiến độ: ${newStatus.label}",
        body = notifMessage,
        type = NotificationType.SERVICE_PROGRESS,
        channel = NotificationChannel.IN_APP,
        bookingId = bookingId
      )
    }
  }

  // Quotes
  private val _quotes = MutableStateFlow(
    listOf(
      Quote(
        id = "BG-882101",
        bookingId = "IV-8821",
        version = 1,
        items = listOf(
          QuoteItem(
            name = "Gói Bảo Dưỡng Cấp Cao 48 Hạng Mục",
            isPart = false,
            quantity = 1,
            unitPriceVnd = 2450000,
            isApproved = true,
            warrantyDays = 15
          ),
          QuoteItem(
            name = "Dầu Tổng Hợp Toàn Phần Cao Cấp Mobil 1 (Can 4L)",
            isPart = true,
            quantity = 2,
            unitPriceVnd = 850000,
            isApproved = true,
            warrantyDays = 90
          ),
          QuoteItem(
            name = "Lọc Gió Cabin Than Hoạt Tính Kháng Khuẩn",
            isPart = true,
            quantity = 1,
            unitPriceVnd = 450000,
            isApproved = true,
            warrantyDays = 180
          ),
          QuoteItem(
            name = "Thay Cụm Bố Thắng Trước Gốm Carbon (Khuyến nghị do mòn >75%)",
            isPart = true,
            quantity = 1,
            unitPriceVnd = 3200000,
            isApproved = false,
            warrantyDays = 180
          )
        ),
        isSignedByCustomer = false
      )
    )
  )
  val quotes: StateFlow<List<Quote>> = _quotes.asStateFlow()

  fun toggleQuoteItemApproval(quoteId: String, itemId: String) {
    _quotes.value = _quotes.value.map { q ->
      if (q.id == quoteId) {
        val updatedItems = q.items.map { item ->
          if (item.id == itemId) item.copy(isApproved = !item.isApproved) else item
        }
        q.copy(items = updatedItems)
      } else q
    }
  }

  fun approveQuote(quoteId: String) {
    val quote = _quotes.value.find { it.id == quoteId } ?: return
    _quotes.value = _quotes.value.map { q ->
      if (q.id == quoteId) {
        q.copy(isSignedByCustomer = true, customerDecisionTimeMillis = System.currentTimeMillis())
      } else q
    }
    // Update booking state
    updateBookingStatus(quote.bookingId, BookingStatus.QUOTE_APPROVED, "Khách hàng đã ký duyệt báo giá trực tuyến")
    
    // Automatically hold stock for approved parts
    quote.items.filter { it.isPart && it.isApproved }.forEach { item ->
      reserveStock(item.name, item.quantity)
    }

    addAuditLog(
      actorName = _currentUser.value.name,
      actorRole = _currentUser.value.role,
      action = "DUYỆT_BÁO_GIÁ",
      targetEntity = "Quote",
      details = "Khách duyệt báo giá #${quoteId} tổng số tiền ${formatVnd(quote.totalApprovedAmountVnd)}"
    )
  }

  // Inventory
  private val _inventory = MutableStateFlow(
    listOf(
      InventoryItem("INV-01", "OIL-MOBIL-1", "Dầu Tổng Hợp Toàn Phần Mobil 1", "BR-01", actualStock = 45, reservedStock = 6, unitPriceVnd = 850000, "Ô tô xăng/hybrid"),
      InventoryItem("INV-02", "FIL-CABIN-C1", "Lọc Gió Cabin Than Hoạt Tính", "BR-01", actualStock = 28, reservedStock = 4, unitPriceVnd = 450000, "Porsche / Mercedes / Audi"),
      InventoryItem("INV-03", "BRK-PAD-CER", "Bố Thắng Gốm Carbon Ceramic", "BR-01", actualStock = 12, reservedStock = 2, unitPriceVnd = 3200000, "Porsche Taycan / Panamera"),
      InventoryItem("INV-04", "MOT-SPARK-IR", "Bugi Iridium Cao Cấp NGK", "BR-01", actualStock = 60, reservedStock = 10, unitPriceVnd = 280000, "Xe máy & Ô tô thể thao"),
      InventoryItem("INV-05", "CER-COAT-9H", "Chai Phủ Thủy Tinh Liquid Glass 9H", "BR-01", actualStock = 15, reservedStock = 3, unitPriceVnd = 1200000, "Toàn bộ xe")
    )
  )
  val inventory: StateFlow<List<InventoryItem>> = _inventory.asStateFlow()

  private fun reserveStock(partName: String, quantity: Int) {
    _inventory.value = _inventory.value.map { item ->
      if (item.name.contains(partName, ignoreCase = true) || partName.contains(item.name, ignoreCase = true)) {
        item.copy(reservedStock = item.reservedStock + quantity)
      } else item
    }
  }

  // Payments
  private val _payments = MutableStateFlow(
    listOf(
      PaymentTransaction(
        id = "PAY-9921",
        bookingId = "IV-5512",
        quoteId = "BG-551201",
        amountVnd = 950000,
        method = PaymentMethod.VIETQR,
        isConfirmed = true,
        transactionTimeMillis = System.currentTimeMillis() - 86400000L * 12,
        note = "Thanh toán qua mã VietQR thành công"
      )
    )
  )
  val payments: StateFlow<List<PaymentTransaction>> = _payments.asStateFlow()

  fun confirmPayment(bookingId: String, quoteId: String, amount: Long, method: PaymentMethod): PaymentTransaction {
    val transaction = PaymentTransaction(
      bookingId = bookingId,
      quoteId = quoteId,
      amountVnd = amount,
      method = method,
      isConfirmed = true,
      note = "Xác nhận đối soát thành công qua cổng ${method.label}"
    )
    _payments.value = listOf(transaction) + _payments.value
    updateBookingStatus(bookingId, BookingStatus.IN_SERVICE, "Đã hoàn tất thanh toán - Bắt đầu điều phối thực hiện")
    addAuditLog(
      actorName = _currentUser.value.name,
      actorRole = _currentUser.value.role,
      action = "THANH_TOÁN_THÀNH_CÔNG",
      targetEntity = "Payment",
      details = "Giao dịch #${transaction.id} số tiền ${formatVnd(amount)} qua ${method.label}"
    )
    return transaction
  }

  // Inspection Check Items
  private val _inspections = MutableStateFlow(
    listOf(
      InspectionCheckItem("CHK-01", "Kiểm tra tình trạng áp suất & độ mòn 4 lốp", "Khung gầm & Lốp", isPassed = true, "Áp suất đạt chuẩn 2.4 bar"),
      InspectionCheckItem("CHK-02", "Kiểm tra độ dày bố thắng & đĩa phanh", "Hệ thống phanh", isPassed = false, "Bố thắng trước mòn còn 25%, khuyến nghị thay thế", requiresAction = true),
      InspectionCheckItem("CHK-03", "Mức dung dịch làm mát & nước rửa kính", "Động cơ & Dung dịch", isPassed = true, "Đã châm thêm đầy đủ"),
      InspectionCheckItem("CHK-04", "Điện áp bình ắc quy / Pin cao áp Hybrid", "Hệ thống điện", isPassed = true, "12.8V - Sức khỏe pin 96%"),
      InspectionCheckItem("CHK-05", "Đèn pha, xi-nhan và đèn hậu", "Hệ thống chiếu sáng", isPassed = true, "Hoạt động bình thường"),
      InspectionCheckItem("CHK-06", "Kiểm tra gầm xe có dấu hiệu rò rỉ dầu/dung dịch", "Gầm xe", isPassed = true, "Khô ráo sạch sẽ")
    )
  )
  val inspections: StateFlow<List<InspectionCheckItem>> = _inspections.asStateFlow()

  fun toggleInspectionItem(itemId: String, passed: Boolean, note: String) {
    _inspections.value = _inspections.value.map { item ->
      if (item.id == itemId) item.copy(isPassed = passed, note = note) else item
    }
  }

  // Franchise Contracts
  private val _franchiseContracts = MutableStateFlow(
    listOf(
      FranchiseContract(
        id = "FC-2025-03",
        branchId = "BR-03",
        branchName = "iVehicle AutoCare Tân Bình",
        franchiseeOwner = "Công ty TNHH Đầu tư Dịch vụ Hoàng Gia Auto",
        startDate = "2025-01-01",
        endDate = "2029-12-31",
        investmentCapitalVnd = 4500000000L,
        monthlyRoyaltyPercent = 6.5,
        currentRevenueVnd = 428000000L,
        currentExpenseVnd = 285000000L,
        complianceScore = 96,
        pendingAuditIssues = 1
      )
    )
  )
  val franchiseContracts: StateFlow<List<FranchiseContract>> = _franchiseContracts.asStateFlow()

  // Audit Logs
  private val _auditLogs = MutableStateFlow(
    listOf(
      AuditLog(
        timestampMillis = System.currentTimeMillis() - 7200000L,
        actorName = "Lê Hoàng Quân",
        actorRole = UserRole.BRANCH_MANAGER,
        action = "DUYỆT_LỊCH_HẸN",
        targetEntity = "Booking",
        details = "Xác nhận lịch hẹn IV-8821 cho xe Porsche Taycan 4S"
      ),
      AuditLog(
        timestampMillis = System.currentTimeMillis() - 5400000L,
        actorName = "Trần Văn Hưng",
        actorRole = UserRole.TECHNICIAN,
        action = "HOÀN_TẤT_CHECKLIST",
        targetEntity = "Inspection",
        details = "Kiểm tra hoàn thành 6/6 hạng mục, cảnh báo bố thắng trước mòn"
      )
    )
  )
  val auditLogs: StateFlow<List<AuditLog>> = _auditLogs.asStateFlow()

  fun addAuditLog(actorName: String, actorRole: UserRole, action: String, targetEntity: String, details: String) {
    val log = AuditLog(
      actorName = actorName,
      actorRole = actorRole,
      action = action,
      targetEntity = targetEntity,
      details = details
    )
    _auditLogs.value = listOf(log) + _auditLogs.value
  }

  // --- MỞ RỘNG TÍNH NĂNG: LỊCH SỬ BẢO DƯỠNG & HÓA ĐƠN TỪNG PHƯƠNG TIỆN ---
  private val _serviceHistoryRecords = MutableStateFlow(
    listOf(
      ServiceHistoryRecord(
        id = "HIST-0925",
        vehicleId = "VEH-1",
        vehiclePlate = "51K-123.45",
        vehicleModel = "Toyota Corolla Cross",
        branchName = "iVehicle Quận 7",
        serviceDate = "25/09/2026",
        serviceCategory = ServiceCategory.MAINTENANCE,
        packageName = "Bảo Dưỡng Cấp Cao 48 Hạng Mục (Mốc 10.000 km)",
        technicianName = "Trần Văn Hưng",
        mileageKmAtService = 10250,
        performedItems = listOf(
          "Thay dầu nhớt động cơ toàn phần Mobil 1 0W-20 (4L)",
          "Thay lọc nhớt chính hãng Toyota Denso",
          "Vệ sinh buồng đốt và bướm ga điện tử",
          "Cân chỉnh áp suất lốp & siết lực tắc kê 4 bánh",
          "Kiểm tra ắc quy & hệ thống nạp điện"
        ),
        invoice = ServiceInvoice(
          invoiceNumber = "HD-2026-8821",
          issueDate = "25/09/2026",
          subtotalVnd = 2450000,
          paymentMethod = PaymentMethod.VIETQR,
          isPaid = true
        ),
        warrantyDays = 15,
        customerRating = 5,
        inspectionPassed = true
      ),
      ServiceHistoryRecord(
        id = "HIST-0618",
        vehicleId = "VEH-1",
        vehiclePlate = "51K-123.45",
        vehicleModel = "Toyota Corolla Cross",
        branchName = "iVehicle Quận 7",
        serviceDate = "18/06/2026",
        serviceCategory = ServiceCategory.MAINTENANCE,
        packageName = "Bảo Dưỡng Tiêu Chuẩn 24 Hạng Mục (Mốc 5.000 km)",
        technicianName = "Vũ Minh Quân",
        mileageKmAtService = 5120,
        performedItems = listOf(
          "Thay dầu nhớt tổng hợp cao cấp Mobil 1",
          "Kiểm tra mức nước làm mát & dầu thắng",
          "Vệ sinh lọc gió động cơ & lọc gió cabin",
          "Đảo lốp và kiểm tra độ mòn gai lốp"
        ),
        invoice = ServiceInvoice(
          invoiceNumber = "HD-2026-4412",
          issueDate = "18/06/2026",
          subtotalVnd = 1250000,
          paymentMethod = PaymentMethod.CASH,
          isPaid = true
        ),
        warrantyDays = 15,
        customerRating = 5,
        inspectionPassed = true
      ),
      ServiceHistoryRecord(
        id = "HIST-0810",
        vehicleId = "VEH-2",
        vehiclePlate = "59-A3 999.88",
        vehicleModel = "Honda SH 160i ABS",
        branchName = "iVehicle Thảo Điền",
        serviceDate = "10/08/2026",
        serviceCategory = ServiceCategory.DETAILING,
        packageName = "Chăm Sóc Toàn Diện & Phủ Thủy Tinh Ceramic 9H",
        technicianName = "Nguyễn Quốc Bảo",
        mileageKmAtService = 3400,
        performedItems = listOf(
          "Rửa chi tiết khoang máy & gầm xe bằng hơi nước nóng",
          "Đánh bóng hiệu chỉnh bề mặt sơn 3 bước",
          "Phủ bảo vệ Ceramic 9H hai lớp dàn áo và vành mâm",
          "Bảo dưỡng dây curoa và vệ sinh nồi xe tay ga"
        ),
        invoice = ServiceInvoice(
          invoiceNumber = "HD-2026-6631",
          issueDate = "10/08/2026",
          subtotalVnd = 1850000,
          paymentMethod = PaymentMethod.E_WALLET,
          isPaid = true
        ),
        warrantyDays = 30,
        customerRating = 5,
        inspectionPassed = true
      ),
      ServiceHistoryRecord(
        id = "HIST-0705",
        vehicleId = "VEH-3",
        vehiclePlate = "51K-889.92",
        vehicleModel = "Porsche Taycan 4S",
        branchName = "iVehicle Quận 7",
        serviceDate = "05/07/2026",
        serviceCategory = ServiceCategory.REPAIR,
        packageName = "Cân Chỉnh Thước Lái 3D & Cân Mâm Điện Tử",
        technicianName = "Đặng Hoàng Nam",
        mileageKmAtService = 8200,
        performedItems = listOf(
          "Đo và cân chỉnh góc đặt bánh xe máy Hunter 3D Hoa Kỳ",
          "Cân bằng động điện tử 4 bánh xe thể thao",
          "Hiệu chuẩn cảm biến góc lái điện tử EPS",
          "Kiểm tra hệ thống treo khí nén chủ động PASM"
        ),
        invoice = ServiceInvoice(
          invoiceNumber = "HD-2026-7789",
          issueDate = "05/07/2026",
          subtotalVnd = 1500000,
          paymentMethod = PaymentMethod.CARD,
          isPaid = true
        ),
        warrantyDays = 15,
        customerRating = 5,
        inspectionPassed = true
      )
    )
  )
  val serviceHistoryRecords: StateFlow<List<ServiceHistoryRecord>> = _serviceHistoryRecords.asStateFlow()

  // --- MỞ RỘNG TÍNH NĂNG: BẢO HÀNH (WARRANTY) ---
  private val _warranties = MutableStateFlow(
    listOf(
      WarrantyRecord(
        id = "WR-9982",
        vehiclePlate = "51K-123.45",
        itemName = "Gói Bảo Dưỡng Cấp Cao 48 Hạng Mục",
        isPart = false,
        startDate = "2026-09-25",
        endDate = "2026-10-10",
        daysLeft = 2,
        status = WarrantyStatus.ACTIVE,
        conditionNote = "Bảo hành tiêu chuẩn kỹ thuật 15 ngày kể từ ngày nghiệm thu bàn giao."
      ),
      WarrantyRecord(
        id = "WR-8841",
        vehiclePlate = "51K-123.45",
        itemName = "Lọc Gió Cabin Than Hoạt Tính Kháng Khuẩn",
        isPart = true,
        startDate = "2026-08-15",
        endDate = "2027-02-15",
        daysLeft = 130,
        status = WarrantyStatus.ACTIVE,
        conditionNote = "Bảo hành phụ tùng 180 ngày lỗi từ nhà sản xuất."
      ),
      WarrantyRecord(
        id = "WR-7712",
        vehiclePlate = "51K-889.92",
        itemName = "Phủ Thủy Tinh Liquid Glass 9H",
        isPart = false,
        startDate = "2026-06-01",
        endDate = "2026-07-01",
        daysLeft = 0,
        status = WarrantyStatus.EXPIRED,
        conditionNote = "Bảo hành độ bóng gương và chống xước 30 ngày (đã hoàn thành)."
      )
    )
  )
  val warranties: StateFlow<List<WarrantyRecord>> = _warranties.asStateFlow()

  // --- MỞ RỘNG TÍNH NĂNG: YÊU CẦU GIẢI TRÌNH NHƯỢNG QUYỀN (EXPLANATION REQUESTS) ---
  private val _explanationRequests = MutableStateFlow(
    listOf(
      ExplanationRequest(
        id = "EXP-2026-01",
        branchId = "BR-03",
        branchName = "iVehicle AutoCare Tân Bình",
        issueTitle = "Chênh lệch tồn phụ tùng dầu Mobil 1 so với báo cáo doanh thu",
        contractClause = "Điều 8.2: Quy chế kiểm kê và sử dụng phụ tùng chính hãng",
        discrepancyDetail = "Hệ thống ghi nhận xuất 25 can dầu nhưng doanh thu chỉ báo 22 can. Cần giải trình 3 can thất thoát.",
        deadlineDate = "2026-10-12",
        status = ExplanationStatus.PENDING_RESPONSE
      )
    )
  )
  val explanationRequests: StateFlow<List<ExplanationRequest>> = _explanationRequests.asStateFlow()

  fun submitExplanationResponse(requestId: String, note: String) {
    _explanationRequests.value = _explanationRequests.value.map { req ->
      if (req.id == requestId) req.copy(
        status = ExplanationStatus.RESOLVED,
        responseNote = note
      ) else req
    }
    addAuditLog(
      actorName = _currentUser.value.name,
      actorRole = _currentUser.value.role,
      action = "PHẢN_HỒI_GIẢI_TRÌNH",
      targetEntity = "ExplanationRequest",
      details = "Chi nhánh gửi giải trình cho yêu cầu $requestId: $note"
    )
  }

  // --- MỞ RỘNG TÍNH NĂNG: THÔNG BÁO & HẬU MÃI (ZALO OA / SMS / IN-APP) ---
  private val _notifications = MutableStateFlow(
    listOf(
      NotificationMessage(
        title = "Nhắc bảo dưỡng định kỳ",
        body = "Xe Toyota Corolla Cross (51K-123.45) đã đến mốc 15.000 km. Hãy đặt lịch bảo dưỡng để đảm bảo an toàn.",
        type = NotificationType.MAINTENANCE_DUE,
        channel = NotificationChannel.ZALO_OA,
        timestampText = "10 phút trước"
      ),
      NotificationMessage(
        title = "Báo giá mới đã sẵn sàng",
        body = "Kỹ thuật viên Trần Văn Hưng đã lập báo giá BG-882101 cho xe của bạn. Vui lòng xem và phê duyệt.",
        type = NotificationType.QUOTE_ALERT,
        channel = NotificationChannel.IN_APP,
        timestampText = "1 giờ trước",
        bookingId = "IV-8821"
      ),
      NotificationMessage(
        title = "Xác nhận lịch hẹn thành công",
        body = "Lịch hẹn IV-2026-889 lúc 09:30 ngày 09/10 tại iVehicle Quận 7 đã được tiếp nhận.",
        type = NotificationType.APPOINTMENT_REMINDER,
        channel = NotificationChannel.SMS,
        timestampText = "Hôm qua",
        bookingId = "IV-2026-889"
      )
    )
  )
  val notifications: StateFlow<List<NotificationMessage>> = _notifications.asStateFlow()

  fun pushNotification(
    title: String,
    body: String,
    type: NotificationType,
    channel: NotificationChannel = NotificationChannel.IN_APP,
    bookingId: String? = null
  ) {
    val notif = NotificationMessage(
      title = title,
      body = body,
      type = type,
      channel = channel,
      timestampText = "Vừa xong",
      bookingId = bookingId,
      isRead = false,
      isDelivered = true
    )
    _notifications.value = listOf(notif) + _notifications.value
  }

  fun markNotificationAsRead(id: String) {
    _notifications.value = _notifications.value.map { n ->
      if (n.id == id) n.copy(isRead = true) else n
    }
  }

  // --- MỞ RỘNG TÍNH NĂNG: FIREBASE CLOUD MESSAGING (FCM) MAINTENANCE REMINDERS ---
  private val _fcmMaintenanceSchedules = MutableStateFlow(
    listOf(
      FcmMaintenanceSchedule(
        vehiclePlate = "51K-123.45",
        vehicleModel = "Toyota Corolla Cross",
        title = "Nhắc bảo dưỡng định kỳ 15.000 km",
        body = "Xe của bạn sắp đạt mốc 15.000 km. Hãy đặt lịch trước để KTV kiểm tra dầu nhớt, hệ thống phanh và lọc gió.",
        scheduledDate = "15/10/2026",
        targetOdoKm = 15000,
        urgencyLevel = "Cần bảo dưỡng sớm"
      ),
      FcmMaintenanceSchedule(
        vehiclePlate = "59-A3 999.88",
        vehicleModel = "Honda SH 160i ABS",
        title = "Nhắc bảo dưỡng định kỳ 5.000 km",
        body = "Đã đến chu kỳ bảo dưỡng 6 tháng hoặc 5.000 km: Kiểm tra dây curoa, dầu láp và nước làm mát.",
        scheduledDate = "20/10/2026",
        targetOdoKm = 5000,
        urgencyLevel = "Định kỳ"
      ),
      FcmMaintenanceSchedule(
        vehiclePlate = "51K-889.92",
        vehicleModel = "Porsche Taycan 4S",
        title = "Nhắc kiểm định hệ thống Pin EV & Hệ thống làm mát",
        body = "Chu kỳ kiểm định an toàn pin điện áp cao EV và cân chỉnh thước lái Hunter 3D.",
        scheduledDate = "28/10/2026",
        targetOdoKm = 10000,
        urgencyLevel = "Khuyến nghị"
      )
    )
  )
  val fcmMaintenanceSchedules: StateFlow<List<FcmMaintenanceSchedule>> = _fcmMaintenanceSchedules.asStateFlow()

  // Dispatch FCM Push Notification to User Device
  fun dispatchFcmMaintenanceReminder(scheduleId: String) {
    val schedule = _fcmMaintenanceSchedules.value.find { it.id == scheduleId } ?: return

    // Update schedule state
    _fcmMaintenanceSchedules.value = _fcmMaintenanceSchedules.value.map { s ->
      if (s.id == scheduleId) s.copy(isSent = true) else s
    }

    // Push into Notifications Stream with FIREBASE_FCM Channel
    pushNotification(
      title = "🔔 [FCM Cloud] ${schedule.title}",
      body = "${schedule.vehiclePlate} (${schedule.vehicleModel}): ${schedule.body}",
      type = NotificationType.MAINTENANCE_DUE,
      channel = NotificationChannel.FIREBASE_FCM
    )

    addAuditLog(
      actorName = _currentUser.value.name,
      actorRole = _currentUser.value.role,
      action = "GỬI_FIREBASE_FCM",
      targetEntity = "FcmMaintenanceSchedule",
      details = "Gửi thông báo đẩy FCM nhắc bảo dưỡng xe ${schedule.vehiclePlate} tới token ${schedule.fcmToken}"
    )
  }

  fun createFcmMaintenanceSchedule(
    plate: String,
    model: String,
    title: String,
    body: String,
    date: String,
    odoKm: Int
  ) {
    val newSchedule = FcmMaintenanceSchedule(
      vehiclePlate = plate,
      vehicleModel = model,
      title = title,
      body = body,
      scheduledDate = date,
      targetOdoKm = odoKm
    )
    _fcmMaintenanceSchedules.value = listOf(newSchedule) + _fcmMaintenanceSchedules.value
    addAuditLog(
      actorName = _currentUser.value.name,
      actorRole = _currentUser.value.role,
      action = "TẠO_LỊCH_NHẮC_FCM",
      targetEntity = "FcmMaintenanceSchedule",
      details = "Lập lịch gửi FCM nhắc bảo dưỡng xe $plate ngày $date"
    )
  }

  // --- MỞ RỘNG TÍNH NĂNG: ĐIỀU CHUYỂN KHO LIÊN CHI NHÁNH ---
  private val _stockTransfers = MutableStateFlow(
    listOf(
      StockTransferRequest(
        partCode = "BRK-PAD-CER",
        partName = "Bố Thắng Gốm Carbon Ceramic",
        fromBranchId = "Kho Tổng Trụ Sở",
        toBranchId = "iVehicle Quận 7 (BR-01)",
        quantity = 4,
        createdAt = "2026-10-07 14:30"
      ),
      StockTransferRequest(
        partCode = "FIL-CABIN-C1",
        partName = "Lọc Gió Cabin Than Hoạt Tính",
        fromBranchId = "iVehicle Thảo Điền (BR-02)",
        toBranchId = "iVehicle Tân Bình (BR-03)",
        quantity = 10,
        createdAt = "2026-10-06 09:15"
      )
    )
  )
  val stockTransfers: StateFlow<List<StockTransferRequest>> = _stockTransfers.asStateFlow()

  fun createStockTransfer(partCode: String, partName: String, from: String, to: String, qty: Int) {
    val req = StockTransferRequest(
      partCode = partCode,
      partName = partName,
      fromBranchId = from,
      toBranchId = to,
      quantity = qty,
      createdAt = "Vừa xong"
    )
    _stockTransfers.value = listOf(req) + _stockTransfers.value
    addAuditLog(
      actorName = _currentUser.value.name,
      actorRole = _currentUser.value.role,
      action = "ĐIỀU_CHUYỂN_KHO",
      targetEntity = "StockTransfer",
      details = "Tạo lệnh điều chuyển $qty x $partName từ $from sang $to"
    )
  }
}
