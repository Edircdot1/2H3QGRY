package com.example

import com.example.model.*
import com.example.repository.AppRepository
import org.junit.Assert.*
import org.junit.Test

class BusinessLogicUnitTest {

  @Test
  fun testVehicleOwnershipAndFormatting() {
    val vehicles = AppRepository.vehicles.value
    assertTrue("Hệ thống phải có danh sách xe khởi tạo", vehicles.isNotEmpty())
    
    val porsche = vehicles.first { it.make == "Porsche" }
    assertEquals("51K-889.92", porsche.licensePlate)
    assertEquals(VerificationStatus.VERIFIED, porsche.verificationStatus)

    val formatted = AppRepository.formatVnd(2450000L)
    assertTrue("Phải có định dạng tiền VND có dấu phân cách", formatted.contains("2.450.000") || formatted.contains("2,450,000"))
  }

  @Test
  fun testQuoteVersioningAndPartialApproval() {
    val quotes = AppRepository.quotes.value
    assertTrue("Phải có ít nhất 1 báo giá trong hệ thống", quotes.isNotEmpty())
    val quote = quotes.first()

    assertEquals(1, quote.version)
    assertEquals(15, quote.validDays)
    
    val initialApprovedTotal = quote.totalApprovedAmountVnd
    val totalProposed = quote.totalProposedAmountVnd
    assertTrue("Tổng duyệt một phần phải nhỏ hơn hoặc bằng tổng đề xuất", initialApprovedTotal <= totalProposed)

    // Toggle an item
    val firstItem = quote.items.first()
    val wasApproved = firstItem.isApproved
    AppRepository.toggleQuoteItemApproval(quote.id, firstItem.id)

    val updatedQuote = AppRepository.quotes.value.first { it.id == quote.id }
    val updatedItem = updatedQuote.items.first { it.id == firstItem.id }
    assertEquals(!wasApproved, updatedItem.isApproved)
  }

  @Test
  fun testBookingCreationAndStateMachine() {
    val newBooking = AppRepository.createBooking(
      vehicleId = "VEH-1",
      branchId = "BR-01",
      servicePackageId = "SRV-01",
      appointmentDate = "2026-11-01",
      appointmentTime = "10:30",
      notes = "Test Unit Booking"
    )

    assertEquals(BookingStatus.PENDING_CONFIRMATION, newBooking.status)

    // Move state to CONFIRMED
    AppRepository.updateBookingStatus(newBooking.id, BookingStatus.CONFIRMED, "Quản lý xác nhận lịch")
    val confirmedBooking = AppRepository.bookings.value.first { it.id == newBooking.id }
    assertEquals(BookingStatus.CONFIRMED, confirmedBooking.status)

    // Check Audit Log
    val auditLogs = AppRepository.auditLogs.value
    assertTrue(auditLogs.any { it.action == "CHUYỂN_TRẠNG_THÁI_LỊCH" && it.details.contains(newBooking.id) })
  }

  @Test
  fun testPaymentAndStockReservation() {
    val initialStock = AppRepository.inventory.value.first { it.code == "OIL-MOBIL-1" }.reservedStock

    AppRepository.approveQuote("BG-882101")
    
    val quote = AppRepository.quotes.value.first { it.id == "BG-882101" }
    assertTrue("Báo giá phải được ghi nhận đã ký duyệt", quote.isSignedByCustomer)

    // Payment completion
    val pay = AppRepository.confirmPayment(
      bookingId = "IV-8821",
      quoteId = "BG-882101",
      amount = quote.totalApprovedAmountVnd,
      method = PaymentMethod.VIETQR
    )

    assertTrue("Giao dịch thanh toán phải được xác nhận", pay.isConfirmed)
    val booking = AppRepository.bookings.value.first { it.id == "IV-8821" }
    assertEquals(BookingStatus.IN_SERVICE, booking.status)
  }

  @Test
  fun testWarrantyTracking() {
    val list = AppRepository.warranties.value
    assertTrue("Hệ thống phải có danh sách chính sách bảo hành mẫu", list.isNotEmpty())
    val activeList = list.filter { it.status == WarrantyStatus.ACTIVE }
    assertTrue("Có ít nhất 1 bảo hành đang hiệu lực", activeList.isNotEmpty())
  }

