package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BookingStatus
import com.example.model.InspectionCheckItem
import com.example.repository.AppRepository
import com.example.ui.components.GlassCard
import com.example.ui.components.GroundCTAButton
import com.example.ui.components.StatusPill
import com.example.ui.theme.*

@Composable
fun TechnicianWorkspaceScreen() {
  val bookings by AppRepository.bookings.collectAsState()
  val inspections by AppRepository.inspections.collectAsState()
  val technicians by AppRepository.technicians.collectAsState()
  val currentUser by AppRepository.currentUser.collectAsState()

  var selectedTab by remember { mutableIntStateOf(0) } // 0: Hàng đợi xe, 1: Checklist, 2: Kho phụ tùng, 3: Điều chuyển kho
  var activeBookingId by remember { mutableStateOf("IV-8821") }

  var showAssignDialogForBooking by remember { mutableStateOf<String?>(null) }
  var showRejectDialogForBooking by remember { mutableStateOf<String?>(null) }
  var rejectReason by remember { mutableStateOf("") }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(SystemBg),
    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Technician Header
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "KHÔNG GIAN LÀM VIỆC KỸ THUẬT",
            style = MaterialTheme.typography.labelSmall.copy(
              color = LabelTertiary,
              letterSpacing = 1.sp
            )
          )
          Text(
            text = currentUser.name,
            style = MaterialTheme.typography.headlineLarge.copy(color = LabelPrimary)
          )
        }

        StatusPill(
          text = "Ca làm: 08:00 - 17:30",
          tintColor = TertiaryGreen
        )
      }
    }

    // Work Mode Tabs (Pill Tabs)
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(Color.White)
          .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        listOf("Hàng đợi xe", "Checklist (6/6)", "Kho phụ tùng", "Điều chuyển kho").forEachIndexed { index, title ->
          val isSelected = selectedTab == index
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(12.dp))
              .background(if (isSelected) SecondaryDark else Color.Transparent)
              .clickable { selectedTab = index }
              .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = title,
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else LabelSecondary
              ),
              maxLines = 1
            )
          }
        }
      }
    }

    when (selectedTab) {
      0 -> {
        // Job Queue
        items(bookings) { booking ->
          val isCurrent = booking.id == activeBookingId
          GlassCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 20.dp,
            borderAlpha = if (isCurrent) 1f else 0.4f,
            onClick = { activeBookingId = booking.id }
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.Top
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  Text(
                    text = "LỆNH #${booking.id}",
                    style = MaterialTheme.typography.titleMedium.copy(
                      fontWeight = FontWeight.Bold,
                      color = PrimaryBlue
                    )
                  )
                  StatusPill(
                    text = booking.status.label,
                    tintColor = when (booking.status) {
                      BookingStatus.IN_SERVICE -> TertiaryGreen
                      BookingStatus.QUOTED -> PrimaryBlue
                      BookingStatus.COMPLETED -> LabelSecondary
                      else -> Color(0xFFE65100)
                    }
                  )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "Xe: Porsche Taycan 4S (51K-889.92)",
                  style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Text(
                  text = "Giờ hẹn: ${booking.appointmentTime} hôm nay",
                  style = MaterialTheme.typography.bodySmall.copy(color = LabelSecondary)
                )
                if (booking.customerNotes.isNotBlank()) {
                  Spacer(modifier = Modifier.height(6.dp))
                  Text(
                    text = "💬 Khách yêu cầu: ${booking.customerNotes}",
                    style = MaterialTheme.typography.bodySmall.copy(
                      color = LabelSecondary,
                      fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = GlassSeparator, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(8.dp))

            // Action row for Technician
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "KTV: ${booking.assignedTechName ?: "Chưa phân công"}",
                  style = MaterialTheme.typography.labelSmall.copy(color = LabelSecondary, fontWeight = FontWeight.Bold)
                )
                Text(
                  text = "Đổi KTV phân công",
                  style = MaterialTheme.typography.labelSmall.copy(color = PrimaryBlue, textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline),
                  modifier = Modifier.clickable { showAssignDialogForBooking = booking.id }
                )
              }

              Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                // Reject Button with mandatory reason
                if (booking.status == BookingStatus.CONFIRMED || booking.status == BookingStatus.VEHICLE_RECEIVED) {
                  OutlinedButton(
                    onClick = { showRejectDialogForBooking = booking.id },
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    border = BorderStroke(1.dp, ErrorRed)
                  ) {
                    Text("Từ chối ca", fontSize = 11.sp, color = ErrorRed, fontWeight = FontWeight.Bold)
                  }
                }

                Button(
                  onClick = {
                    val nextStatus = when (booking.status) {
                      BookingStatus.CONFIRMED -> BookingStatus.VEHICLE_RECEIVED
                      BookingStatus.VEHICLE_RECEIVED -> BookingStatus.INSPECTING
                      BookingStatus.INSPECTING -> BookingStatus.QUOTED
                      BookingStatus.QUOTE_APPROVED -> BookingStatus.IN_SERVICE
                      BookingStatus.IN_SERVICE -> BookingStatus.QUALITY_CHECK
                      BookingStatus.QUALITY_CHECK -> BookingStatus.COMPLETED
                      else -> booking.status
                    }
                    AppRepository.updateBookingStatus(booking.id, nextStatus, "KTV thực hiện cập nhật tiến độ xưởng")
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                  shape = RoundedCornerShape(12.dp),
                  contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                  Text(
                    text = when (booking.status) {
                      BookingStatus.CONFIRMED -> "Tiếp nhận xe"
                      BookingStatus.VEHICLE_RECEIVED -> "Bắt đầu kiểm tra"
                      BookingStatus.INSPECTING -> "Hoàn tất checklist"
                      BookingStatus.QUOTE_APPROVED -> "Bắt đầu thi công"
                      BookingStatus.IN_SERVICE -> "Nghiệm thu QC"
                      BookingStatus.QUALITY_CHECK -> "Bàn giao xe"
                      else -> "Cập nhật"
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            }
          }
        }
      }

      1 -> {
        // Inspection Checklist
        item {
          Text(
            text = "CHECKLIST TIẾP NHẬN & KIỂM TRA ĐỊNH KỲ (6 HẠNG MỤC)",
            style = MaterialTheme.typography.labelSmall.copy(color = LabelTertiary)
          )
        }

        items(inspections) { item ->
          GlassCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 18.dp,
            borderAlpha = if (item.isPassed) 0.6f else 0.9f
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.Top
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = item.name,
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                  text = "Phân loại: ${item.category}",
                  style = MaterialTheme.typography.bodySmall.copy(color = LabelTertiary)
                )
                if (item.note.isNotBlank()) {
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = "📝 ${item.note}",
                    style = MaterialTheme.typography.bodySmall.copy(
                      color = if (item.isPassed) TertiaryGreen else ErrorRed,
                      fontWeight = FontWeight.Medium
                    )
                  )
                }
              }

              Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                IconButton(
                  onClick = { AppRepository.toggleInspectionItem(item.id, true, "Đạt tiêu chuẩn kỹ thuật") },
                  modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (item.isPassed) TertiaryAccent.copy(alpha = 0.2f) else Color.White)
                ) {
                  Icon(
                    Icons.Filled.Check,
                    contentDescription = "Đạt",
                    tint = if (item.isPassed) TertiaryGreen else LabelTertiary,
                    modifier = Modifier.size(18.dp)
                  )
                }

                IconButton(
                  onClick = { AppRepository.toggleInspectionItem(item.id, false, "Cần sửa chữa / thay thế") },
                  modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (!item.isPassed) ErrorRed.copy(alpha = 0.2f) else Color.White)
                ) {
                  Icon(
                    Icons.Filled.Close,
                    contentDescription = "Không đạt",
                    tint = if (!item.isPassed) ErrorRed else LabelTertiary,
                    modifier = Modifier.size(18.dp)
                  )
                }
              }
            }
          }
        }
      }

      2 -> {
        // Warehouse Parts Lookup
        item {
          val inventory by AppRepository.inventory.collectAsState()
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
              text = "TRA CỨU TỒN KHO CHI NHÁNH & GIỮ HÀNG",
              style = MaterialTheme.typography.labelSmall.copy(color = LabelTertiary)
            )

            inventory.forEach { inv ->
              GlassCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 18.dp
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column {
                    Text(
                      text = inv.name,
                      style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                      text = "Mã: ${inv.code} • Dùng cho: ${inv.compatibleVehicles}",
                      style = MaterialTheme.typography.bodySmall.copy(color = LabelSecondary)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                      text = "Đơn giá: ${AppRepository.formatVnd(inv.unitPriceVnd)}",
                      style = MaterialTheme.typography.labelSmall.copy(
                        color = PrimaryBlue,
                        fontWeight = FontWeight.Bold
                      )
                    )
                  }

                  Column(horizontalAlignment = Alignment.End) {
                    StatusPill(
                      text = "Khả dụng: ${inv.availableStock}",
                      tintColor = if (inv.availableStock > 5) TertiaryGreen else Color(0xFFE65100)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                      text = "Tồn: ${inv.actualStock} | Giữ: ${inv.reservedStock}",
                      style = MaterialTheme.typography.labelSmall.copy(color = LabelTertiary)
                    )
                  }
                }
              }
            }
          }
        }
      }
      3 -> {
        // Inter-branch Stock Transfer Orders
        item {
          val stockTransfers by AppRepository.stockTransfers.collectAsState()
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "ĐIỀU CHUYỂN PHỤ TÙNG LIÊN CHI NHÁNH",
                style = MaterialTheme.typography.labelSmall.copy(color = LabelTertiary)
              )
              Text(
                text = "+ Tạo lệnh mới",
                style = MaterialTheme.typography.labelSmall.copy(color = PrimaryBlue, fontWeight = FontWeight.Bold),
                modifier = Modifier.clickable {
                  AppRepository.createStockTransfer(
                    partCode = "MOT-SPARK-IR",
                    partName = "Bugi Iridium NGK",
                    from = "iVehicle Quận 7",
                    to = "iVehicle Tân Bình",
                    qty = 5
                  )
                }
              )
            }

            stockTransfers.forEach { trf ->
              GlassCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 18.dp
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.Top
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = "LỆNH #${trf.id}",
                      style = MaterialTheme.typography.labelSmall.copy(color = PrimaryBlue, fontWeight = FontWeight.Bold)
                    )
                    Text(
                      text = trf.partName,
                      style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                      text = "Mã: ${trf.partCode} • Số lượng: ${trf.quantity} cái",
                      style = MaterialTheme.typography.bodySmall.copy(color = LabelSecondary)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                      text = "Từ: ${trf.fromBranchId} → Đến: ${trf.toBranchId}",
                      style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                    )
                  }

                  StatusPill(
                    text = trf.status,
                    tintColor = TertiaryGreen
                  )
                }
              }
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(70.dp))
    }
  }

  // Dialog 1: Điều phối / Phân công lại KTV
  if (showAssignDialogForBooking != null) {
    val bId = showAssignDialogForBooking!!
    AlertDialog(
      onDismissRequest = { showAssignDialogForBooking = null },
      title = { Text("Phân Công Kỹ Thuật Viên Cho Lệnh #$bId", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            text = "Phân công theo chuyên môn, tải công việc và KPI ca làm việc:",
            fontSize = 12.sp,
            color = LabelSecondary
          )

          technicians.forEach { tech ->
            GlassCard(
              modifier = Modifier.fillMaxWidth(),
              cornerRadius = 14.dp,
              contentPadding = PaddingValues(10.dp),
              onClick = {
                AppRepository.assignTechnician(bId, tech.id, "Điều phối lại theo chuyên môn ${tech.specialization}")
                showAssignDialogForBooking = null
              }
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text(tech.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                  Text(tech.specialization, fontSize = 11.sp, color = LabelSecondary)
                  Text("KPI: ${tech.kpiScore}%", fontSize = 11.sp, color = TertiaryGreen, fontWeight = FontWeight.Bold)
                }
                StatusPill(
                  text = "Đang nhận: ${tech.activeJobsCount}/${tech.maxCapacity}",
                  tintColor = if (tech.activeJobsCount >= tech.maxCapacity) ErrorRed else PrimaryBlue
                )
              }
            }
          }
        }
      },
      confirmButton = {},
      dismissButton = {
        TextButton(onClick = { showAssignDialogForBooking = null }) {
          Text("Đóng")
        }
      }
    )
  }

  // Dialog 2: Từ chối ca làm (Bắt buộc nhập lý do)
  if (showRejectDialogForBooking != null) {
    val bId = showRejectDialogForBooking!!
    AlertDialog(
      onDismissRequest = { showRejectDialogForBooking = null },
      title = { Text("Từ Chối Tiếp Nhận Ca #$bId", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            text = "Yêu cầu quy chuẩn: Phải nêu rõ lý do chính đáng để chuyển Quản lý chi nhánh điều phối lại ca.",
            fontSize = 12.sp,
            color = ErrorRed
          )
          OutlinedTextField(
            value = rejectReason,
            onValueChange = { rejectReason = it },
            placeholder = { Text("VD: Đang xử lý ca động cơ phức tạp quá tải...") },
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (rejectReason.isNotBlank()) {
              AppRepository.addAuditLog(
                actorName = currentUser.name,
                actorRole = currentUser.role,
                action = "KTV_TỪ_CHỐI_VIỆC",
                targetEntity = "Booking",
                details = "KTV từ chối ca #$bId. Lý do: $rejectReason. Chuyển Quản lý xử lý."
              )
              showRejectDialogForBooking = null
              rejectReason = ""
            }
          },
          enabled = rejectReason.isNotBlank(),
          colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
        ) {
          Text("Xác nhận từ chối")
        }
      },
      dismissButton = {
        TextButton(onClick = { showRejectDialogForBooking = null }) {
          Text("Hủy")
        }
      }
    )
  }
}
