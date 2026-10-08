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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ServiceHistoryRecord
import com.example.repository.AppRepository
import com.example.ui.components.BrutalCard
import com.example.ui.components.BrutalPill
import com.example.ui.components.brutalShadow
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceHistoryScreen(
  onBack: () -> Unit
) {
  val historyRecords by AppRepository.serviceHistoryRecords.collectAsState()
  val vehicles by AppRepository.vehicles.collectAsState()

  var selectedVehiclePlate by remember { mutableStateOf<String?>("Tất cả") }
  var selectedRecordForInvoiceModal by remember { mutableStateOf<ServiceHistoryRecord?>(null) }

  val filteredRecords = if (selectedVehiclePlate == null || selectedVehiclePlate == "Tất cả") {
    historyRecords
  } else {
    historyRecords.filter { it.vehiclePlate == selectedVehiclePlate }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = "Lịch Sử Bảo Dưỡng & Hóa Đơn",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)
            )
            Text(
              text = "Tra cứu nhật ký bảo dưỡng chi tiết theo từng xe",
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
      // 1. Vehicle Selector Filter Pills
      item {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Text(
            text = "LỌC THEO PHƯƠNG TIỆN",
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            color = PositivusDark,
            letterSpacing = 0.5.sp
          )

          val plateOptions = listOf("Tất cả") + vehicles.map { it.licensePlate }
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            plateOptions.forEach { plate ->
              val isSelected = selectedVehiclePlate == plate
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) PositivusDark else Color.White,
                border = BorderStroke(1.5.dp, PositivusBorder),
                modifier = Modifier.clickable { selectedVehiclePlate = plate }
              ) {
                Text(
                  text = plate,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  fontFamily = if (plate == "Tất cả") FontFamily.Default else FontFamily.Monospace,
                  color = if (isSelected) Color.White else PositivusDark,
                  modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                )
              }
            }
          }
        }
      }

      // 2. Summary Card
      item {
        val totalSpent = filteredRecords.sumOf { it.invoice.totalVnd }
        BrutalCard(
          modifier = Modifier.fillMaxWidth(),
          backgroundColor = PositivusDark,
          cornerRadius = 22.dp,
          contentPadding = PaddingValues(16.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text("TỔNG SỐ LẦN BẢO DƯỠNG", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PositivusGreen, letterSpacing = 0.5.sp)
              Text("${filteredRecords.size} lần dịch vụ", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color.White)
            }

            Column(horizontalAlignment = Alignment.End) {
              Text("TỔNG CHI PHÍ ĐÃ CHI", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextGray)
              Text(
                text = AppRepository.formatVnd(totalSpent),
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = PositivusGreen,
                fontFamily = FontFamily.Monospace
              )
            }
          }
        }
      }

      // 3. Service History Record Items
      items(filteredRecords) { record ->
        BrutalCard(
          modifier = Modifier.fillMaxWidth(),
          cornerRadius = 24.dp,
          contentPadding = PaddingValues(16.dp)
        ) {
          // Top row: Plate + Service Date + Badge
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = PositivusDark
              ) {
                Text(
                  text = record.vehiclePlate,
                  color = Color.White,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  fontFamily = FontFamily.Monospace,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
              }

              Text(
                text = record.vehicleModel,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextGray
              )
            }

            BrutalPill(
              text = record.serviceDate,
              backgroundColor = PositivusGreen,
              textColor = PositivusDark
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Package Title
          Text(
            text = record.packageName,
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            color = PositivusDark
          )

          Spacer(modifier = Modifier.height(4.dp))
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
              Icon(Icons.Filled.LocationOn, contentDescription = null, tint = PositivusDark, modifier = Modifier.size(14.dp))
              Text(record.branchName, fontSize = 12.sp, color = TextGray)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
              Icon(Icons.Filled.Speed, contentDescription = null, tint = PositivusDark, modifier = Modifier.size(14.dp))
              Text("${record.mileageKmAtService} km", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PositivusDark)
            }
          }

          Spacer(modifier = Modifier.height(10.dp))
          Divider(color = PositivusBorder.copy(alpha = 0.15f), thickness = 0.8.dp)
          Spacer(modifier = Modifier.height(8.dp))

          // Performed items checklist summary
          Text("CÁC HẠNG MỤC ĐÃ THỰC HIỆN:", fontSize = 10.sp, fontWeight = FontWeight.Black, color = TextGray)
          Spacer(modifier = Modifier.height(4.dp))
          record.performedItems.forEach { item ->
            Row(
              verticalAlignment = Alignment.Top,
              horizontalArrangement = Arrangement.spacedBy(6.dp),
              modifier = Modifier.padding(vertical = 1.dp)
            ) {
              Icon(Icons.Filled.Check, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(15.dp))
              Text(text = item, fontSize = 12.sp, color = PositivusDark)
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Bottom Action Row: Invoice button & Amount
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text("TỔNG HÓA ĐƠN", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextGray)
              Text(
                text = AppRepository.formatVnd(record.invoice.totalVnd),
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = PositivusDark,
                fontFamily = FontFamily.Monospace
              )
            }

            Surface(
              shape = RoundedCornerShape(12.dp),
              color = PositivusGreen,
              border = BorderStroke(1.5.dp, PositivusBorder),
              modifier = Modifier.clickable {
                selectedRecordForInvoiceModal = record
              }
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Icon(Icons.Filled.ReceiptLong, contentDescription = null, tint = PositivusDark, modifier = Modifier.size(16.dp))
                Text(
                  text = "Xem Hóa Đơn",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Black,
                  color = PositivusDark
                )
              }
            }
          }
        }
      }
    }
  }

  // --- Modal Chi Tiết Hóa Đơn Điện Tử (E-Invoice Modal) ---
  if (selectedRecordForInvoiceModal != null) {
    val rec = selectedRecordForInvoiceModal!!
    val inv = rec.invoice

    AlertDialog(
      onDismissRequest = { selectedRecordForInvoiceModal = null },
      title = {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("HÓA ĐƠN DỊCH VỤ", fontWeight = FontWeight.Black, fontSize = 16.sp)
          BrutalPill(
            text = "ĐÃ THANH TOÁN",
            backgroundColor = PositivusGreen,
            textColor = PositivusDark
          )
        }
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = PositivusGray,
            border = BorderStroke(1.dp, PositivusBorder),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Text("Mã hóa đơn: ${inv.invoiceNumber}", fontWeight = FontWeight.Bold, fontSize = 13.sp, fontFamily = FontFamily.Monospace)
              Text("Ngày lập: ${inv.issueDate}", fontSize = 12.sp, color = TextGray)
              Text("Đơn vị: ${rec.branchName} • iVehicle Vietnam", fontSize = 12.sp, color = TextGray)
              Text("Phương tiện: ${rec.vehiclePlate} (${rec.vehicleModel})", fontSize = 12.sp, fontWeight = FontWeight.Bold)
              Text("Kỹ thuật viên thực hiện: ${rec.technicianName}", fontSize = 12.sp, color = TextGray)
            }
          }

          Text("CHI TIẾT THANH TOÁN:", fontSize = 11.sp, fontWeight = FontWeight.Black)

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Tiền dịch vụ & phụ tùng:", fontSize = 12.sp, color = TextGray)
            Text(AppRepository.formatVnd(inv.subtotalVnd), fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Thuế VAT (8%):", fontSize = 12.sp, color = TextGray)
            Text(AppRepository.formatVnd(inv.vatVnd), fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Phương thức thanh toán:", fontSize = 12.sp, color = TextGray)
            Text(inv.paymentMethod.label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
          }

          Divider(color = PositivusBorder, thickness = 1.dp)

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("TỔNG THANH TOÁN:", fontSize = 14.sp, fontWeight = FontWeight.Black)
            Text(
              AppRepository.formatVnd(inv.totalVnd),
              fontSize = 16.sp,
              fontWeight = FontWeight.Black,
              color = PositivusDark,
              fontFamily = FontFamily.Monospace
            )
          }

          Text(
            text = "Tra cứu hóa đơn điện tử tại: ${inv.eInvoiceUrl}${inv.invoiceNumber}",
            fontSize = 10.sp,
            color = TextGray
          )
        }
      },
      confirmButton = {
        Button(
          onClick = { selectedRecordForInvoiceModal = null },
          colors = ButtonDefaults.buttonColors(containerColor = PositivusDark),
          shape = RoundedCornerShape(12.dp)
        ) {
          Text("Đóng hóa đơn")
        }
      }
    )
  }
}
