package com.example.ui.screens

import androidx.compose.foundation.background
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
import com.example.model.Branch
import com.example.model.ServicePackage
import com.example.model.Vehicle
import com.example.model.VehicleType
import com.example.repository.AppRepository
import com.example.ui.components.GlassCard
import com.example.ui.components.GroundCTAButton
import com.example.ui.components.StatusPill
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingFlowScreen(
  onBack: () -> Unit,
  onBookingSuccess: (String) -> Unit
) {
  val vehicles by AppRepository.vehicles.collectAsState()
  val branches by AppRepository.branches.collectAsState()
  val services by AppRepository.services.collectAsState()

  // 3-step booking state
  var currentStep by remember { mutableIntStateOf(1) } // 1: Nhu cầu & Xe, 2: Địa điểm & Thời gian, 3: Kiểm tra & Gửi
  var selectedVehicle by remember { mutableStateOf<Vehicle?>(vehicles.firstOrNull()) }
  var selectedService by remember { mutableStateOf<ServicePackage?>(services.firstOrNull()) }
  var selectedBranch by remember { mutableStateOf<Branch?>(branches.firstOrNull()) }
  var selectedDate by remember { mutableStateOf("2026-10-15") }
  var selectedTimeSlot by remember { mutableStateOf("09:00") }
  var customerNotes by remember { mutableStateOf("") }
  var isSubmitting by remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = "Đặt Lịch Dịch Vụ",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
              text = "Bước $currentStep/3: " + when (currentStep) {
                1 -> "Chọn xe & Gói dịch vụ"
                2 -> "Chọn chi nhánh & Giờ hẹn"
                else -> "Xác nhận & Gửi yêu cầu"
              },
              style = MaterialTheme.typography.labelSmall.copy(color = PrimaryBlue)
            )
          }
        },
        navigationIcon = {
          IconButton(onClick = {
            if (currentStep > 1) currentStep-- else onBack()
          }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = SystemBg)
      )
    },
    containerColor = SystemBg
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .padding(horizontal = 18.dp, vertical = 8.dp)
    ) {
      // Step Progress Indicator
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        for (i in 1..3) {
          Box(
            modifier = Modifier
              .weight(1f)
              .height(4.dp)
              .clip(RoundedCornerShape(2.dp))
              .background(
                if (i <= currentStep) PrimaryBlue else Color(0xFFD9DADE)
              )
          )
        }
      }

      // Step Contents
      Box(modifier = Modifier.weight(1f)) {
        when (currentStep) {
          1 -> Step1VehicleAndService(
            vehicles = vehicles,
            services = services,
            selectedVehicle = selectedVehicle,
            onSelectVehicle = { selectedVehicle = it },
            selectedService = selectedService,
            onSelectService = { selectedService = it }
          )

          2 -> Step2BranchAndTime(
            branches = branches,
            selectedBranch = selectedBranch,
            onSelectBranch = { selectedBranch = it },
            selectedDate = selectedDate,
            onSelectDate = { selectedDate = it },
            selectedTime = selectedTimeSlot,
            onSelectTime = { selectedTimeSlot = it },
            notes = customerNotes,
            onNotesChange = { customerNotes = it }
          )

          3 -> Step3ReviewAndSubmit(
            vehicle = selectedVehicle,
            service = selectedService,
            branch = selectedBranch,
            date = selectedDate,
            time = selectedTimeSlot,
            notes = customerNotes
          )
        }
      }

      // Action Bottom Bar
      Spacer(modifier = Modifier.height(10.dp))
      GroundCTAButton(
        text = if (currentStep < 3) "Tiếp Tục Sang Bước ${currentStep + 1}" else "Xác Nhận & Gửi Đặt Lịch",
        subtext = if (currentStep == 3) "Cam kết phản hồi trong 15 phút làm việc" else null,
        icon = if (currentStep < 3) Icons.Filled.ArrowForward else Icons.Filled.CheckCircle,
        enabled = !isSubmitting && (
          (currentStep == 1 && selectedVehicle != null && selectedService != null) ||
          (currentStep == 2 && selectedBranch != null) ||
          (currentStep == 3)
        ),
        onClick = {
          if (currentStep < 3) {
            currentStep++
          } else {
            isSubmitting = true
            val newBooking = AppRepository.createBooking(
              vehicleId = selectedVehicle!!.id,
              branchId = selectedBranch!!.id,
              servicePackageId = selectedService!!.id,
              appointmentDate = selectedDate,
              appointmentTime = selectedTimeSlot,
              notes = customerNotes
            )
            onBookingSuccess(newBooking.id)
          }
        }
      )
      Spacer(modifier = Modifier.height(8.dp))
    }
  }
}

