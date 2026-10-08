package com.example.ui.screens

import androidx.compose.foundation.background
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
import com.example.model.Vehicle
import com.example.model.VehicleType
import com.example.repository.AppRepository
import com.example.ui.components.GlassCard
import com.example.ui.components.GroundCTAButton
import com.example.ui.components.StatusPill
import com.example.ui.theme.*

@Composable
fun CustomerGarageScreen(
  onBookService: () -> Unit
) {
  val vehicles by AppRepository.vehicles.collectAsState()
  val bookings by AppRepository.bookings.collectAsState()

  var showAddVehicleDialog by remember { mutableStateOf(false) }
  var selectedVehicleForHistory by remember { mutableStateOf<Vehicle?>(vehicles.firstOrNull()) }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(SystemBg),
    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Header
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
            text = "GARAGE CỦA TÔI",
            style = MaterialTheme.typography.labelSmall.copy(
              color = LabelTertiary,
              letterSpacing = 1.sp
            )
          )
          Text(
            text = "Danh Sách Phương Tiện",
            style = MaterialTheme.typography.headlineLarge.copy(color = LabelPrimary)
          )
        }

        IconButton(
          onClick = { showAddVehicleDialog = true },
          modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(SecondaryDark)
        ) {
          Icon(Icons.Filled.Add, contentDescription = "Thêm xe", tint = Color.White)
        }
      }
    }

    // Vehicle cards
    items(vehicles) { vehicle ->
      val isSelected = vehicle.id == selectedVehicleForHistory?.id
      GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 24.dp,
        borderAlpha = if (isSelected) 1f else 0.4f,
        onClick = { selectedVehicleForHistory = vehicle }
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.Top
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(PrimaryBlue.copy(alpha = 0.12f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = if (vehicle.type == VehicleType.CAR) Icons.Filled.DirectionsCar else Icons.Filled.TwoWheeler,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier.size(22.dp)
              )
            }

            Column {
              Text(
                text = "${vehicle.make} ${vehicle.model} (${vehicle.year})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
              )
              Text(
                text = "Biển số: ${vehicle.licensePlate} • Màu: ${vehicle.color}",
                style = MaterialTheme.typography.bodySmall.copy(color = LabelSecondary)
              )
            }
          }

          StatusPill(
            text = vehicle.verificationStatus.label,
            tintColor = TertiaryGreen
          )
        }

        Spacer(modifier = Modifier.height(14.dp))
        Divider(color = GlassSeparator, thickness = 0.5.dp)
        Spacer(modifier = Modifier.height(10.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column {
            Text("SỐ KM ĐÃ ĐI", style = MaterialTheme.typography.labelSmall.copy(color = LabelTertiary))
            Text("${vehicle.mileageKm} km", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
          }
          Column {
            Text("SỐ KHUNG (VIN)", style = MaterialTheme.typography.labelSmall.copy(color = LabelTertiary))
            Text(vehicle.vinNumber.take(10) + "...", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
          }
          Column(horizontalAlignment = Alignment.End) {
            Text("TÌNH TRẠNG", style = MaterialTheme.typography.labelSmall.copy(color = LabelTertiary))
            Text("Hoạt động tốt", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = TertiaryGreen))
          }
        }
      }
    }

    // Historical services scoped strictly to selected vehicle
    if (selectedVehicleForHistory != null) {
      item {
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "LỊCH SỬ DỊCH VỤ CỦA XE ${selectedVehicleForHistory!!.licensePlate}",
          style = MaterialTheme.typography.labelSmall.copy(color = LabelTertiary)
        )
      }

      val vehicleBookings = bookings.filter { it.vehicleId == selectedVehicleForHistory!!.id }

      if (vehicleBookings.isEmpty()) {
        item {
          GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 18.dp) {
            Text("Chưa có hồ sơ dịch vụ nào cho xe này.", style = MaterialTheme.typography.bodyMedium.copy(color = LabelSecondary))
          }
        }
      } else {
        items(vehicleBookings) { booking ->
          GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 20.dp) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "LỊCH HẸN #${booking.id}",
                  style = MaterialTheme.typography.labelSmall.copy(color = PrimaryBlue, fontWeight = FontWeight.Bold)
                )
                Text(
                  text = "Ngày ${booking.appointmentDate} • ${booking.appointmentTime}",
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                  text = "Chi nhánh tiếp nhận: ${booking.branchId}",
                  style = MaterialTheme.typography.bodySmall.copy(color = LabelSecondary)
                )
              }

              StatusPill(
                text = booking.status.label,
                tintColor = if (booking.status == BookingStatus.COMPLETED) TertiaryGreen else PrimaryBlue
              )
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(70.dp))
    }
  }

  // Dialog Add New Vehicle
  if (showAddVehicleDialog) {
    var make by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var licensePlate by remember { mutableStateOf("") }
    var yearText by remember { mutableStateOf("2024") }
    var isCar by remember { mutableStateOf(true) }

    AlertDialog(
      onDismissRequest = { showAddVehicleDialog = false },
      title = { Text("Thêm Phương Tiện Mới", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text("Nhập thông tin xe chính xác để hệ thống chi nhánh xác minh.", fontSize = 12.sp, color = LabelSecondary)
          
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
              onClick = { isCar = true },
              colors = ButtonDefaults.buttonColors(containerColor = if (isCar) PrimaryBlue else Color.LightGray)
            ) {
              Text("Ô tô")
            }
            Button(
              onClick = { isCar = false },
              colors = ButtonDefaults.buttonColors(containerColor = if (!isCar) PrimaryBlue else Color.LightGray)
            ) {
              Text("Xe máy")
            }
          }

          OutlinedTextField(
            value = make,
            onValueChange = { make = it },
            label = { Text("Hãng xe (VD: Honda, Toyota, BMW)") },
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = model,
            onValueChange = { model = it },
            label = { Text("Dòng xe (VD: SH 160i, Camry, CR-V)") },
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = licensePlate,
            onValueChange = { licensePlate = it },
            label = { Text("Biển số xe (VD: 51K-123.45)") },
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (make.isNotBlank() && licensePlate.isNotBlank()) {
              AppRepository.addVehicle(
                Vehicle(
                  customerId = "USR-001",
                  licensePlate = licensePlate,
                  make = make,
                  model = if (model.isBlank()) "Standard" else model,
                  year = yearText.toIntOrNull() ?: 2024,
                  type = if (isCar) VehicleType.CAR else VehicleType.MOTORCYCLE,
                  color = "Trắng ngọc trai",
                  mileageKm = 1000
                )
              )
              showAddVehicleDialog = false
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
          Text("Lưu xe")
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddVehicleDialog = false }) { Text("Hủy") }
      }
    )
  }
}
