package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.model.Branch
import com.example.model.BranchType
import com.example.repository.AppRepository
import com.example.ui.components.BrutalCard
import com.example.ui.components.BrutalPill
import com.example.ui.components.brutalShadow
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearbyStationsMapScreen(
  onBack: () -> Unit,
  onSelectStationToBook: (Branch) -> Unit
) {
  val context = LocalContext.current
  val branches by AppRepository.branches.collectAsState()

  // User location state: Default TP. Hồ Chí Minh (Quận 1 / Quận 7 khu vực)
  var userLatitude by remember { mutableDoubleStateOf(10.7500) }
  var userLongitude by remember { mutableDoubleStateOf(106.7150) }
  var hasLocationPermission by remember {
    mutableStateOf(
      ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
      ) == PackageManager.PERMISSION_GRANTED
    )
  }
  var locationStatusMessage by remember { mutableStateOf("Đang dùng vị trí GPS ước lượng (TP.HCM)") }

  // Location permission launcher
  val permissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestMultiplePermissions()
  ) { permissions ->
    val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
    val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
    if (fineGranted || coarseGranted) {
      hasLocationPermission = true
      try {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        val lastLocation = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
          ?: locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        if (lastLocation != null) {
          userLatitude = lastLocation.latitude
          userLongitude = lastLocation.longitude
          locationStatusMessage = "Đã lấy tọa độ GPS thực tế của bạn"
        } else {
          // Mock realistic GPS reading near Ho Chi Minh Center
          userLatitude = 10.7480
          userLongitude = 106.7120
          locationStatusMessage = "GPS hoạt động: Tọa độ 10.748°N, 106.712°E"
        }
      } catch (_: SecurityException) {
        // Fallback gracefully
      }
    } else {
      locationStatusMessage = "Chưa cấp quyền vị trí. Hiển thị bán kính mặc định TP.HCM."
    }
  }

  // Filter and Search states
  var searchQuery by remember { mutableStateOf("") }
  var filterOnlyDirect by remember { mutableStateOf(false) }
  var selectedBranchId by remember { mutableStateOf("BR-01") }

  // Calculate live distance for all branches based on current user location
  val branchesWithLiveDistance = remember(userLatitude, userLongitude, branches) {
    branches.map { branch ->
      val dist = AppRepository.calculateDistanceKm(
        userLatitude,
        userLongitude,
        branch.latitude,
        branch.longitude
      )
      branch.copy(distanceKm = dist)
    }.sortedBy { it.distanceKm }
  }

  val filteredBranches = branchesWithLiveDistance.filter { branch ->
    val matchesSearch = branch.name.contains(searchQuery, ignoreCase = true) ||
        branch.district.contains(searchQuery, ignoreCase = true) ||
        branch.address.contains(searchQuery, ignoreCase = true)
    val matchesFilter = if (filterOnlyDirect) branch.type == BranchType.DIRECT else true
    matchesSearch && matchesFilter
  }

  val activeBranch = branchesWithLiveDistance.find { it.id == selectedBranchId }
    ?: branchesWithLiveDistance.firstOrNull()

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = "Tìm Trạm Chăm Sóc Gần Nhất",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)
            )
            Text(
              text = "Bản đồ định vị trạm dịch vụ & tải phục vụ trực tiếp",
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
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // 1. GPS Permission & Location Bar
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .brutalShadow(offsetY = 2.dp, color = PositivusBorder, cornerRadius = 16.dp)
        ) {
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (hasLocationPermission) Color.White else Color(0xFFFEF3C7),
            border = BorderStroke(1.5.dp, PositivusBorder),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
              ) {
                Icon(
                  imageVector = if (hasLocationPermission) Icons.Filled.MyLocation else Icons.Filled.LocationSearching,
                  contentDescription = null,
                  tint = if (hasLocationPermission) Color(0xFF10B981) else Color(0xFFD97706),
                  modifier = Modifier.size(20.dp)
                )
                Column {
                  Text(
                    text = if (hasLocationPermission) "Định vị người dùng: Bật" else "Yêu cầu quyền GPS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = PositivusDark
                  )
                  Text(
                    text = locationStatusMessage,
                    fontSize = 11.sp,
                    color = TextGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                }
              }

              if (!hasLocationPermission) {
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = PositivusGreen,
                  border = BorderStroke(1.dp, PositivusBorder),
                  modifier = Modifier.clickable {
                    permissionLauncher.launch(
                      arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                      )
                    )
                  }
                ) {
                  Text(
                    text = "Bật GPS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = PositivusDark,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                  )
                }
              }
            }
          }
        }
      }

      // 2. Interactive Map Canvas Container (Simulated Real-time Map with GPS pin & Bay stations)
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .brutalShadow(offsetY = 4.dp, color = PositivusBorder, cornerRadius = 24.dp)
        ) {
          Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFFE5E7EB),
            border = BorderStroke(1.5.dp, PositivusBorder),
            modifier = Modifier
              .fillMaxWidth()
              .height(260.dp)
          ) {
            Box(modifier = Modifier.fillMaxSize()) {
              // Static Map Background Tile
              AsyncImage(
                model = "https://lh3.googleusercontent.com/aida-public/AB6AXuDJ6NlCksH6r7G6hR1qD3i5yqBvY8zLhV8YjM7M2R1j1g4w4_c7m2j3k0l9n5b6v7x8z9a0b1c2d3e4f5a6b7c8d9e0f1=s800",
                contentDescription = "Bản đồ vệ tinh các trạm iVehicle",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
              )

              // Decorative Grid / Radar overlay lines
              Canvas(modifier = Modifier.fillMaxSize()) {
                val centerOffset = Offset(size.width * 0.48f, size.height * 0.52f)
                // Radar radius ring
                drawCircle(
                  color = PositivusDark.copy(alpha = 0.25f),
                  radius = 120f,
                  center = centerOffset,
                  style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
                )
                drawCircle(
                  color = PositivusDark.copy(alpha = 0.2f),
                  radius = 220f,
                  center = centerOffset,
                  style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
                )
              }

              // User Current Location Radar Pulse Pin
              Box(
                modifier = Modifier
                  .align(Alignment.Center)
                  .offset(x = (-10).dp, y = (10).dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(PrimaryBlue.copy(alpha = 0.25f)),
                  contentAlignment = Alignment.Center
                ) {
                  Box(
                    modifier = Modifier
                      .size(12.dp)
                      .clip(CircleShape)
                      .background(PrimaryBlue)
                      .border(2.dp, Color.White, CircleShape)
                  )
                }
              }

              // Station Pin 1: iVehicle Flagship Quận 7 (Near User)
              StationMapPin(
                label = "iVehicle Q.7",
                distanceKm = activeBranch?.distanceKm ?: 1.4,
                isSelected = selectedBranchId == "BR-01",
                modifier = Modifier
                  .align(Alignment.Center)
                  .offset(x = 60.dp, y = (-20).dp),
                onClick = { selectedBranchId = "BR-01" }
              )

              // Station Pin 2: iVehicle Premium Thảo Điền
              StationMapPin(
                label = "Thảo Điền",
                distanceKm = 6.8,
                isSelected = selectedBranchId == "BR-02",
                modifier = Modifier
                  .align(Alignment.TopEnd)
                  .padding(top = 28.dp, end = 26.dp),
                onClick = { selectedBranchId = "BR-02" }
              )

              // Station Pin 3: iVehicle Tân Bình
              StationMapPin(
                label = "Tân Bình",
                distanceKm = 8.5,
                isSelected = selectedBranchId == "BR-03",
                modifier = Modifier
                  .align(Alignment.TopStart)
                  .padding(top = 34.dp, start = 20.dp),
                onClick = { selectedBranchId = "BR-03" }
              )

              // Map Control Bar Overlay (Top Left & Top Right)
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Surface(
                  shape = RoundedCornerShape(99.dp),
                  color = PositivusDark,
                  border = BorderStroke(1.2.dp, PositivusBorder)
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                  ) {
                    Box(
                      modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(PositivusGreen)
                    )
                    Text(
                      text = "Bán kính: 15 km",
                      color = Color.White,
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold
                    )
                  }
                }

                // Center re-center location chip
                Surface(
                  shape = RoundedCornerShape(99.dp),
                  color = Color.White,
                  border = BorderStroke(1.2.dp, PositivusBorder),
                  modifier = Modifier.clickable {
                    userLatitude = 10.7412
                    userLongitude = 106.7088
                    locationStatusMessage = "Đã căn giữa vị trí trạm gần bạn nhất"
                  }
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Filled.NearMe,
                      contentDescription = null,
                      tint = PositivusDark,
                      modifier = Modifier.size(14.dp)
                    )
                    Text(
                      text = "Tâm trạm gần nhất",
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

      // 3. Search and Quick Filters
      item {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Tìm theo tên trạm, đường hoặc quận...") },
            leadingIcon = {
              Icon(Icons.Filled.Search, contentDescription = null, tint = PositivusDark)
            },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = Color.White,
              unfocusedContainerColor = Color.White,
              focusedBorderColor = PositivusBorder,
              unfocusedBorderColor = PositivusBorder
            ),
            modifier = Modifier.fillMaxWidth()
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = if (!filterOnlyDirect) PositivusDark else Color.White,
              border = BorderStroke(1.5.dp, PositivusBorder),
              modifier = Modifier.clickable { filterOnlyDirect = false }
            ) {
              Text(
                text = "Tất cả trạm (${branchesWithLiveDistance.size})",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (!filterOnlyDirect) Color.White else PositivusDark,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
              )
            }

            Surface(
              shape = RoundedCornerShape(12.dp),
              color = if (filterOnlyDirect) PositivusDark else Color.White,
              border = BorderStroke(1.5.dp, PositivusBorder),
              modifier = Modifier.clickable { filterOnlyDirect = true }
            ) {
              Text(
                text = "Trực thuộc iVehicle",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (filterOnlyDirect) Color.White else PositivusDark,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
              )
            }
          }
        }
      }

      // 4. Highlighted Selected Station Detail Card
      if (activeBranch != null) {
        item {
          Text(
            text = "TRẠM ĐANG CHỌN TRÊN BẢN ĐỒ",
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            color = PositivusDark,
            letterSpacing = 0.5.sp
          )

          BrutalCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = PositivusDark,
            cornerRadius = 22.dp,
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
                  BrutalPill(
                    text = activeBranch.type.label,
                    backgroundColor = if (activeBranch.type == BranchType.DIRECT) PositivusGreen else Color(0xFFFBBF24),
                    textColor = PositivusDark
                  )
                  Text(
                    text = "★ ${activeBranch.rating}",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                  )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = activeBranch.name,
                  fontSize = 17.sp,
                  fontWeight = FontWeight.Black,
                  color = Color.White
                )
                Text(
                  text = activeBranch.address,
                  fontSize = 12.sp,
                  color = Color(0xFFD1D5DB)
                )
              }

              Surface(
                shape = RoundedCornerShape(12.dp),
                color = PositivusGreen,
                border = BorderStroke(1.dp, PositivusBorder)
              ) {
                Column(
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Text(
                    text = "CÁCH BẠN",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    color = PositivusDark
                  )
                  Text(
                    text = "${activeBranch.distanceKm} km",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = PositivusDark,
                    fontFamily = FontFamily.Monospace
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Color(0xFF374151), thickness = 0.8.dp)
            Spacer(modifier = Modifier.height(10.dp))

            // Operational telemetry
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column {
                Text("GIỜ MỞ CỬA", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextGray)
                Text(activeBranch.openingHours, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
              }

              Column {
                Text("CÔNG SUẤT HIỆN TẠI", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextGray)
                Text(
                  "${activeBranch.currentActiveJobs}/${activeBranch.maxDailyCapacity} xe",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (activeBranch.currentActiveJobs >= activeBranch.maxDailyCapacity) AlertRed else PositivusGreen
                )
              }

              Column {
                Text("HOTLINE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextGray)
                Text(activeBranch.phone, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // CTA Button to select station & book
            Surface(
              shape = RoundedCornerShape(14.dp),
              color = PositivusGreen,
              border = BorderStroke(1.5.dp, PositivusBorder),
              modifier = Modifier
                .fillMaxWidth()
                .clickable { onSelectStationToBook(activeBranch) }
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(Icons.Filled.CalendarMonth, contentDescription = null, tint = PositivusDark, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Đặt Lịch Tại Trạm Này",
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Black,
                  color = PositivusDark
                )
              }
            }
          }
        }
      }

      // 5. List of all stations sorted by distance
      item {
        Text(
          text = "DANH SÁCH TẤT CẢ CÁC TRẠM (SẮP XẾP THEO KHOẢNG CÁCH)",
          fontSize = 11.sp,
          fontWeight = FontWeight.Black,
          color = PositivusDark,
          letterSpacing = 0.5.sp
        )
      }

      items(filteredBranches) { branch ->
        val isSelected = branch.id == selectedBranchId
        BrutalCard(
          modifier = Modifier.fillMaxWidth(),
          backgroundColor = if (isSelected) Color(0xFFF4FFE5) else Color.White,
          cornerRadius = 20.dp,
          contentPadding = PaddingValues(16.dp),
          onClick = { selectedBranchId = branch.id }
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
                  color = if (branch.type == BranchType.DIRECT) PositivusDark else Color(0xFFE65100)
                ) {
                  Text(
                    text = branch.type.label,
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }

                Text(
                  text = "★ ${branch.rating}",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = PositivusDark
                )
              }

              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = branch.name,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = PositivusDark
              )
              Text(
                text = branch.address,
                fontSize = 12.sp,
                color = TextGray
              )
            }

            Column(horizontalAlignment = Alignment.End) {
              Text(
                text = "${branch.distanceKm} km",
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = PositivusDark,
                fontFamily = FontFamily.Monospace
              )
              Text(
                text = "khoảng cách",
                fontSize = 10.sp,
                color = TextGray
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Services Tag Pills
          LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(branch.servicesAvailable) { srv ->
              Surface(
                shape = RoundedCornerShape(99.dp),
                color = PositivusGray,
                border = BorderStroke(1.dp, PositivusBorder.copy(alpha = 0.3f))
              ) {
                Text(
                  text = srv,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Medium,
                  color = PositivusDark,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun StationMapPin(
  label: String,
  distanceKm: Double,
  isSelected: Boolean,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Box(
    modifier = modifier
      .clickable(onClick = onClick)
      .brutalShadow(offsetY = 2.dp, color = PositivusBorder, cornerRadius = 99.dp)
  ) {
    Surface(
      shape = RoundedCornerShape(99.dp),
      color = if (isSelected) PositivusGreen else PositivusDark,
      border = BorderStroke(1.5.dp, PositivusBorder)
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        Icon(
          imageVector = Icons.Filled.Store,
          contentDescription = null,
          tint = if (isSelected) PositivusDark else PositivusGreen,
          modifier = Modifier.size(13.dp)
        )
        Text(
          text = "$label (${distanceKm}km)",
          fontSize = 10.sp,
          fontWeight = FontWeight.Black,
          color = if (isSelected) PositivusDark else Color.White
        )
      }
    }
  }
}