@Composable
private fun Step1VehicleAndService(
  vehicles: List<Vehicle>,
  services: List<ServicePackage>,
  selectedVehicle: Vehicle?,
  onSelectVehicle: (Vehicle) -> Unit,
  selectedService: ServicePackage?,
  onSelectService: (ServicePackage) -> Unit
) {
  LazyColumn(
    verticalArrangement = Arrangement.spacedBy(14.dp),
    modifier = Modifier.fillMaxSize()
  ) {
    item {
      Text(
        text = "1. CHỌN PHƯƠNG TIỆN CỦA BẠN",
        style = MaterialTheme.typography.labelSmall.copy(color = LabelTertiary)
      )
    }

    items(vehicles) { vehicle ->
      val isSelected = vehicle.id == selectedVehicle?.id
      GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 20.dp,
        borderAlpha = if (isSelected) 1f else 0.4f,
        onClick = { onSelectVehicle(vehicle) }
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            RadioButton(
              selected = isSelected,
              onClick = { onSelectVehicle(vehicle) },
              colors = RadioButtonDefaults.colors(selectedColor = PrimaryBlue)
            )

            Column {
              Text(
                text = "${vehicle.make} ${vehicle.model}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
              )
              Text(
                text = "${vehicle.licensePlate} • ${vehicle.color}",
                style = MaterialTheme.typography.bodySmall.copy(color = LabelSecondary)
              )
            }
          }

          StatusPill(
            text = if (vehicle.type == VehicleType.CAR) "Ô tô" else "Xe máy",
            tintColor = PrimaryBlue
          )
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "2. CHỌN GÓI DỊCH VỤ CHĂM SÓC",
        style = MaterialTheme.typography.labelSmall.copy(color = LabelTertiary)
      )
    }

    items(services) { service ->
      val isSelected = service.id == selectedService?.id
      val isCar = selectedVehicle?.type == VehicleType.CAR
      val price = if (isCar) service.carPriceVnd else service.motoPriceVnd

      GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 20.dp,
        borderAlpha = if (isSelected) 1f else 0.4f,
        onClick = { onSelectService(service) }
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.Top
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = service.name,
              style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = if (isSelected) PrimaryBlue else LabelPrimary
              )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = service.description,
              style = MaterialTheme.typography.bodySmall.copy(color = LabelSecondary)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              StatusPill(
                text = "⏱ ~${service.estimatedMinutes} phút",
                tintColor = LabelSecondary
              )
              StatusPill(
                text = "🛡 BH ${service.warrantyDays} ngày",
                tintColor = TertiaryGreen
              )
            }
          }

          Column(horizontalAlignment = Alignment.End) {
            Text(
              text = AppRepository.formatVnd(price),
              style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
              )
            )
            RadioButton(
              selected = isSelected,
              onClick = { onSelectService(service) },
              colors = RadioButtonDefaults.colors(selectedColor = PrimaryBlue)
            )
          }
        }
      }
    }
  }
}

@Composable
private fun Step2BranchAndTime(
  branches: List<Branch>,
  selectedBranch: Branch?,
  onSelectBranch: (Branch) -> Unit,
  selectedDate: String,
  onSelectDate: (String) -> Unit,
  selectedTime: String,
  onSelectTime: (String) -> Unit,
  notes: String,
  onNotesChange: (String) -> Unit
) {
  val timeSlots = listOf("08:30", "09:30", "11:00", "13:30", "15:00", "16:30")
  val sampleDates = listOf("2026-10-15", "2026-10-16", "2026-10-17", "2026-10-18")

  LazyColumn(
    verticalArrangement = Arrangement.spacedBy(14.dp),
    modifier = Modifier.fillMaxSize()
  ) {
    item {
      Text(
        text = "1. CHỌN CHI NHÁNH TIẾP NHẬN",
        style = MaterialTheme.typography.labelSmall.copy(color = LabelTertiary)
      )
    }

    items(branches) { branch ->
      val isSelected = branch.id == selectedBranch?.id
      GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 20.dp,
        borderAlpha = if (isSelected) 1f else 0.4f,
        onClick = { onSelectBranch(branch) }
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            RadioButton(
              selected = isSelected,
              onClick = { onSelectBranch(branch) },
              colors = RadioButtonDefaults.colors(selectedColor = PrimaryBlue)
            )
            Column {
              Text(
                text = branch.name,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
              )
              Text(
                text = "${branch.address}, ${branch.district}",
                style = MaterialTheme.typography.bodySmall.copy(color = LabelSecondary)
              )
            }
          }

          Column(horizontalAlignment = Alignment.End) {
            Text(
              text = "${branch.distanceKm} km",
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
              )
            )
            StatusPill(
              text = "${branch.maxDailyCapacity - branch.currentActiveJobs} chỗ trống",
              tintColor = TertiaryGreen
            )
          }
        }
      }
    }

    item {
      Text(
        text = "2. CHỌN NGÀY & KHUNG GIỜ HẸN",
        style = MaterialTheme.typography.labelSmall.copy(color = LabelTertiary)
      )
      Spacer(modifier = Modifier.height(6.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        sampleDates.forEach { date ->
          val isSelected = date == selectedDate
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(14.dp))
              .background(if (isSelected) PrimaryBlue else Color.White)
              .clickable { onSelectDate(date) }
              .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = date.substring(5), // MM-DD
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else LabelPrimary
              )
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))
      Text(
        text = "Khung giờ sẵn sàng:",
        style = MaterialTheme.typography.bodySmall.copy(color = LabelSecondary)
      )
      Spacer(modifier = Modifier.height(6.dp))

      // 3x2 Grid for time slots
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        timeSlots.chunked(3).forEach { rowSlots ->
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            rowSlots.forEach { slot ->
              val isSelected = slot == selectedTime
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(12.dp))
                  .background(if (isSelected) SecondaryDark else Color.White)
                  .clickable { onSelectTime(slot) }
                  .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = slot,
                  style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else LabelPrimary
                  )
                )
              }
            }
          }
        }
      }
    }

    item {
      Text(
        text = "3. GHI CHÚ BỔ SUNG CHO KỸ THUẬT VIÊN",
        style = MaterialTheme.typography.labelSmall.copy(color = LabelTertiary)
      )
      Spacer(modifier = Modifier.height(6.dp))
      OutlinedTextField(
        value = notes,
        onValueChange = onNotesChange,
        placeholder = { Text("Ví dụ: xe bị rung nhẹ khi qua gờ giảm tốc, cần kiểm tra kĩ...") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = Color.White,
          unfocusedContainerColor = Color.White
        ),
        minLines = 2
      )
    }
  }
}

