package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.model.ServiceBay
import com.example.repository.AppRepository
import com.example.ui.components.BrutalCard
import com.example.ui.components.BrutalPill
import com.example.ui.components.brutalShadow
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceBaysManagerScreen(
  onBack: () -> Unit
) {
  val bays by AppRepository.serviceBays.collectAsState()
  val bookings by AppRepository.bookings.collectAsState()
  val technicians by AppRepository.technicians.collectAsState()

  var selectedBayForReassign by remember { mutableStateOf<ServiceBay?>(null) }
  var selectedBookingForTechReassign by remember { mutableStateOf<String?>(null) }
  var selectedBookingForBayTransfer by remember { mutableStateOf<String?>(null) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = "Giám Sát Khoang & Hàng Đợi Xưởng",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)
            )
            Text(
              text = "Cập nhật thời gian thực • Điều phối khoang & kỹ thuật viên",
              style = MaterialTheme.typography.labelSmall.copy(color = TextGray)
            )
          }
        },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = PositivusCanvas)
      )
    },
    containerColor = PositivusCanvas
  ) { padding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding),
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // 1. KPI Snapshot bar
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          val occupiedCount = bays.count { it.isOccupied }
          BrutalCard(
            modifier = Modifier.weight(1f),
            cornerRadius = 18.dp,
            contentPadding = PaddingValues(12.dp)
          ) {
            Text("TẢI KHOANG", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextGray)
            Text("$occupiedCount/${bays.size}", fontSize = 22.sp, fontWeight = FontWeight.Black, color = if (occupiedCount >= bays.size) AlertRed else PositivusDark)
            Text("${bays.size - occupiedCount} khoang đang trống", fontSize = 11.sp, color = PositivusGreen.copy(alpha = 0.9f))
          }

          BrutalCard(
            modifier = Modifier.weight(1f),
            cornerRadius = 18.dp,
            contentPadding = PaddingValues(12.dp)
          ) {
            Text("KTV TRỰC CA", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextGray)
            Text("${technicians.size}", fontSize = 22.sp, fontWeight = FontWeight.Black, color = PositivusDark)
            Text("KPI TB: 97.9%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = StatusGreen)
          }
        }
      }

      // 2. Section Header
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "TRẠNG THÁI CÁC KHOANG SỬA CHỮA (BAYS)",
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            color = PositivusDark
          )
          Text(
            text = "Bấm để đổi KTV / Chuyển khoang",
            fontSize = 11.sp,
            color = TextGray
          )
        }
      }

      // 3. Bay cards with real-time queues
      items(bays) { bay ->
        val currentBooking = bookings.find { it.id == bay.currentBookingId }
        val assignedTech = technicians.find { it.id == currentBooking?.assignedTechId }

        BrutalCard(
          modifier = Modifier.fillMaxWidth(),
          backgroundColor = if (bay.isOccupied) Color.White else Color(0xFFF9FAFB),
          cornerRadius = 22.dp,
          contentPadding = PaddingValues(16.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp),
              modifier = Modifier.weight(1f)
            ) {
              Box(
                modifier = Modifier
                  .size(44.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .background(if (bay.isOccupied) PositivusGreen else PositivusGray)
                  .border(1.5.dp, PositivusBorder, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "#0${bay.bayNumber}",
                  fontWeight = FontWeight.Black,
                  fontSize = 15.sp,
                  color = PositivusDark
                )
              }

              Column {
                Text(
                  text = bay.name,
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Black,
                  color = PositivusDark
                )
                Text(
                  text = bay.type.label,
                  fontSize = 12.sp,
                  color = TextGray
                )
              }
            }

            BrutalPill(
              text = if (bay.isOccupied) "ĐANG CÓ XE" else "KHOANG TRỐNG",
              backgroundColor = if (bay.isOccupied) PositivusDark else PositivusGreen,
              textColor = if (bay.isOccupied) PositivusGreen else PositivusDark
            )
          }

          if (currentBooking != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = PositivusBorder.copy(alpha = 0.2f), thickness = 1.dp)
            Spacer(modifier = Modifier.height(10.dp))

            // Vehicle and Technician in Bay
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "LỆNH #${currentBooking.id}",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = PrimaryBlue
                )
                Text(
                  text = "Tiến độ: ${currentBooking.status.label}",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = PositivusDark
                )
                Text(
                  text = "KTV phụ trách: ${currentBooking.assignedTechName ?: "Chưa chỉ định"}",
                  fontSize = 12.sp,
                  color = TextGray
                )
              }

              Column(horizontalAlignment = Alignment.End) {
                Text(
                  text = "Giờ hẹn: ${currentBooking.appointmentTime}",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                  // Button: Reassign Tech
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PositivusGreen,
                    border = BorderStroke(1.dp, PositivusBorder),
                    modifier = Modifier.clickable {
                      selectedBookingForTechReassign = currentBooking.id
                    }
                  ) {
                    Text(
                      text = "Đổi KTV",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      color = PositivusDark,
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                  }

                  // Button: Transfer to another Bay
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PositivusGray,
                    border = BorderStroke(1.dp, PositivusBorder),
                    modifier = Modifier.clickable {
                      selectedBookingForBayTransfer = currentBooking.id
                    }
                  ) {
                    Text(
                      text = "Chuyển khoang",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      color = PositivusDark,
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                  }
                }
              }
            }
          }
        }
      }

      // 4. Unassigned / Queued incoming jobs
      val unassignedBookings = bookings.filter { it.status == BookingStatus.CONFIRMED || it.status == BookingStatus.VEHICLE_RECEIVED }
      if (unassignedBookings.isNotEmpty()) {
        item {
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "HÀNG ĐỢI XE CHỜ VÀO KHOANG (${unassignedBookings.size} XE)",
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            color = PositivusDark
          )
        }

        items(unassignedBookings) { booking ->
          BrutalCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 18.dp,
            contentPadding = PaddingValues(14.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "Lệnh #${booking.id} • ${booking.appointmentTime}",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Black
                )
                Text(
                  text = "Ghi chú: ${booking.customerNotes.ifBlank { "Bảo dưỡng định kỳ" }}",
                  fontSize = 12.sp,
                  color = TextGray
                )
              }

              Surface(
                shape = RoundedCornerShape(10.dp),
                color = PositivusGreen,
                border = BorderStroke(1.2.dp, PositivusBorder),
                modifier = Modifier.clickable {
                  selectedBookingForBayTransfer = booking.id
                }
              ) {
                Text(
                  text = "Xếp vào khoang",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Black,
                  color = PositivusDark,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
              }
            }
          }
        }
      }
    }
  }

  // Dialog: Reassign Technician
  if (selectedBookingForTechReassign != null) {
    val bId = selectedBookingForTechReassign!!
    AlertDialog(
      onDismissRequest = { selectedBookingForTechReassign = null },
      title = { Text("Đổi Kỹ Thuật Viên Cho Lệnh #$bId", fontWeight = FontWeight.Black) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text("Chọn KTV phù hợp theo chuyên môn và tải công việc:", fontSize = 12.sp, color = TextGray)
          technicians.forEach { tech ->
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = Color.White,
              border = BorderStroke(1.2.dp, PositivusBorder),
              modifier = Modifier
                .fillMaxWidth()
                .clickable {
                  AppRepository.assignTechnician(bId, tech.id, "Quản lý điều phối lại KTV tại khoang sửa chữa")
                  selectedBookingForTechReassign = null
                }
            ) {
              Row(
                modifier = Modifier.padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text(tech.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                  Text(tech.specialization, fontSize = 11.sp, color = TextGray)
                }
                Text(
                  text = "${tech.activeJobsCount}/${tech.maxCapacity} việc",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (tech.activeJobsCount >= tech.maxCapacity) AlertRed else PrimaryBlue
                )
              }
            }
          }
        }
      },
      confirmButton = {},
      dismissButton = {
        TextButton(onClick = { selectedBookingForTechReassign = null }) { Text("Đóng") }
      }
    )
  }

  // Dialog: Transfer Task to Another Bay
  if (selectedBookingForBayTransfer != null) {
    val bId = selectedBookingForBayTransfer!!
    AlertDialog(
      onDismissRequest = { selectedBookingForBayTransfer = null },
      title = { Text("Chuyển Lệnh #$bId Vào Khoang Mới", fontWeight = FontWeight.Black) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text("Chọn khoang tiếp nhận sửa chữa:", fontSize = 12.sp, color = TextGray)
          bays.forEach { b ->
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = if (b.isOccupied) PositivusGray else Color.White,
              border = BorderStroke(1.2.dp, PositivusBorder),
              modifier = Modifier
                .fillMaxWidth()
                .clickable {
                  AppRepository.reassignTaskToBay(bId, b.id)
                  selectedBookingForBayTransfer = null
                }
            ) {
              Row(
                modifier = Modifier.padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text(b.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                  Text(b.type.label, fontSize = 11.sp, color = TextGray)
                }
                BrutalPill(
                  text = if (b.isOccupied) "Có xe" else "Trống",
                  backgroundColor = if (b.isOccupied) Color(0xFFFEE2E2) else PositivusGreen,
                  textColor = if (b.isOccupied) AlertRed else PositivusDark
                )
              }
            }
          }
        }
      },
      confirmButton = {},
      dismissButton = {
        TextButton(onClick = { selectedBookingForBayTransfer = null }) { Text("Đóng") }
      }
    )
  }
}
