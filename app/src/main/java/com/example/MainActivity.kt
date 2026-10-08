package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UserRole
import com.example.repository.AppRepository
import com.example.ui.components.brutalShadow
import com.example.ui.screens.*
import com.example.ui.theme.*

sealed class AppDestination {
  object Home : AppDestination()
  object Appointments : AppDestination()
  object Garage : AppDestination()
  object Technician : AppDestination()
  object Management : AppDestination()
  object ServiceBaysManager : AppDestination()
  object Warranty : AppDestination()
  object Notifications : AppDestination()
  object ServiceHistory : AppDestination()
  object NearbyMap : AppDestination()
  data class BookingFlow(val preselectedVehicleId: String? = null) : AppDestination()
  data class QuoteDetail(val quoteId: String) : AppDestination()
  data class Payment(val amountVnd: Long, val bookingId: String = "IV-8821") : AppDestination()
}

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        MainAppContent()
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent() {
  val currentUser by AppRepository.currentUser.collectAsState()
  var currentDestination by remember { mutableStateOf<AppDestination>(AppDestination.Home) }

  // Handle Android system back button
  BackHandler(enabled = currentDestination != AppDestination.Home) {
    currentDestination = AppDestination.Home
  }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    containerColor = PositivusCanvas,
    contentWindowInsets = WindowInsets.safeDrawing,
    topBar = {
      // Top App Bar as in HTML: fixed top header with brand and profile icon
      Surface(
        color = Color.White.copy(alpha = 0.96f),
        border = BorderStroke(1.5.dp, PositivusBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(64.dp)
            .padding(horizontal = 16.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            // Lime square badge with car icon
            Box(
              modifier = Modifier
                .size(40.dp)
                .brutalShadow(
                  offsetY = 2.dp,
                  color = PositivusBorder,
                  cornerRadius = 14.dp
                )
            ) {
              Surface(
                shape = RoundedCornerShape(14.dp),
                color = PositivusGreen,
                border = BorderStroke(1.5.dp, PositivusBorder),
                modifier = Modifier.fillMaxSize()
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(
                    imageVector = Icons.Filled.DirectionsCar,
                    contentDescription = null,
                    tint = PositivusDark,
                    modifier = Modifier.size(22.dp)
                  )
                }
              }
            }

            Column {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Surface(
                  shape = RoundedCornerShape(4.dp),
                  color = PositivusGreen,
                  border = BorderStroke(1.dp, PositivusBorder)
                ) {
                  Text(
                    text = "IVEHICLE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = PositivusDark,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
                Box(
                  modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF10B981))
                )
              }
              Text(
                text = when (currentDestination) {
                  is AppDestination.Home -> "Trang Chủ"
                  is AppDestination.Garage -> "Garage Của Tôi"
                  is AppDestination.Appointments -> "Lịch Hẹn Dịch Vụ"
                  is AppDestination.Technician -> "Kỹ Thuật Xưởng"
                  is AppDestination.Management -> "Quản Lý Hệ Thống"
                  is AppDestination.ServiceBaysManager -> "Giám Sát Khoang Xưởng"
                  is AppDestination.BookingFlow -> "Đặt Lịch Dịch Vụ"
                  is AppDestination.QuoteDetail -> "Chi Tiết Báo Giá"
                  is AppDestination.Payment -> "Cổng Thanh Toán"
                  is AppDestination.Warranty -> "Tra Cứu Bảo Hành"
                  is AppDestination.Notifications -> "Hộp Thư & Hậu Mãi"
                  is AppDestination.ServiceHistory -> "Lịch Sử Bảo Dưỡng"
                  is AppDestination.NearbyMap -> "Bản Đồ Trạm Gần Nhất"
                },
                fontSize = 17.sp,
                fontWeight = FontWeight.Black,
                color = PositivusDark,
                lineHeight = 20.sp
              )
            }
          }

          // Top Right Action Buttons (Notifications Bell + Profile)
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            // Notification Bell with unread counter
            val notifications by AppRepository.notifications.collectAsState()
            val unreadCount = notifications.count { !it.isRead }

            Box(
              modifier = Modifier.brutalShadow(
                offsetY = 2.dp,
                color = PositivusBorder,
                cornerRadius = 14.dp
              )
            ) {
              Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                border = BorderStroke(1.5.dp, PositivusBorder),
                modifier = Modifier
                  .size(40.dp)
                  .clickable {
                    currentDestination = AppDestination.Notifications
                  }
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = "Thông báo hệ thống",
                    tint = PositivusDark,
                    modifier = Modifier.size(22.dp)
                  )
                  if (unreadCount > 0) {
                    Box(
                      modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 4.dp, end = 4.dp)
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(AlertRed),
                      contentAlignment = Alignment.Center
                    ) {
                      Text(
                        text = "$unreadCount",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black
                      )
                    }
                  }
                }
              }
            }

            // Profile action button
            Box(
              modifier = Modifier.brutalShadow(
                offsetY = 2.dp,
                color = PositivusBorder,
                cornerRadius = 14.dp
              )
            ) {
              Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                border = BorderStroke(1.5.dp, PositivusBorder),
                modifier = Modifier
                  .size(40.dp)
                  .clickable {
                    currentDestination = AppDestination.Management
                  }
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Hồ sơ người dùng",
                    tint = PositivusDark,
                    modifier = Modifier.size(22.dp)
                  )
                }
              }
            }
          }
        }
      }
    },
    bottomBar = {
      val isRootTab = currentDestination is AppDestination.Home ||
          currentDestination is AppDestination.Appointments ||
          currentDestination is AppDestination.Garage ||
          currentDestination is AppDestination.Technician ||
          currentDestination is AppDestination.Management

      if (isRootTab) {
        PositivusBrutalBottomBar(
          currentDestination = currentDestination,
          onNavigate = { currentDestination = it }
        )
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      when (val dest = currentDestination) {
        is AppDestination.Home -> CustomerHomeScreen(
          onNavigateToBooking = { currentDestination = AppDestination.BookingFlow() },
          onNavigateToVehicles = { currentDestination = AppDestination.Garage },
          onNavigateToAppointments = { currentDestination = AppDestination.Appointments },
          onViewQuote = { quoteId -> currentDestination = AppDestination.QuoteDetail(quoteId) },
          onNavigateToWarranty = { currentDestination = AppDestination.Warranty },
          onNavigateToNotifications = { currentDestination = AppDestination.Notifications },
          onNavigateToServiceHistory = { currentDestination = AppDestination.ServiceHistory },
          onNavigateToNearbyMap = { currentDestination = AppDestination.NearbyMap }
        )

        is AppDestination.NearbyMap -> NearbyStationsMapScreen(
          onBack = { currentDestination = AppDestination.Home },
          onSelectStationToBook = { branch ->
            currentDestination = AppDestination.BookingFlow()
          }
        )

        is AppDestination.ServiceHistory -> ServiceHistoryScreen(
          onBack = { currentDestination = AppDestination.Home }
        )

        is AppDestination.Warranty -> WarrantyLookupScreen(
          onBack = { currentDestination = AppDestination.Home }
        )

        is AppDestination.Notifications -> NotificationsScreen(
          onBack = { currentDestination = AppDestination.Home },
          onViewQuote = { quoteId -> currentDestination = AppDestination.QuoteDetail(quoteId) },
          onViewAppointments = { currentDestination = AppDestination.Appointments }
        )

        is AppDestination.Garage -> CustomerGarageScreen(
          onBookService = { currentDestination = AppDestination.BookingFlow() }
        )

        is AppDestination.Appointments -> TechnicianWorkspaceScreen()

        is AppDestination.Technician -> TechnicianWorkspaceScreen()

        is AppDestination.Management -> ManagementDashboardScreen(
          onOpenBayManagement = { currentDestination = AppDestination.ServiceBaysManager }
        )

        is AppDestination.ServiceBaysManager -> ServiceBaysManagerScreen(
          onBack = { currentDestination = AppDestination.Management }
        )

        is AppDestination.BookingFlow -> BookingFlowScreen(
          onBack = { currentDestination = AppDestination.Home },
          onBookingSuccess = { currentDestination = AppDestination.Home }
        )

        is AppDestination.QuoteDetail -> QuoteDetailScreen(
          quoteId = dest.quoteId,
          onBack = { currentDestination = AppDestination.Home },
          onOpenPayment = { amount ->
            currentDestination = AppDestination.Payment(amountVnd = amount)
          }
        )

        is AppDestination.Payment -> PaymentCheckoutScreen(
          amountVnd = dest.amountVnd,
          bookingId = dest.bookingId,
          onBack = { currentDestination = AppDestination.Home },
          onPaymentSuccess = {
            currentDestination = AppDestination.Home
          }
        )
      }
    }
  }
}

