package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import com.example.model.PaymentMethod
import com.example.model.Quote
import com.example.model.QuoteItem
import com.example.repository.AppRepository
import com.example.ui.components.GlassCard
import com.example.ui.components.GroundCTAButton
import com.example.ui.components.StatusPill
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuoteDetailScreen(
  quoteId: String,
  onBack: () -> Unit,
  onOpenPayment: (Long) -> Unit
) {
  val quotes by AppRepository.quotes.collectAsState()
  val quote = quotes.find { it.id == quoteId } ?: quotes.firstOrNull()

  var showConfirmDialog by remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = "Báo Giá Dịch Vụ & Phụ Tùng",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
              text = "Mã: ${quote?.id ?: quoteId} • Phiên bản v${quote?.version ?: 1}",
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
    if (quote == null) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(padding),
        contentAlignment = Alignment.Center
      ) {
        Text("Không tìm thấy báo giá")
      }
      return@Scaffold
    }

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
        // Validity & Legal notice card
        item {
          GlassCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 20.dp,
            contentPadding = PaddingValues(14.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Icon(
                  imageVector = Icons.Filled.VerifiedUser,
                  contentDescription = null,
                  tint = TertiaryGreen,
                  modifier = Modifier.size(20.dp)
                )
                Text(
                  text = "Hiệu lực 15 ngày kể từ ngày lập",
                  style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                )
              }

              StatusPill(
                text = if (quote.isSignedByCustomer) "ĐÃ PHÊ DUYỆT" else "CHỜ KHÁCH DUYỆT",
                tintColor = if (quote.isSignedByCustomer) TertiaryGreen else PrimaryBlue
              )
            }
          }
        }

        // Section header
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "DANH MỤC CHI TIẾT (CHỌN TỪNG HẠNG MỤC)",
              style = MaterialTheme.typography.labelSmall.copy(color = LabelTertiary)
            )
            Text(
              text = "${quote.items.count { it.isApproved }}/${quote.items.size} đã chọn",
              style = MaterialTheme.typography.labelSmall.copy(color = PrimaryBlue)
            )
          }
        }

        // Item list with individual checkboxes
        items(quote.items) { item ->
          QuoteItemCard(
            item = item,
            isLocked = quote.isSignedByCustomer,
            onToggle = {
              AppRepository.toggleQuoteItemApproval(quote.id, item.id)
            }
          )
        }

        // Unapproved items notice
        val unapprovedCount = quote.items.count { !it.isApproved }
        if (unapprovedCount > 0) {
          item {
            GlassCard(
              modifier = Modifier.fillMaxWidth(),
              cornerRadius = 16.dp,
              contentPadding = PaddingValues(12.dp)
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Icon(
                  imageVector = Icons.Filled.Info,
                  contentDescription = null,
                  tint = LabelSecondary,
                  modifier = Modifier.size(18.dp)
                )
                Text(
                  text = "$unapprovedCount hạng mục không duyệt sẽ được lưu lại hệ thống để nhắc nhở và theo dõi trong lần bảo dưỡng sau.",
                  style = MaterialTheme.typography.bodySmall.copy(color = LabelSecondary)
                )
              }
            }
          }
        }

        // Price Summary
        item {
          GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 24.dp) {
            Text(
              text = "TỔNG KẾT BÁO GIÁ",
              style = MaterialTheme.typography.labelSmall.copy(
                color = PrimaryBlue,
                letterSpacing = 1.sp
              )
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "Tổng chi phí đề xuất:",
                style = MaterialTheme.typography.bodyMedium.copy(color = LabelSecondary)
              )
              Text(
                text = AppRepository.formatVnd(quote.totalProposedAmountVnd),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
              )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Tổng tiền được khách duyệt:",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
              )
              Text(
                text = AppRepository.formatVnd(quote.totalApprovedAmountVnd),
                style = MaterialTheme.typography.headlineMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = PrimaryBlue
                )
              )
            }
          }
        }
      }

      // Bottom Action
      Spacer(modifier = Modifier.height(10.dp))
      if (!quote.isSignedByCustomer) {
        GroundCTAButton(
          text = "Ký Duyệt & Chấp Thuận Các Hạng Mục",
          subtext = "Tổng thanh toán dự kiến: ${AppRepository.formatVnd(quote.totalApprovedAmountVnd)}",
          icon = Icons.Filled.Draw,
          onClick = { showConfirmDialog = true }
        )
      } else {
        GroundCTAButton(
          text = "Tiến Hành Thanh Toán Ngay",
          subtext = "Hỗ trợ VietQR, Tiền mặt & Thẻ liên ngân hàng",
          icon = Icons.Filled.Payment,
          onClick = { onOpenPayment(quote.totalApprovedAmountVnd) }
        )
      }
      Spacer(modifier = Modifier.height(8.dp))
    }
  }

  // Digital Signature Approval Confirmation Dialog
  if (showConfirmDialog) {
    AlertDialog(
      onDismissRequest = { showConfirmDialog = false },
      title = {
        Text(
          "Xác Nhận Ký Duyệt Báo Giá",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
      },
      text = {
        Text(
          "Bạn đang đồng ý duyệt ${quote?.items?.count { it.isApproved } ?: 0} hạng mục với tổng chi phí ${AppRepository.formatVnd(quote?.totalApprovedAmountVnd ?: 0L)}. Hệ thống sẽ giữ chỗ phụ tùng và bắt đầu phân công xưởng thực hiện.",
          style = MaterialTheme.typography.bodyMedium
        )
      },
      confirmButton = {
        Button(
          onClick = {
            if (quote != null) {
              AppRepository.approveQuote(quote.id)
            }
            showConfirmDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
          Text("Đồng ý ký duyệt")
        }
      },
      dismissButton = {
        TextButton(onClick = { showConfirmDialog = false }) {
          Text("Xem lại")
        }
      }
    )
  }
}

@Composable
private fun QuoteItemCard(
  item: QuoteItem,
  isLocked: Boolean,
  onToggle: () -> Unit
) {
  GlassCard(
    modifier = Modifier.fillMaxWidth(),
    cornerRadius = 20.dp,
    borderAlpha = if (item.isApproved) 0.8f else 0.3f
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.Top
    ) {
      Row(
        modifier = Modifier.weight(1f),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Checkbox(
          checked = item.isApproved,
          onCheckedChange = { if (!isLocked) onToggle() },
          enabled = !isLocked,
          colors = CheckboxDefaults.colors(checkedColor = PrimaryBlue)
        )

        Column {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Text(
              text = item.name,
              style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = if (item.isApproved) LabelPrimary else LabelTertiary
              )
            )
          }

          Spacer(modifier = Modifier.height(4.dp))

          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatusPill(
              text = if (item.isPart) "Phụ tùng thay thế" else "Công thợ / Dịch vụ",
              tintColor = if (item.isPart) Color(0xFF0058BC) else Color(0xFF5F5E61)
            )
            StatusPill(
              text = "SL: ${item.quantity}",
              tintColor = LabelSecondary
            )
            StatusPill(
              text = "BH ${item.warrantyDays}N",
              tintColor = TertiaryGreen
            )
          }
        }
      }

      Column(horizontalAlignment = Alignment.End) {
        Text(
          text = AppRepository.formatVnd(item.totalPriceVnd),
          style = MaterialTheme.typography.titleSmall.copy(
            fontWeight = FontWeight.Bold,
            color = if (item.isApproved) PrimaryBlue else LabelTertiary
          )
        )
        if (item.quantity > 1) {
          Text(
            text = "${AppRepository.formatVnd(item.unitPriceVnd)}/đv",
            style = MaterialTheme.typography.labelSmall.copy(color = LabelTertiary)
          )
        }
      }
    }
  }
}
