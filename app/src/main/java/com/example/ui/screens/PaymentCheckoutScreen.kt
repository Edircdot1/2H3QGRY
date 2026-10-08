package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PaymentMethod
import com.example.repository.AppRepository
import com.example.ui.components.GlassCard
import com.example.ui.components.GroundCTAButton
import com.example.ui.components.StatusPill
import com.example.ui.theme.*

import androidx.compose.foundation.lazy.items

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentCheckoutScreen(
  amountVnd: Long,
  bookingId: String = "IV-8821",
  quoteId: String = "BG-882101",
  onBack: () -> Unit,
  onPaymentSuccess: (String) -> Unit
) {
  var selectedMethod by remember { mutableStateOf(PaymentMethod.VIETQR) }
  var isProcessing by remember { mutableStateOf(false) }
  val availableMethods = remember { PaymentMethod.entries }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = "Cổng Thanh Toán Dịch Vụ",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
              text = "Mã giao dịch đính kèm: $bookingId",
              style = MaterialTheme.typography.labelSmall.copy(color = PrimaryBlue)
            )
          }
        },
        navigationIcon = {
          IconButton(onClick = onBack) {
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
      LazyColumn(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Amount Due Showcase
        item {
          GlassCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 24.dp
          ) {
            Column(
              modifier = Modifier.fillMaxWidth(),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = "SỐ TIỀN CẦN THANH TOÁN",
                style = MaterialTheme.typography.labelSmall.copy(
                  color = LabelTertiary,
                  letterSpacing = 1.sp
                )
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = AppRepository.formatVnd(amountVnd),
                style = MaterialTheme.typography.headlineLarge.copy(
                  fontWeight = FontWeight.Bold,
                  color = PrimaryBlue
                )
              )
              Spacer(modifier = Modifier.height(6.dp))
              StatusPill(
                text = "Thanh toán dịch vụ & bảo hiểm phụ tùng iVehicle",
                tintColor = TertiaryGreen
              )
            }
          }
        }

        // VietQR Code Box (Simulated Standard Banking Transfer Code)
        if (selectedMethod == PaymentMethod.VIETQR) {
          item {
            GlassCard(
              modifier = Modifier.fillMaxWidth(),
              cornerRadius = 24.dp
            ) {
              Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(
                  text = "QUÉT MÃ VIETQR ĐỂ THANH TOÁN",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Realistic QR Graphic Container
                Box(
                  modifier = Modifier
                    .size(190.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(2.dp, Color(0xFFE2E2E7), RoundedCornerShape(16.dp))
                    .padding(12.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                  ) {
                    // Top QR header mock
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                      Box(modifier = Modifier.size(36.dp).background(Color.Black))
                      Text("NAPAS 247", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                      Box(modifier = Modifier.size(36.dp).background(Color.Black))
                    }

                    // QR center emblem
                    Box(
                      modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(PrimaryBlue),
                      contentAlignment = Alignment.Center
                    ) {
                      Text("iV", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    // Bottom QR footer mock
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                      Box(modifier = Modifier.size(36.dp).background(Color.Black))
                      Text(bookingId, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                      Box(modifier = Modifier.size(36.dp).background(Color.Black))
                    }
                  }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                  text = "Ngân hàng TMCP Quân Đội (MB Bank)",
                  style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                  text = "STK: 0988123456 • CTY CP IVEHICLE VIETNAM",
                  style = MaterialTheme.typography.bodySmall.copy(color = LabelSecondary)
                )
                Text(
                  text = "Nội dung CK: $bookingId",
                  style = MaterialTheme.typography.labelMedium.copy(
                    color = PrimaryBlue,
                    fontWeight = FontWeight.Bold
                  )
                )
              }
            }
          }
        }

        // Method Selection List
        item {
          Text(
            text = "CHỌN PHƯƠNG THỨC THANH TOÁN",
            style = MaterialTheme.typography.labelSmall.copy(color = LabelTertiary)
          )
        }

        items(availableMethods) { method: PaymentMethod ->
          val isSelected = method == selectedMethod
          GlassCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 18.dp,
            borderAlpha = if (isSelected) 0.9f else 0.4f,
            onClick = { selectedMethod = method }
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
                  onClick = { selectedMethod = method },
                  colors = RadioButtonDefaults.colors(selectedColor = PrimaryBlue)
                )

                Icon(
                  imageVector = when (method) {
                    PaymentMethod.VIETQR -> Icons.Filled.QrCode2
                    PaymentMethod.CASH -> Icons.Filled.AttachMoney
                    PaymentMethod.CARD -> Icons.Filled.CreditCard
                    PaymentMethod.E_WALLET -> Icons.Filled.AccountBalanceWallet
                  },
                  contentDescription = null,
                  tint = if (isSelected) PrimaryBlue else LabelSecondary
                )

                Text(
                  text = method.label,
                  style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                  )
                )
              }

              if (method == PaymentMethod.VIETQR) {
                StatusPill(text = "Khuyên dùng", tintColor = TertiaryGreen)
              }
            }
          }
        }
      }

      // Bottom confirmation CTA
      Spacer(modifier = Modifier.height(10.dp))
      GroundCTAButton(
        text = if (isProcessing) "Đang Xác Nhận Giao Dịch..." else "Xác Nhận Đã Chuyển Khoản / Thu Tiền",
        subtext = "Hệ thống tự động đối soát và kích hoạt lệnh thực hiện",
        icon = Icons.Filled.Check,
        enabled = !isProcessing,
        onClick = {
          isProcessing = true
          val transaction = AppRepository.confirmPayment(
            bookingId = bookingId,
            quoteId = quoteId,
            amount = amountVnd,
            method = selectedMethod
          )
          onPaymentSuccess(transaction.id)
        }
      )
      Spacer(modifier = Modifier.height(8.dp))
    }
  }
}