/**
 * Neo-Brutalist Positivus Bottom Navigation Bar:
 * White pill with 1.5px black border, solid brutal shadow, and lime active indicator
 */
@Composable
fun PositivusBrutalBottomBar(
  currentDestination: AppDestination,
  onNavigate: (AppDestination) -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .navigationBarsPadding()
      .padding(horizontal = 16.dp, vertical = 10.dp),
    contentAlignment = Alignment.Center
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .brutalShadow(
          offsetY = 4.dp,
          color = PositivusBorder,
          cornerRadius = 99.dp
        )
    ) {
      Surface(
        shape = RoundedCornerShape(99.dp),
        color = Color.White,
        border = BorderStroke(1.5.dp, PositivusBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = 6.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceAround
        ) {
          // Tab 1: Trang chủ
          val isHome = currentDestination is AppDestination.Home
          BottomNavItem(
            title = "Trang chủ",
            icon = Icons.Filled.Home,
            isSelected = isHome,
            onClick = { onNavigate(AppDestination.Home) }
          )

          // Tab 2: Lịch hẹn
          val isAppointments = currentDestination is AppDestination.Appointments
          BottomNavItem(
            title = "Lịch hẹn",
            icon = Icons.Filled.CalendarMonth,
            isSelected = isAppointments,
            onClick = { onNavigate(AppDestination.Appointments) }
          )

          // Tab 3: Garage
          val isGarage = currentDestination is AppDestination.Garage
          BottomNavItem(
            title = "Garage",
            icon = Icons.Filled.Garage,
            isSelected = isGarage,
            onClick = { onNavigate(AppDestination.Garage) }
          )

          // Tab 4: Tài khoản / Quản lý
          val isAccount = currentDestination is AppDestination.Management
          BottomNavItem(
            title = "Tài khoản",
            icon = Icons.Filled.ManageAccounts,
            isSelected = isAccount,
            onClick = { onNavigate(AppDestination.Management) }
          )
        }
      }
    }
  }
}

@Composable
private fun BottomNavItem(
  title: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  Column(
    modifier = Modifier
      .width(72.dp)
      .fillMaxHeight()
      .clickable(onClick = onClick),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    if (isSelected) {
      Box(
        modifier = Modifier
          .size(32.dp)
          .clip(CircleShape)
          .background(PositivusGreen)
          .border(1.2.dp, PositivusBorder, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = title,
          tint = PositivusDark,
          modifier = Modifier.size(18.dp)
        )
      }
    } else {
      Icon(
        imageVector = icon,
        contentDescription = title,
        tint = TextGray,
        modifier = Modifier.size(20.dp)
      )
    }

    Spacer(modifier = Modifier.height(2.dp))
    Text(
      text = title,
      fontSize = 10.sp,
      fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
      color = if (isSelected) PositivusDark else TextGray,
      letterSpacing = (-0.3).sp
    )
  }
}