  @Test
  fun testFranchiseExplanationWorkflow() {
    val reqs = AppRepository.explanationRequests.value
    assertTrue("Phải có yêu cầu giải trình hợp đồng mẫu", reqs.isNotEmpty())
    val firstReq = reqs.first()
    assertEquals(ExplanationStatus.PENDING_RESPONSE, firstReq.status)

    AppRepository.submitExplanationResponse(firstReq.id, "Đã bù trừ chứng từ kho")
    val updated = AppRepository.explanationRequests.value.first { it.id == firstReq.id }
    assertEquals(ExplanationStatus.RESOLVED, updated.status)
    assertEquals("Đã bù trừ chứng từ kho", updated.responseNote)

    val audit = AppRepository.auditLogs.value
    assertTrue(audit.any { it.action == "PHẢN_HỒI_GIẢI_TRÌNH" })
  }

  @Test
  fun testStockTransferCreation() {
    val initialCount = AppRepository.stockTransfers.value.size
    AppRepository.createStockTransfer(
      partCode = "TEST-PART",
      partName = "Lọc Dầu Test",
      from = "Kho A",
      to = "Kho B",
      qty = 2
    )
    val afterCount = AppRepository.stockTransfers.value.size
    assertEquals(initialCount + 1, afterCount)
  }

  @Test
  fun testServiceBaysQueueAndReassignment() {
    val bays = AppRepository.serviceBays.value
    assertTrue("Hệ thống phải có danh sách các khoang sửa chữa (bays)", bays.isNotEmpty())
    
    val bay1 = bays.first { it.id == "BAY-01" }
    assertTrue("Khoang 1 đang có xe", bay1.isOccupied)
    assertEquals("IV-8821", bay1.currentBookingId)

    // Reassign task from bay 1 to bay 3
    AppRepository.reassignTaskToBay("IV-8821", "BAY-03")
    val updatedBay1 = AppRepository.serviceBays.value.first { it.id == "BAY-01" }
    val updatedBay3 = AppRepository.serviceBays.value.first { it.id == "BAY-03" }
    
    assertFalse("Khoang 1 sau khi chuyển phải trống", updatedBay1.isOccupied)
    assertTrue("Khoang 3 sau khi nhận phải có xe", updatedBay3.isOccupied)
    assertEquals("IV-8821", updatedBay3.currentBookingId)

    // Reassign technician
    AppRepository.assignTechnician("IV-8821", "TECH-008", "Quản lý đổi KTV chuyên trách")
    val booking = AppRepository.bookings.value.first { it.id == "IV-8821" }
    assertEquals("TECH-008", booking.assignedTechId)
    assertEquals("Vũ Minh Quân", booking.assignedTechName)

    val audit = AppRepository.auditLogs.value
    assertTrue(audit.any { it.action == "ĐIỀU_CHUYỂN_KHOANG" && it.details.contains("IV-8821") })
    assertTrue(audit.any { it.action == "PHÂN_CÔNG_KTV" && it.details.contains("IV-8821") })
  }

  @Test
  fun testNotificationSystemAutoTriggers() {
    val initialNotifs = AppRepository.notifications.value.size

    // 1. Test notification generated on booking status change (e.g. IN_SERVICE)
    AppRepository.updateBookingStatus("IV-8821", BookingStatus.IN_SERVICE, "KTV đang thi công")
    val afterStatusNotifs = AppRepository.notifications.value
    assertTrue("Thông báo tiến độ sửa chữa phải được tự động sinh", afterStatusNotifs.size > initialNotifs)
    val latestProgressNotif = afterStatusNotifs.first()
    assertEquals(NotificationType.SERVICE_PROGRESS, latestProgressNotif.type)
    assertTrue(latestProgressNotif.body.contains("IV-8821"))

    // 2. Test manual push of appointment reminder
    AppRepository.pushNotification(
      title = "Nhắc hẹn trước 2 giờ",
      body = "Xe của bạn có lịch hẹn lúc 14:00 hôm nay tại chi nhánh Q7.",
      type = NotificationType.APPOINTMENT_REMINDER,
      bookingId = "IV-8821"
    )
    val latestReminder = AppRepository.notifications.value.first()
    assertEquals(NotificationType.APPOINTMENT_REMINDER, latestReminder.type)
    assertFalse(latestReminder.isRead)

    // 3. Mark as read
    AppRepository.markNotificationAsRead(latestReminder.id)
    val readNotif = AppRepository.notifications.value.first { it.id == latestReminder.id }
    assertTrue(readNotif.isRead)
  }

