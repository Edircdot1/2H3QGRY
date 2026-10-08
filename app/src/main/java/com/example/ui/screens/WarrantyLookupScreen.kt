package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.WarrantyRecord
import com.example.model.WarrantyStatus
import com.example.repository.AppRepository
import com.example.ui.components.BrutalCard
import com.example.ui.components.BrutalPill
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WarrantyLookupScreen(
  onBack: () -> Unit
) {
  val warranties by AppRepository.warranties.collectAsState()
  var searchQuery by remember { mutableStateOf("") }
  var selectedTab by remember { mutableIntStateOf(0) } // 0: Tất cả, 1: Còn hạn, 2: Phụ tùng

  val filteredWarranties = warranties.filter { record ->
    val matchesQuery = record.itemName.contains(searchQuery, ignoreCase = true) ||
        record.vehiclePlate.contains(searchQuery, ignoreCase = true)
    val matchesTab = when (selectedTab) {
      1 -> record.status == WarrantyStatus.ACTIVE
      2 -> record.isPart
      else -> true
    }
    matchesQuery && matchesTab
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = "Tra Cứu & Quản Lý Bảo Hành",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)
            )
            Text(
              text = "Chính sách: Dịch vụ 7-15 ngày • Phụ tùng 180 ngày",
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
      // Search Bar
      item {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("Tìm theo tên linh kiện hoặc biển số xe...") },
          leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = PositivusDark) },
          shape = RoundedCornerShape(16.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = PositivusBorder,
            unfocusedBorderColor = PositivusBorder
          ),
          modifier = Modifier.fillMaxWidth()
        )
      }

      // Filter Tabs
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          listOf("Tất cả (${warranties.size})", "Còn hiệu lực", "Phụ tùng lắp thêm").forEachIndexed { index, label ->
            val isSelected = selectedTab == index
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = if (isSelected) PositivusDark else Color.White,
              border = BorderStroke(1.5.dp, PositivusBorder),
              modifier = Modifier
                .clickable { selectedTab = index }
            ) {
              Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else PositivusDark,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
              )
            }
          }
        }
      }

      // Warranty Records List
      items(filteredWarranties) { item ->
        BrutalCard(
          modifier = Modifier.fillMaxWidth(),
          cornerRadius = 20.dp,
          contentPadding = PaddingValues(16.dp)
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
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = PositivusDark,
                  modifier = Modifier.padding(bottom = 2.dp)
                ) {
                  Text(
                    text = item.vehiclePlate,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }

                BrutalPill(
                  text = if (item.isPart) "Phụ tùng" else "Dịch vụ xưởng",
                  backgroundColor = if (item.isPart) PositivusGreen else PositivusGray,
                  textColor = PositivusDark
                )
              }

              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = item.itemName,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = PositivusDark
              )
              Text(
                text = "Mã bảo hành: ${item.id}",
                fontSize = 11.sp,
                color = TextGray
              )
            }

            BrutalPill(
              text = item.status.label,
              backgroundColor = if (item.status == WarrantyStatus.ACTIVE) PositivusGreen else Color(0xFFFEE2E2),
              textColor = if (item.status == WarrantyStatus.ACTIVE) PositivusDark else Color(0xFFDC2626)
            )
          }

          Spacer(modifier = Modifier.height(10.dp))
          Divider(color = PositivusBorder.copy(alpha = 0.2f), thickness = 1.dp)
          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column {
              Text("THỜI GIAN ÁP DỤNG", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextGray)
              Text("${item.startDate} → ${item.endDate}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Column(horizontalAlignment = Alignment.End) {
              Text("THỜI HẠN CÒN LẠI", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextGray)
              Text(
                text = if (item.daysLeft > 0) "${item.daysLeft} ngày" else "Đã hết hạn",
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = if (item.daysLeft > 0) Color(0xFF10B981) else Color(0xFFDC2626)
              )
            }
          }

          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "📋 Điều kiện: ${item.conditionNote}",
            fontSize = 11.sp,
            color = TextGray,
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
          )
        }
      }
    }
  }
}
