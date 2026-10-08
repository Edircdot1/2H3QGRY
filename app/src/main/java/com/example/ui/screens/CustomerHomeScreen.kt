package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.BookingStatus
import com.example.repository.AppRepository
import com.example.ui.components.BrutalCard
import com.example.ui.components.BrutalPill
import com.example.ui.components.brutalShadow
import com.example.ui.theme.*

@Composable
fun CustomerHomeScreen(
  onNavigateToBooking: () -> Unit,
  onNavigateToVehicles: () -> Unit,
  onNavigateToAppointments: () -> Unit,
  onViewQuote: (String) -> Unit,
  onNavigateToWarranty: () -> Unit,
  onNavigateToNotifications: () -> Unit,
  onNavigateToServiceHistory: () -> Unit = {},
  onNavigateToNearbyMap: () -> Unit = {}
) {
  val vehicles by AppRepository.vehicles.collectAsState()
  val bookings by AppRepository.bookings.collectAsState()
  val currentUser by AppRepository.currentUser.collectAsState()

  // Selected vehicle or Toyota Corolla Cross default as in HTML mockup
  val currentVehicle = vehicles.firstOrNull()

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(PositivusCanvas),
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Status & OBD-II Bar (Positivus pill style)
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Left Pill: Sync time
        Box(
          modifier = Modifier.brutalShadow(
            offsetY = 2.dp,
            color = PositivusBorder,
            cornerRadius = 99.dp
          )
        ) {
          Surface(
            shape = RoundedCornerShape(99.dp),
            color = Color.White,
            border = BorderStroke(1.5.dp, PositivusBorder)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(10.dp)
                  .clip(CircleShape)
                  .background(PositivusGreen)
                  .border(1.dp, PositivusBorder, CircleShape)
              )
              Text(
                text = "Đồng bộ lúc 08:30 hôm nay",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = PositivusDark
              )
            }
          }
        }

        // Right Pill: OBD-II Connected
        Box(
          modifier = Modifier.brutalShadow(
            offsetY = 2.dp,
            color = PositivusBorder,
            cornerRadius = 99.dp
          )
        ) {
          Surface(
            shape = RoundedCornerShape(99.dp),
            color = PositivusGreen,
            border = BorderStroke(1.5.dp, PositivusBorder)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Icon(
                imageVector = Icons.Filled.Sensors,
                contentDescription = null,
                tint = PositivusDark,
                modifier = Modifier.size(15.dp)
              )
              Text(
                text = "Kết nối OBD-II",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = PositivusDark
              )
            }
          }
        }
      }
    }

    // 2. Thẻ XE ĐANG CHỌN (Positivus Card: Clean White, 1.5px Solid Border, Brutal Shadow)
    item {
      BrutalCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 26.dp,
        contentPadding = PaddingValues(18.dp)
      ) {
        // Top Badges & Switch Action
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          BrutalPill(
            text = "ĐÃ XÁC MINH",
            backgroundColor = PositivusGreen,
            textColor = PositivusDark,
            icon = {
              Icon(
                imageVector = Icons.Filled.Verified,
                contentDescription = null,
                tint = PositivusDark,
                modifier = Modifier.size(14.dp)
              )
            }
          )

          // Switch Car Button
          Box(
            modifier = Modifier.brutalShadow(
              offsetY = 2.dp,
              color = PositivusBorder,
              cornerRadius = 12.dp
            )
          ) {
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = PositivusGray,
              border = BorderStroke(1.5.dp, PositivusBorder),
              modifier = Modifier.clickable { onNavigateToVehicles() }
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Text(
                  text = "Đổi xe",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = PositivusDark
                )
                Icon(
                  imageVector = Icons.Filled.SwapHoriz,
                  contentDescription = null,
                  tint = PositivusDark,
                  modifier = Modifier.size(18.dp)
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Car Title & Spec + Image
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "MẪU XE CÁ NHÂN",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = TextGray,
              letterSpacing = 1.sp
            )
            Text(
              text = currentVehicle?.let { "${it.make} ${it.model}" } ?: "Toyota Corolla Cross",
              fontSize = 20.sp,
              fontWeight = FontWeight.Black,
              color = PositivusDark,
              lineHeight = 24.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            // License plate pill
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = PositivusDark,
              border = BorderStroke(1.dp, PositivusBorder),
              modifier = Modifier.padding(top = 2.dp)
            ) {
              Text(
                text = currentVehicle?.licensePlate ?: "51K-123.45",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
              )
            }
          }

          // Car Photo Thumbnail
          Box(
            modifier = Modifier.brutalShadow(
              offsetY = 2.dp,
              color = PositivusBorder,
              cornerRadius = 16.dp
            )
          ) {
            Surface(
              shape = RoundedCornerShape(16.dp),
              color = Color(0xFFF9F9FB),
              border = BorderStroke(1.5.dp, PositivusBorder),
              modifier = Modifier
                .size(width = 112.dp, height = 80.dp)
                .padding(4.dp)
            ) {
              AsyncImage(
                model = "https://lh3.googleusercontent.com/aida-public/AB6AXuBIoZ0Szn2kDEAXtAWlcB39qAI_k6LgHHu54qy3dBSGl8o7mfZxAPehmCGDpaelRQ0Qir_1q6msd6xuIRiY1ZzabgpyejDI5lQlel41_iEiRDNcOg5mvekSXHLtN_533yR9uLd975r-_4nZk5B2nG-fOTDk3VG-35f8xVawrCq21oEUgJwp2CZZM17Stay87FxLzqucDiC17MxDgsMZggHCdcsNIuBVtLJA1_hp9LDs",
                contentDescription = "Ảnh mẫu xe Toyota Corolla Cross",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Telemetry Box Grid
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // Box 1: Odo thực tế
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = PositivusGray,
            border = BorderStroke(1.5.dp, PositivusBorder),
            modifier = Modifier.weight(1f)
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .background(PositivusGreen)
                  .border(1.dp, PositivusBorder, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Filled.Speed,
                  contentDescription = null,
                  tint = PositivusDark,
                  modifier = Modifier.size(20.dp)
                )
              }
              Column {
                Text(
                  text = "Odo thực tế",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = TextGray
                )
                Text(
                  text = "${currentVehicle?.mileageKm ?: 12480} km",
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Black,
                  color = PositivusDark,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }
            }
          }

          // Box 2: Bảo dưỡng gần nhất
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = PositivusGray,
            border = BorderStroke(1.5.dp, PositivusBorder),
            modifier = Modifier.weight(1f)
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .background(Color.White)
                  .border(1.dp, PositivusBorder, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Filled.CalendarToday,
                  contentDescription = null,
                  tint = PositivusDark,
                  modifier = Modifier.size(19.dp)
                )
              }
              Column {
                Text(
                  text = "Bảo dưỡng gần nhất",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = TextGray
                )
                Text(
                  text = "Tháng 08/2026",
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Black,
                  color = PositivusDark,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }
            }
          }
        }
      }
    }

    // 3. Primary CTA: ĐẶT LỊCH DỊCH VỤ (Neo-Brutalist Lime Banner with bold arrow)
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .brutalShadow(
            offsetY = 4.dp,
            color = PositivusBorder,
            cornerRadius = 24.dp
          )
      ) {
        Surface(
          shape = RoundedCornerShape(24.dp),
          color = PositivusGreen,
          border = BorderStroke(1.5.dp, PositivusBorder),
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigateToBooking() }
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp),
              modifier = Modifier.weight(1f)
            ) {
              Box(
                modifier = Modifier
                  .size(48.dp)
                  .clip(RoundedCornerShape(16.dp))
                  .background(PositivusDark)
                  .border(1.5.dp, PositivusBorder, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Filled.CalendarMonth,
                  contentDescription = null,
                  tint = PositivusGreen,
                  modifier = Modifier.size(24.dp)
                )
              }

              Column {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  Text(
                    text = "Đặt lịch dịch vụ",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = PositivusDark
                  )
                  Surface(
                    shape = RoundedCornerShape(99.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, PositivusBorder)
                  ) {
                    Text(
                      text = "NHANH 60S",
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      color = PositivusDark,
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                  }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = "Gói bảo dưỡng, chọn xưởng & kỹ thuật viên",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Medium,
                  color = PositivusDark.copy(alpha = 0.85f),
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }
            }

            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(PositivusDark),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Filled.NorthEast,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
              )
            }
          }
        }
      }
    }

    // 4. YÊU CẦU CẦN XỬ LÝ GẤP (Dark Hero Contrast Card - #191A23 + Lime Accent)
    item {
      BrutalCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = PositivusDark,
        cornerRadius = 26.dp,
        contentPadding = PaddingValues(18.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            shape = RoundedCornerShape(99.dp),
            color = Color(0xFF30313D),
            border = BorderStroke(1.dp, Color(0xFF4B5563))
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Icon(
                imageVector = Icons.Filled.Error,
                contentDescription = null,
                tint = PositivusGreen,
                modifier = Modifier.size(15.dp)
              )
              Text(
                text = "CẦN BẠN PHÊ DUYỆT",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = PositivusGreen,
                letterSpacing = 0.5.sp
              )
            }
          }

          Surface(
            shape = RoundedCornerShape(99.dp),
            color = Color(0x33EF4444),
            border = BorderStroke(1.dp, Color(0x66EF4444))
          ) {
            Text(
              text = "Hạn chót: 18:00",
              color = Color(0xFFF87171),
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(
          text = "Báo giá bảo dưỡng định kỳ 15.000 km",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.padding(top = 4.dp)
        ) {
          Text(
            text = "Chi phí ước tính:",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF9CA3AF)
          )
          Text(
            text = "2.450.000 đ",
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            color = PositivusGreen,
            fontFamily = FontFamily.Monospace
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Button Xem & Duyệt ngay
          Box(
            modifier = Modifier
              .weight(1f)
              .brutalShadow(
                offsetY = 2.dp,
                color = PositivusBorder,
                cornerRadius = 16.dp
              )
          ) {
            Surface(
              shape = RoundedCornerShape(16.dp),
              color = PositivusGreen,
              border = BorderStroke(1.5.dp, PositivusBorder),
              modifier = Modifier
                .fillMaxWidth()
                .clickable { onViewQuote("BG-882101") }
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
              ) {
                Text(
                  text = "Xem & Duyệt ngay",
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Black,
                  color = PositivusDark
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                  imageVector = Icons.Filled.ArrowForward,
                  contentDescription = null,
                  tint = PositivusDark,
                  modifier = Modifier.size(18.dp)
                )
              }
            }
          }

          // More Options Button
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF292A32),
            border = BorderStroke(1.dp, Color(0xFF374151)),
            modifier = Modifier
              .size(48.dp)
              .clickable { /* options */ }
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Filled.MoreHoriz,
                contentDescription = "Xem thêm",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
              )
            }
          }
        }
      }
    }

    // 5. LƯỚI THAO TÁC NHANH (Positivus 4-Col Grid with Black Outline & Lime Accent)
    item {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Box(
            modifier = Modifier
              .size(10.dp)
              .background(PositivusGreen)
              .border(1.dp, PositivusBorder)
          )
          Text(
            text = "TIỆN ÍCH NHANH",
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            color = PositivusDark,
            letterSpacing = 0.8.sp
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // Item 1: Bảo dưỡng
          QuickActionItem(
            title = "Bảo dưỡng",
            icon = Icons.Filled.Build,
            iconBg = PositivusGreen,
            onClick = onNavigateToBooking,
            modifier = Modifier.weight(1f)
          )

          // Item 2: Sửa chữa
          QuickActionItem(
            title = "Sửa chữa",
            icon = Icons.Filled.CarRepair,
            iconBg = PositivusGray,
            onClick = onNavigateToBooking,
            modifier = Modifier.weight(1f)
          )

          // Item 3: Cứu hộ 24/7
          QuickActionItem(
            title = "Cứu hộ 24/7",
            icon = Icons.Filled.Sos,
            iconBg = Color(0xFFFEE2E2),
            iconTint = Color(0xFFDC2626),
            textColor = Color(0xFFDC2626),
            onClick = { /* Call hotline */ },
            modifier = Modifier.weight(1f)
          )

          // Item 4: Lịch sử bảo dưỡng & Hóa đơn
          QuickActionItem(
            title = "Lịch sử",
            icon = Icons.Filled.History,
            iconBg = PositivusGray,
            onClick = onNavigateToServiceHistory,
            modifier = Modifier.weight(1f)
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // Item 5: Tra cứu Bảo hành
          QuickActionItem(
            title = "Bảo hành",
            icon = Icons.Filled.VerifiedUser,
            iconBg = PositivusGreen,
            onClick = onNavigateToWarranty,
            modifier = Modifier.weight(1f)
          )

          // Item 6: Tin nhắn Zalo & SMS
          QuickActionItem(
            title = "Thông báo",
            icon = Icons.Filled.Notifications,
            iconBg = PositivusGray,
            onClick = onNavigateToNotifications,
            modifier = Modifier.weight(1f)
          )

          // Item 7: Cố vấn dịch vụ
          QuickActionItem(
            title = "Tư vấn KTV",
            icon = Icons.Filled.SupportAgent,
            iconBg = PositivusGray,
            onClick = { /* Contact Advisor */ },
            modifier = Modifier.weight(1f)
          )

          // Item 8: Danh bạ xưởng & Bản đồ
          QuickActionItem(
            title = "Bản đồ trạm",
            icon = Icons.Filled.Map,
            iconBg = PositivusGreen,
            onClick = onNavigateToNearbyMap,
            modifier = Modifier.weight(1f)
          )
        }
      }
    }

    // 6. LỊCH HẸN GẦN NHẤT (Brutalist White Card)
    item {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Box(
              modifier = Modifier
                .size(10.dp)
                .background(PositivusGreen)
                .border(1.dp, PositivusBorder)
            )
            Text(
              text = "LỊCH HẸN GẦN NHẤT",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = PositivusDark,
              letterSpacing = 0.8.sp
            )
          }

          Text(
            text = "Xem tất cả",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = PositivusDark,
            modifier = Modifier.clickable { onNavigateToAppointments() }
          )
        }

        BrutalCard(
          modifier = Modifier.fillMaxWidth(),
          cornerRadius = 26.dp,
          contentPadding = PaddingValues(16.dp),
          onClick = onNavigateToAppointments
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            BrutalPill(
              text = "ĐÃ XÁC NHẬN",
              backgroundColor = PositivusGreen,
              textColor = PositivusDark,
              icon = {
                Icon(
                  imageVector = Icons.Filled.CheckCircle,
                  contentDescription = null,
                  tint = PositivusDark,
                  modifier = Modifier.size(14.dp)
                )
              }
            )

            Surface(
              shape = RoundedCornerShape(4.dp),
              color = PositivusGray,
              border = BorderStroke(1.dp, Color(0xFFD1D5DB))
            ) {
              Text(
                text = "IV-2026-889",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = TextGray,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            // Date block TH10 - 09
            Box(
              modifier = Modifier.brutalShadow(
                offsetY = 2.dp,
                color = PositivusBorder,
                cornerRadius = 16.dp
              )
            ) {
              Surface(
                shape = RoundedCornerShape(16.dp),
                color = PositivusDark,
                border = BorderStroke(1.5.dp, PositivusBorder),
                modifier = Modifier.size(width = 56.dp, height = 64.dp)
              ) {
                Column(
                  modifier = Modifier.fillMaxSize(),
                  horizontalAlignment = Alignment.CenterHorizontally,
                  verticalArrangement = Arrangement.Center
                ) {
                  Text(
                    text = "TH10",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = PositivusGreen,
                    letterSpacing = 1.sp
                  )
                  Text(
                    text = "09",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                  )
                }
              }
            }

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Kiểm tra động cơ định kỳ",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = PositivusDark,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(top = 4.dp)
              ) {
                Icon(
                  imageVector = Icons.Filled.Schedule,
                  contentDescription = null,
                  tint = PositivusDark,
                  modifier = Modifier.size(15.dp)
                )
                Text(
                  text = "09:30 AM",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = TextGray
                )
              }
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(top = 2.dp)
              ) {
                Icon(
                  imageVector = Icons.Filled.LocationOn,
                  contentDescription = null,
                  tint = PositivusDark,
                  modifier = Modifier.size(15.dp)
                )
                Text(
                  text = "iVehicle Q.7 • 120 Nguyễn Thị Thập",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Medium,
                  color = TextGray
                )
              }
            }

            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(PositivusDark)
                .border(1.5.dp, PositivusBorder, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Filled.NorthEast,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Static Map Preview Banner
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onNavigateToNearbyMap() }
              .brutalShadow(
                offsetY = 2.dp,
                color = PositivusBorder,
                cornerRadius = 16.dp
              )
          ) {
            Surface(
              shape = RoundedCornerShape(16.dp),
              color = Color(0xFFE5E7EB),
              border = BorderStroke(1.5.dp, PositivusBorder),
              modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
            ) {
              Box(modifier = Modifier.fillMaxSize()) {
                // Static Map Graphic / Snapshot
                AsyncImage(
                  model = "https://lh3.googleusercontent.com/aida-public/AB6AXuDJ6NlCksH6r7G6hR1qD3i5yqBvY8zLhV8YjM7M2R1j1g4w4_c7m2j3k0l9n5b6v7x8z9a0b1c2d3e4f5a6b7c8d9e0f1=s600",
                  contentDescription = "Bản đồ định vị xưởng iVehicle Quận 7",
                  contentScale = ContentScale.Crop,
                  modifier = Modifier.fillMaxSize()
                )

                // Neo-Brutalist Map Pin Badge Overlay
                Surface(
                  shape = RoundedCornerShape(99.dp),
                  color = PositivusDark,
                  border = BorderStroke(1.2.dp, PositivusBorder),
                  modifier = Modifier
                    .align(Alignment.Center)
                    .padding(4.dp)
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Filled.LocationOn,
                      contentDescription = null,
                      tint = PositivusGreen,
                      modifier = Modifier.size(16.dp)
                    )
                    Text(
                      text = "iVehicle Q.7 (Cách 1.4 km)",
                      color = Color.White,
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold
                    )
                  }
                }

                // Quick Navigation Chip (bottom right)
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = PositivusGreen,
                  border = BorderStroke(1.dp, PositivusBorder),
                  modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .clickable { onNavigateToNearbyMap() }
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Filled.Directions,
                      contentDescription = null,
                      tint = PositivusDark,
                      modifier = Modifier.size(13.dp)
                    )
                    Text(
                      text = "Chỉ đường",
                      fontSize = 10.sp,
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

    // 7. Visual Workshop Hero Card (Ảnh xưởng chuẩn ISO)
    item {
      BrutalCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 26.dp,
        contentPadding = PaddingValues(0.dp)
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
        ) {
          AsyncImage(
            model = "https://lh3.googleusercontent.com/aida-public/AB6AXuC6Dk-QhAZOOcqRvOkzK91IKB1WhAt2kfERkix17OOaHGefk5p2MubCW7dsdtmpoAGcUpaaubbJ7B1z_VWnn0g0RxIj5a4zM45gDyOPykBzRjJa2NcgIERBdkaCfWeFaWVnSJCs1zkjAKfeAlVe7JqLrATglbH51kL5sRuURLhdLo1oKDEtuh1DjfjXezOo4JQtajAWE0FfqohhcHikbGjTjAAq4lVIfoM8eYKgdnes",
            contentDescription = "Xưởng dịch vụ đạt chuẩn iVehicle ISO",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
          )

          // Dark Overlay Gradient
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(
                androidx.compose.ui.graphics.Brush.verticalGradient(
                  colors = listOf(Color.Transparent, PositivusDark.copy(alpha = 0.9f)),
                  startY = 40f
                )
              )
          )

          Row(
            modifier = Modifier
              .align(Alignment.BottomStart)
              .fillMaxWidth()
              .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Xưởng dịch vụ đạt chuẩn iVehicle ISO",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
              Text(
                text = "Trang thiết bị hiện đại 2026",
                fontSize = 11.sp,
                color = Color(0xFFD1D5DB)
              )
            }

            BrutalPill(
              text = "4.9/5",
              backgroundColor = PositivusGreen,
              textColor = PositivusDark,
              icon = {
                Icon(
                  imageVector = Icons.Filled.Star,
                  contentDescription = null,
                  tint = PositivusDark,
                  modifier = Modifier.size(13.dp)
                )
              }
            )
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(72.dp)) // Safe padding for bottom bar
    }
  }
}

@Composable
private fun QuickActionItem(
  title: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  iconBg: Color,
  iconTint: Color = PositivusDark,
  textColor: Color = PositivusDark,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier.brutalShadow(
      offsetY = 2.dp,
      color = PositivusBorder,
      cornerRadius = 16.dp
    )
  ) {
    Surface(
      shape = RoundedCornerShape(16.dp),
      color = Color.White,
      border = BorderStroke(1.5.dp, PositivusBorder),
      modifier = Modifier
        .fillMaxWidth()
        .height(84.dp)
        .clickable(onClick = onClick)
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        Box(
          modifier = Modifier
            .size(38.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(iconBg)
            .border(1.dp, PositivusBorder, RoundedCornerShape(12.dp)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = icon,
            contentDescription = title,
            tint = iconTint,
            modifier = Modifier.size(20.dp)
          )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = title,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = textColor,
          maxLines = 1
        )
      }
    }
  }
}