@Composable
private fun Step3ReviewAndSubmit(
  vehicle: Vehicle?,
  service: ServicePackage?,
  branch: Branch?,
  date: String,
  time: String,
  notes: String
) {
  val isCar = vehicle?.type == VehicleType.CAR
  val price = if (isCar) service?.carPriceVnd ?: 0L else service?.motoPriceVnd ?: 0L

  LazyColumn(
    verticalArrangement = Arrangement.spacedBy(14.dp),
    modifier = Modifier.fillMaxSize()
  ) {
    item {
      Text(
        text = "KIỂM TRA THÔNG TIN TRƯỚC KHI GỬI",
        style = MaterialTheme.typography.labelSmall.copy(color = LabelTertiary)
      )
    }

    item {
      GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 24.dp) {
        Text(
          text = "TÓM TẮT ĐẶT LỊCH HẸN",
          style = MaterialTheme.typography.labelSmall.copy(
            color = PrimaryBlue,
            letterSpacing = 1.sp
          )
        )
        Spacer(modifier = Modifier.height(12.dp))

        ReviewRow("Phương tiện", "${vehicle?.make} ${vehicle?.model} (${vehicle?.licensePlate})")
        ReviewRow("Gói dịch vụ", service?.name ?: "")
        ReviewRow("Thời lượng dự kiến", "~${service?.estimatedMinutes} phút")
        ReviewRow("Chi nhánh", "${branch?.name} (${branch?.address})")
        ReviewRow("Thời gian hẹn", "$date vào lúc $time")
        if (notes.isNotBlank()) {
          ReviewRow("Yêu cầu thêm", notes)
        }

        Spacer(modifier = Modifier.height(12.dp))
        Divider(color = GlassSeparator, thickness = 0.5.dp)
        Spacer(modifier = Modifier.height(12.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Tạm tính (chưa bao gồm phụ tùng phát sinh nếu có):",
            style = MaterialTheme.typography.bodySmall.copy(color = LabelSecondary),
            modifier = Modifier.weight(1f)
          )
          Text(
            text = AppRepository.formatVnd(price),
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = PrimaryBlue
            )
          )
        }
      }
    }

    item {
      GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 18.dp,
        contentPadding = PaddingValues(14.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Icon(
            imageVector = Icons.Filled.Info,
            contentDescription = null,
            tint = PrimaryBlue,
            modifier = Modifier.size(24.dp)
          )
          Text(
            text = "Quy định đổi/hủy: Bạn có thể chủ động hủy hoặc đổi lịch hẹn miễn phí trước giờ hẹn tối thiểu 2 tiếng qua ứng dụng.",
            style = MaterialTheme.typography.bodySmall.copy(color = LabelSecondary)
          )
        }
      }
    }
  }
}

@Composable
private fun ReviewRow(label: String, value: String) {
  Column(modifier = Modifier.padding(vertical = 4.dp)) {
    Text(
      text = label.uppercase(),
      style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = LabelTertiary)
    )
    Text(
      text = value,
      style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
    )
  }
}