  @Test
  fun testServiceHistoryAndInvoiceLookup() {
    val histories = AppRepository.serviceHistoryRecords.value
    assertTrue("Hệ thống phải có danh sách lịch sử bảo dưỡng mẫu", histories.isNotEmpty())

    // Filter by vehicle
    val corollaHistories = histories.filter { it.vehiclePlate == "51K-123.45" }
    assertEquals(2, corollaHistories.size)

    val firstRecord = corollaHistories.first()
    assertEquals("Toyota Corolla Cross", firstRecord.vehicleModel)
    assertTrue("Hóa đơn phải đính kèm và có tổng tiền", firstRecord.invoice.totalVnd > 0)
    assertTrue("Phải có mã hóa đơn hợp lệ", firstRecord.invoice.invoiceNumber.startsWith("HD-"))
    assertTrue("Hóa đơn đã được xác nhận thanh toán", firstRecord.invoice.isPaid)
    assertTrue("Hạng mục đã thực hiện phải có chi tiết", firstRecord.performedItems.isNotEmpty())
  }

  @Test
  fun testNearbyStationsDistanceCalculation() {
    val branches = AppRepository.branches.value
    assertTrue("Danh sách chi nhánh phải có tọa độ", branches.isNotEmpty())

    // Mock user at District 7 center (10.7480, 106.7120)
    val userLat = 10.7480
    val userLon = 106.7120

    val branchQ7 = branches.first { it.id == "BR-01" }
    val dist = AppRepository.calculateDistanceKm(userLat, userLon, branchQ7.latitude, branchQ7.longitude)
    assertTrue("Khoảng cách tới trạm Q7 phải nhỏ hơn 3km", dist in 0.1..3.0)

    val branchHanoi = branches.first { it.id == "BR-04" }
    val distHanoi = AppRepository.calculateDistanceKm(userLat, userLon, branchHanoi.latitude, branchHanoi.longitude)
    assertTrue("Khoảng cách từ TP.HCM tới trạm Hà Nội phải lớn hơn 1000km", distHanoi > 1000.0)
  }

  @Test
  fun testFcmMaintenanceScheduleReminders() {
    val schedules = AppRepository.fcmMaintenanceSchedules.value
    assertTrue("Hệ thống phải có danh sách lịch bảo dưỡng FCM mẫu", schedules.isNotEmpty())

    val firstSchedule = schedules.first()
    assertFalse("Ban đầu lịch bảo dưỡng chưa gửi", firstSchedule.isSent)

    // Trigger FCM maintenance reminder dispatch
    AppRepository.dispatchFcmMaintenanceReminder(firstSchedule.id)

    val updatedSchedules = AppRepository.fcmMaintenanceSchedules.value
    val updatedItem = updatedSchedules.first { it.id == firstSchedule.id }
    assertTrue("Sau khi bắn FCM, trạng thái phải là isSent = true", updatedItem.isSent)

    // Check that notification inbox received the FCM message
    val notifs = AppRepository.notifications.value
    val fcmNotif = notifs.first { it.channel == NotificationChannel.FIREBASE_FCM }
    assertTrue(fcmNotif.title.contains("FCM Cloud"))
    assertTrue(fcmNotif.body.contains(firstSchedule.vehiclePlate))

    // Check audit trail recorded FCM dispatch
    val audits = AppRepository.auditLogs.value
    assertTrue(audits.any { it.action == "GỬI_FIREBASE_FCM" })
  }
}
