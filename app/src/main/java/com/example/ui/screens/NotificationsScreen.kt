package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.model.FcmMaintenanceSchedule
import com.example.model.NotificationChannel
import com.example.model.NotificationMessage
import com.example.model.NotificationType
import com.example.repository.AppRepository
import com.example.ui.components.BrutalCard
import com.example.ui.components.BrutalPill
import com.example.ui.components.brutalShadow
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
  onBack: () -> Unit,
  onViewQuote: (String) -> Unit = {},
  onViewAppointments: () -> Unit = {}
) {
  val context = LocalContext.current
  val notifications by AppRepository.notifications.collectAsState()
  val fcmSchedules by AppRepository.fcmMaintenanceSchedules.collectAsState()

  var selectedTab by remember { mutableIntStateOf(0) } // 0: Tất cả, 1: Firebase FCM Bảo dưỡng, 2: Tiến độ sửa xe, 3: Nhắc lịch hẹn

  // Notification permission handling for Android 13+ (POST_NOTIFICATIONS)
  var hasNotificationPermission by remember {
    mutableStateOf(
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
          context,
          Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
      } else {
        true
      }
    )
  }

  val notificationPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    hasNotificationPermission = isGranted
  }

  val filteredNotifications = notifications.filter { notif ->
    when (selectedTab) {
      1 -> notif.channel == NotificationChannel.FIREBASE_FCM || notif.type == NotificationType.MAINTENANCE_DUE
      2 -> notif.type == NotificationType.SERVICE_PROGRESS
      3 -> notif.type == NotificationType.APPOINTMENT_REMINDER
      else -> true
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = "Trung Tâm Thông Báo & FCM",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)
            )
            Text(
              text = "Firebase Cloud Messaging: Nhắc lịch bảo dưỡng định kỳ",
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
      // 1. Firebase Cloud Messaging Permission / Status Card
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .brutalShadow(offsetY = 2.dp, color = PositivusBorder, cornerRadius = 18.dp)
        ) {
          Surface(
            shape = RoundedCornerShape(18.dp),
            color = if (hasNotificationPermission) Color(0xFFF0FDF4) else Color(0xFFFEF3C7),
            border = BorderStroke(1.5.dp, PositivusBorder),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
              ) {
                Box(
                  modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (hasNotificationPermission) PositivusGreen else Color(0xFFFBBF24)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Filled.CloudDone,
                    contentDescription = null,
                    tint = PositivusDark,
                    modifier = Modifier.size(20.dp)
                  )
                }

                Column {
                  Text(
                    text = "Firebase Cloud Messaging (FCM)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = PositivusDark
                  )
                  Text(
                    text = if (hasNotificationPermission)
                      "Thiết bị đã kết nối dịch vụ thông báo đẩy bảo dưỡng."
                    else
                      "Cần cấp quyền để nhận cảnh báo bảo dưỡng khi đóng app.",
                    fontSize = 11.sp,
                    color = TextGray
                  )
                }
              }

              if (!hasNotificationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = PositivusGreen,
                  border = BorderStroke(1.dp, PositivusBorder),
                  modifier = Modifier.clickable {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                  }
                ) {
                  Text(
                    text = "Bật FCM",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = PositivusDark,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                  )
                }
              }
            }
          }
        }
      }

      // 2. Section: FCM Upcoming Maintenance Schedules (Kế hoạch bảo dưỡng sắp tới từ Cloud)
      item {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "LỊCH NHẮC BẢO DƯỠNG XE TỪ FIREBASE (FCM SCHEDULE)",
              fontSize = 11.sp,
              fontWeight = FontWeight.Black,
              color = PositivusDark,
              letterSpacing = 0.5.sp
            )

            Text(
              text = "${fcmSchedules.size} lịch hẹn",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = TextGray
            )
          }

          fcmSchedules.forEach { schedule ->
            BrutalCard(
              modifier = Modifier.fillMaxWidth(),
              backgroundColor = if (schedule.isSent) Color(0xFFF9FAFB) else Color.White,
              cornerRadius = 18.dp,
              contentPadding = PaddingValues(14.dp)
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
                      color = PositivusDark
                    ) {
                      Text(
                        text = schedule.vehiclePlate,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                      )
                    }

                    BrutalPill(
                      text = schedule.urgencyLevel,
                      backgroundColor = if (schedule.urgencyLevel.contains("sớm")) Color(0xFFFEE2E2) else PositivusGreen,
                      textColor = if (schedule.urgencyLevel.contains("sớm")) AlertRed else PositivusDark
                    )
                  }

                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = schedule.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = PositivusDark
                  )
                  Text(
                    text = schedule.body,
                    fontSize = 12.sp,
                    color = TextGray,
                    lineHeight = 16.sp
                  )

                  Spacer(modifier = Modifier.height(6.dp))
                  Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = "📅 Ngày gửi: ${schedule.scheduledDate}",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      color = PositivusDark
                    )
                    Text(
                      text = "🚗 Mốc: ${schedule.targetOdoKm} km",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      color = PrimaryBlue
                    )
                  }
                }

                // FCM Cloud Send/Test Button
                Column(horizontalAlignment = Alignment.End) {
                  if (schedule.isSent) {
                    BrutalPill(
                      text = "ĐÃ GỬI FCM",
                      backgroundColor = PositivusGray,
                      textColor = TextGray
                    )
                  } else {
                    Surface(
                      shape = RoundedCornerShape(10.dp),
                      color = PositivusGreen,
                      border = BorderStroke(1.2.dp, PositivusBorder),
                      modifier = Modifier.clickable {
                        AppRepository.dispatchFcmMaintenanceReminder(schedule.id)
                      }
                    ) {
                      Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                      ) {
                        Icon(
                          imageVector = Icons.Filled.Send,
                          contentDescription = null,
                          tint = PositivusDark,
                          modifier = Modifier.size(13.dp)
                        )
                        Text(
                          text = "Bắn FCM",
                          fontSize = 11.sp,
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
        }
      }

      // 3. Filter Category Pills for notification inbox
      item {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Text(
            text = "HỘP THƯ THÔNG BÁO VÀ TIẾN ĐỘ XE",
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            color = PositivusDark,
            letterSpacing = 0.5.sp
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            listOf("Tất cả", "FCM Bảo dưỡng", "Tiến độ xe", "Lịch hẹn").forEachIndexed { index, label ->
              val isSelected = selectedTab == index
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) PositivusDark else Color.White,
                border = BorderStroke(1.5.dp, PositivusBorder),
                modifier = Modifier.clickable { selectedTab = index }
              ) {
                Text(
                  text = label,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isSelected) Color.White else PositivusDark,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                )
              }
            }
          }
        }
      }

      // 4. Notification Cards List
      items(filteredNotifications) { notif ->
        BrutalCard(
          modifier = Modifier.fillMaxWidth(),
          backgroundColor = if (notif.isRead) Color(0xFFF9FAFB) else Color.White,
          cornerRadius = 20.dp,
          contentPadding = PaddingValues(16.dp),
          onClick = {
            AppRepository.markNotificationAsRead(notif.id)
            if (notif.type == NotificationType.QUOTE_ALERT) {
              onViewQuote("BG-882101")
            } else if (notif.type == NotificationType.APPOINTMENT_REMINDER || notif.type == NotificationType.SERVICE_PROGRESS) {
              onViewAppointments()
            }
          }
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
              // Icon Badge according to NotificationType & Channel
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(RoundedCornerShape(10.dp))
                  .background(
                    when (notif.channel) {
                      NotificationChannel.FIREBASE_FCM -> PositivusGreen
                      NotificationChannel.ZALO_OA -> Color(0xFF0068FF).copy(alpha = 0.15f)
                      NotificationChannel.SMS -> PositivusGray
                      NotificationChannel.IN_APP -> Color(0xFF007AFF).copy(alpha = 0.15f)
                    }
                  ),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = when (notif.channel) {
                    NotificationChannel.FIREBASE_FCM -> Icons.Filled.NotificationsActive
                    NotificationChannel.ZALO_OA -> Icons.Filled.Chat
                    NotificationChannel.SMS -> Icons.Filled.Sms
                    NotificationChannel.IN_APP -> Icons.Filled.Build
                  },
                  contentDescription = null,
                  tint = PositivusDark,
                  modifier = Modifier.size(18.dp)
                )
              }

              Column {
                Text(
                  text = notif.type.label,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = TextGray
                )
                Text(
                  text = "Kênh: ${notif.channel.label}",
                  fontSize = 10.sp,
                  color = if (notif.channel == NotificationChannel.FIREBASE_FCM) Color(0xFFE65100) else TextGray,
                  fontWeight = if (notif.channel == NotificationChannel.FIREBASE_FCM) FontWeight.Bold else FontWeight.Normal
                )
              }
            }

            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              if (!notif.isRead) {
                Box(
                  modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(AlertRed)
                )
              }
              Text(
                text = notif.timestampText,
                fontSize = 11.sp,
                color = TextGray
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = notif.title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Black,
            color = PositivusDark
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = notif.body,
            fontSize = 13.sp,
            color = PositivusDark.copy(alpha = 0.85f),
            lineHeight = 18.sp
          )

          if (notif.bookingId != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = PositivusBorder.copy(alpha = 0.15f), thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Mã đơn liên kết: #${notif.bookingId}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
              )

              Text(
                text = "Xem chi tiết →",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = PositivusDark
              )
            }
          }
        }
      }
    }
  }
}
